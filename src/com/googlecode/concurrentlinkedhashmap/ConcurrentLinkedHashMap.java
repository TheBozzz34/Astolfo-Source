package com.googlecode.concurrentlinkedhashmap;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.GuardedBy;

public final class ConcurrentLinkedHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
   public static final int NCPU = Runtime.getRuntime().availableProcessors();
   public static final int NUMBER_OF_READ_BUFFERS = ceilingNextPowerOfTwo(NCPU);
   public static final int READ_BUFFERS_MASK = NUMBER_OF_READ_BUFFERS - 1;
   public static final int READ_BUFFER_THRESHOLD = 32;
   public static final int READ_BUFFER_DRAIN_THRESHOLD = 64;
   public static final int READ_BUFFER_SIZE = 128;
   public static final int READ_BUFFER_INDEX_MASK = 127;
   public static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
   public static final Queue<?> DISCARDING_QUEUE = new ConcurrentLinkedHashMap.DiscardingQueue();
   public final ConcurrentMap<K, ConcurrentLinkedHashMap.Node<K, V>> data;
   public final int concurrencyLevel;
   @GuardedBy("evictionLock")
   public final long[] readBufferReadCount;
   @GuardedBy("evictionLock")
   public final LinkedDeque<ConcurrentLinkedHashMap.Node<K, V>> evictionDeque;
   @GuardedBy("evictionLock")
   public final AtomicLong weightedSize;
   @GuardedBy("evictionLock")
   public final AtomicLong capacity;
   public final Lock evictionLock;
   public final Queue<Runnable> writeBuffer;
   public final AtomicLong[] readBufferWriteCount;
   public final AtomicLong[] readBufferDrainAtWriteCount;
   public final AtomicReference<ConcurrentLinkedHashMap.Node<K, V>>[][] readBuffers;
   public final AtomicReference<ConcurrentLinkedHashMap.DrainStatus> drainStatus;
   public final EntryWeigher<? super K, ? super V> weigher;
   public final Queue<ConcurrentLinkedHashMap.Node<K, V>> pendingNotifications;
   public final EvictionListener<K, V> listener;
   public transient Set<K> keySet;
   public transient Collection<V> values;
   public transient Set<Entry<K, V>> entrySet;
   public static final long serialVersionUID = 1L;

   public static int ceilingNextPowerOfTwo(int x) {
      return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
   }

   public ConcurrentLinkedHashMap(ConcurrentLinkedHashMap.Builder<K, V> builder) {
      this.concurrencyLevel = builder.concurrencyLevel;
      this.capacity = new AtomicLong(Math.min(builder.capacity, 9223372034707292160L));
      this.data = new ConcurrentHashMap<>(builder.initialCapacity, 0.75F, this.concurrencyLevel);
      this.weigher = builder.weigher;
      this.evictionLock = new ReentrantLock();
      this.weightedSize = new AtomicLong();
      this.evictionDeque = new LinkedDeque<>();
      this.writeBuffer = new ConcurrentLinkedQueue<>();
      this.drainStatus = new AtomicReference<>(ConcurrentLinkedHashMap.DrainStatus.IDLE);
      this.readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
      this.readBufferWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
      this.readBufferDrainAtWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
      this.readBuffers = new AtomicReference[NUMBER_OF_READ_BUFFERS][128];

      for (int i = 0; i < NUMBER_OF_READ_BUFFERS; i++) {
         this.readBufferWriteCount[i] = new AtomicLong();
         this.readBufferDrainAtWriteCount[i] = new AtomicLong();
         this.readBuffers[i] = new AtomicReference[128];

         for (int j = 0; j < 128; j++) {
            this.readBuffers[i][j] = new AtomicReference<>();
         }
      }

      this.listener = builder.listener;
      this.pendingNotifications = this.listener == ConcurrentLinkedHashMap.DiscardingListener.INSTANCE ? (java.util.Queue)(DISCARDING_QUEUE) : new ConcurrentLinkedQueue<>();
   }

   public static void checkNotNull(Object o) {
      if (o == null) {
         throw new NullPointerException();
      }
   }

   public static void checkArgument(boolean expression) {
      if (!expression) {
         throw new IllegalArgumentException();
      }
   }

   public static void checkState(boolean expression) {
      if (!expression) {
         throw new IllegalStateException();
      }
   }

   public long capacity() {
      return this.capacity.get();
   }

   public void setCapacity(long capacity) {
      checkArgument(capacity >= 0L);
      this.evictionLock.lock();

      try {
         this.capacity.lazySet(Math.min(capacity, 9223372034707292160L));
         this.drainBuffers();
         this.evict();
      } finally {
         this.evictionLock.unlock();
      }

      this.notifyListener();
   }

   @GuardedBy("evictionLock")
   public boolean hasOverflowed() {
      return this.weightedSize.get() > this.capacity.get();
   }

   @GuardedBy("evictionLock")
   public void evict() {
      while (this.hasOverflowed()) {
         ConcurrentLinkedHashMap.Node node = this.evictionDeque.poll();
         if (node == null) {
            return;
         }

         if (this.data.remove(node.key, node)) {
            this.pendingNotifications.add(node);
         }

         this.makeDead(node);
      }
   }

   public void afterRead(ConcurrentLinkedHashMap.Node<K, V> node) {
      int bufferIndex = readBufferIndex();
      long writeCount = this.recordRead(bufferIndex, node);
      this.drainOnReadIfNeeded(bufferIndex, writeCount);
      this.notifyListener();
   }

   public static int readBufferIndex() {
      return (int)Thread.currentThread().getId() & READ_BUFFERS_MASK;
   }

   public long recordRead(int bufferIndex, ConcurrentLinkedHashMap.Node<K, V> node) {
      AtomicLong counter = this.readBufferWriteCount[bufferIndex];
      long writeCount = counter.get();
      counter.lazySet(writeCount + 1L);
      int index = (int)(writeCount & 127L);
      this.readBuffers[bufferIndex][index].lazySet(node);
      return writeCount;
   }

   public void drainOnReadIfNeeded(int bufferIndex, long writeCount) {
      long l = writeCount - this.readBufferDrainAtWriteCount[bufferIndex].get();
      boolean bl = l < 32L;
      ConcurrentLinkedHashMap.DrainStatus drainStatus2 = this.drainStatus.get();
      if (drainStatus2.shouldDrainBuffers(bl)) {
         this.tryToDrainBuffers();
      }
   }

   public void afterWrite(Runnable task) {
      this.writeBuffer.add(task);
      this.drainStatus.lazySet(ConcurrentLinkedHashMap.DrainStatus.REQUIRED);
      this.tryToDrainBuffers();
      this.notifyListener();
   }

   public void tryToDrainBuffers() {
      if (this.evictionLock.tryLock()) {
         try {
            this.drainStatus.lazySet(ConcurrentLinkedHashMap.DrainStatus.PROCESSING);
            this.drainBuffers();
         } finally {
            this.drainStatus.compareAndSet(ConcurrentLinkedHashMap.DrainStatus.PROCESSING, ConcurrentLinkedHashMap.DrainStatus.IDLE);
            this.evictionLock.unlock();
         }
      }
   }

   @GuardedBy("evictionLock")
   public void drainBuffers() {
      this.drainReadBuffers();
      this.drainWriteBuffer();
   }

   @GuardedBy("evictionLock")
   public void drainReadBuffers() {
      int i = (int)Thread.currentThread().getId();
      int j = i + NUMBER_OF_READ_BUFFERS;

      for (int k = i; k < j; k++) {
         this.drainReadBuffer(k & READ_BUFFERS_MASK);
      }
   }

   @GuardedBy("evictionLock")
   public void drainReadBuffer(int bufferIndex) {
      long l = this.readBufferWriteCount[bufferIndex].get();

      for (int i = 0; i < 64; i++) {
         int j = (int)(this.readBufferReadCount[bufferIndex] & 127L);
         AtomicReference atomicReference = this.readBuffers[bufferIndex][j];
         ConcurrentLinkedHashMap.Node node = (ConcurrentLinkedHashMap.Node)atomicReference.get();
         if (node == null) {
            break;
         }

         atomicReference.lazySet(null);
         this.applyRead(node);
         this.readBufferReadCount[bufferIndex]++;
      }

      this.readBufferDrainAtWriteCount[bufferIndex].lazySet(l);
   }

   @GuardedBy("evictionLock")
   public void applyRead(ConcurrentLinkedHashMap.Node<K, V> node) {
      if (this.evictionDeque.contains(node)) {
         this.evictionDeque.moveToBack(node);
      }
   }

   @GuardedBy("evictionLock")
   public void drainWriteBuffer() {
      for (int i = 0; i < 16; i++) {
         Runnable runnable = this.writeBuffer.poll();
         if (runnable == null) {
            break;
         }

         runnable.run();
      }
   }

   public boolean tryToRetire(ConcurrentLinkedHashMap.Node<K, V> node, ConcurrentLinkedHashMap.WeightedValue<V> expect) {
      if (expect.isAlive()) {
         ConcurrentLinkedHashMap.WeightedValue weightedValue = new ConcurrentLinkedHashMap.WeightedValue(expect.value, -expect.weight);
         return node.compareAndSet(expect, weightedValue);
      } else {
         return false;
      }
   }

   public void makeRetired(ConcurrentLinkedHashMap.Node<K, V> node) {
      ConcurrentLinkedHashMap.WeightedValue weightedValue;
      ConcurrentLinkedHashMap.WeightedValue weightedValue2;
      do {
         weightedValue = (ConcurrentLinkedHashMap.WeightedValue)node.get();
         if (!weightedValue.isAlive()) {
            return;
         }

         weightedValue2 = new ConcurrentLinkedHashMap.WeightedValue(weightedValue.value, -weightedValue.weight);
      } while (!node.compareAndSet(weightedValue, weightedValue2));
   }

   @GuardedBy("evictionLock")
   public void makeDead(ConcurrentLinkedHashMap.Node<K, V> node) {
      ConcurrentLinkedHashMap.WeightedValue weightedValue;
      ConcurrentLinkedHashMap.WeightedValue weightedValue2;
      do {
         weightedValue = (ConcurrentLinkedHashMap.WeightedValue)node.get();
         weightedValue2 = new ConcurrentLinkedHashMap.WeightedValue(weightedValue.value, 0);
      } while (!node.compareAndSet(weightedValue, weightedValue2));

      this.weightedSize.lazySet(this.weightedSize.get() - Math.abs(weightedValue.weight));
   }

   public void notifyListener() {
      ConcurrentLinkedHashMap.Node node;
      while ((node = this.pendingNotifications.poll()) != null) {
         this.listener.onEviction((K)(node.key), (V)node.getValue());
      }
   }

   @Override
   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   @Override
   public int size() {
      return this.data.size();
   }

   public long weightedSize() {
      return Math.max(0L, this.weightedSize.get());
   }

   @Override
   public void clear() {
      this.evictionLock.lock();

      try {
         ConcurrentLinkedHashMap.Node node;
         while ((node = this.evictionDeque.poll()) != null) {
            this.data.remove(node.key, node);
            this.makeDead(node);
         }

         for (AtomicReference[] atomicReferences : this.readBuffers) {
            for (AtomicReference atomicReference : atomicReferences) {
               atomicReference.lazySet(null);
            }
         }

         Runnable runnable;
         while ((runnable = this.writeBuffer.poll()) != null) {
            runnable.run();
         }
      } finally {
         this.evictionLock.unlock();
      }
   }

   @Override
   public boolean containsKey(Object key) {
      return this.data.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      checkNotNull(value);

      for (ConcurrentLinkedHashMap.Node node : this.data.values()) {
         if (node.getValue().equals(value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public V get(Object key) {
      ConcurrentLinkedHashMap.Node node = this.data.get(key);
      if (node == null) {
         return null;
      }

      this.afterRead(node);
      return (V)node.getValue();
   }

   public V getQuietly(Object key) {
      ConcurrentLinkedHashMap.Node node = this.data.get(key);
      return (V)(node == null ? null : node.getValue());
   }

   @Override
   public V put(K key, V value) {
      return this.put(key, value, false);
   }

   @Override
   public V putIfAbsent(K key, V value) {
      return this.put(key, value, true);
   }

   public V put(K key, V value, boolean onlyIfAbsent) {
      checkNotNull(key);
      checkNotNull(value);
      int i = this.weigher.weightOf((K)key, (V)value);
      ConcurrentLinkedHashMap.WeightedValue weightedValue = new ConcurrentLinkedHashMap.WeightedValue<>(value, i);
      ConcurrentLinkedHashMap.Node node = new ConcurrentLinkedHashMap.Node<>(key, weightedValue);

      label27:
      while (true) {
         ConcurrentLinkedHashMap.Node node2 = this.data.putIfAbsent((K)(node.key), node);
         if (node2 == null) {
            this.afterWrite(new ConcurrentLinkedHashMap.AddTask(this, node, i));
            return null;
         }

         if (onlyIfAbsent) {
            this.afterRead(node2);
            return (V)node2.getValue();
         }

         ConcurrentLinkedHashMap.WeightedValue weightedValue2;
         do {
            weightedValue2 = (ConcurrentLinkedHashMap.WeightedValue)node2.get();
            if (!weightedValue2.isAlive()) {
               continue label27;
            }
         } while (!node2.compareAndSet(weightedValue2, weightedValue));

         int j = i - weightedValue2.weight;
         if (j == 0) {
            this.afterRead(node2);
         } else {
            this.afterWrite(new ConcurrentLinkedHashMap.UpdateTask(this, node2, j));
         }

         return (V)(weightedValue2.value);
      }
   }

   @Override
   public V remove(Object key) {
      ConcurrentLinkedHashMap.Node node = this.data.remove(key);
      if (node == null) {
         return null;
      }

      this.makeRetired(node);
      this.afterWrite(new ConcurrentLinkedHashMap.RemovalTask(this, node));
      return (V)node.getValue();
   }

   @Override
   public boolean remove(Object key, Object value) {
      ConcurrentLinkedHashMap.Node node = this.data.get(key);
      if (node != null && value != null) {
         ConcurrentLinkedHashMap.WeightedValue weightedValue = (ConcurrentLinkedHashMap.WeightedValue)node.get();

         while (weightedValue.contains(value)) {
            if (this.tryToRetire(node, weightedValue)) {
               if (this.data.remove(key, node)) {
                  this.afterWrite(new ConcurrentLinkedHashMap.RemovalTask(this, node));
                  return true;
               }
               break;
            }

            weightedValue = (ConcurrentLinkedHashMap.WeightedValue)node.get();
            if (!weightedValue.isAlive()) {
               break;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public V replace(K key, V value) {
      checkNotNull(key);
      checkNotNull(value);
      int i = this.weigher.weightOf((K)key, (V)value);
      ConcurrentLinkedHashMap.WeightedValue weightedValue = new ConcurrentLinkedHashMap.WeightedValue<>(value, i);
      ConcurrentLinkedHashMap.Node node = this.data.get(key);
      if (node == null) {
         return null;
      }

      ConcurrentLinkedHashMap.WeightedValue weightedValue2;
      do {
         weightedValue2 = (ConcurrentLinkedHashMap.WeightedValue)node.get();
         if (!weightedValue2.isAlive()) {
            return null;
         }
      } while (!node.compareAndSet(weightedValue2, weightedValue));

      int j = i - weightedValue2.weight;
      if (j == 0) {
         this.afterRead(node);
      } else {
         this.afterWrite(new ConcurrentLinkedHashMap.UpdateTask(this, node, j));
      }

      return (V)(weightedValue2.value);
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) {
      checkNotNull(key);
      checkNotNull(oldValue);
      checkNotNull(newValue);
      int i = this.weigher.weightOf((K)key, (V)newValue);
      ConcurrentLinkedHashMap.WeightedValue weightedValue = new ConcurrentLinkedHashMap.WeightedValue<>(newValue, i);
      ConcurrentLinkedHashMap.Node node = this.data.get(key);
      if (node == null) {
         return false;
      }

      ConcurrentLinkedHashMap.WeightedValue weightedValue2;
      do {
         weightedValue2 = (ConcurrentLinkedHashMap.WeightedValue)node.get();
         if (!weightedValue2.isAlive() || !weightedValue2.contains(oldValue)) {
            return false;
         }
      } while (!node.compareAndSet(weightedValue2, weightedValue));

      int j = i - weightedValue2.weight;
      if (j == 0) {
         this.afterRead(node);
      } else {
         this.afterWrite(new ConcurrentLinkedHashMap.UpdateTask(this, node, j));
      }

      return true;
   }

   @Override
   public Set<K> keySet() {
      Set set = this.keySet;
      return set == null ? (this.keySet = new ConcurrentLinkedHashMap.ClhmKeySet()) : set;
   }

   public Set<K> I_Set() {
      return this.ascendingKeySetWithLimit(Integer.MAX_VALUE);
   }

   public Set<K> ascendingKeySetWithLimit(int limit) {
      return this.orderedKeySet(true, limit);
   }

   public Set<K> descendingKeySet() {
      return this.descendingKeySetWithLimit(Integer.MAX_VALUE);
   }

   public Set<K> descendingKeySetWithLimit(int limit) {
      return this.orderedKeySet(false, limit);
   }

   public Set<K> orderedKeySet(boolean ascending, int limit) {
      checkArgument(limit >= 0);
      this.evictionLock.lock();

      try {
         this.drainBuffers();
         int i = this.weigher == Weighers.entrySingleton() ? Math.min(limit, (int)this.weightedSize()) : 16;
         LinkedHashSet linkedHashSet = new LinkedHashSet(i);
         Iterator iterator2 = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();

         while (iterator2.hasNext() && limit > linkedHashSet.size()) {
            linkedHashSet.add(((ConcurrentLinkedHashMap.Node)iterator2.next()).key);
         }

         return Collections.unmodifiableSet(linkedHashSet);
      } finally {
         this.evictionLock.unlock();
      }
   }

   @Override
   public Collection<V> values() {
      Collection collection = this.values;
      return collection == null ? (this.values = new ConcurrentLinkedHashMap.ClhmValues()) : collection;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      Set set = this.entrySet;
      return set == null ? (this.entrySet = new ConcurrentLinkedHashMap.ClhmEntrySet()) : set;
   }

   public Map<K, V> ascendingMap() {
      return this.ascendingMapWithLimit(Integer.MAX_VALUE);
   }

   public Map<K, V> ascendingMapWithLimit(int limit) {
      return this.orderedMap(true, limit);
   }

   public Map<K, V> descendingMap() {
      return this.descendingMapWithLimit(Integer.MAX_VALUE);
   }

   public Map<K, V> descendingMapWithLimit(int limit) {
      return this.orderedMap(false, limit);
   }

   public Map<K, V> orderedMap(boolean ascending, int limit) {
      checkArgument(limit >= 0);
      this.evictionLock.lock();

      try {
         this.drainBuffers();
         int i = this.weigher == Weighers.entrySingleton() ? Math.min(limit, (int)this.weightedSize()) : 16;
         LinkedHashMap linkedHashMap = new LinkedHashMap(i);
         Iterator iterator2 = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();

         while (iterator2.hasNext() && limit > linkedHashMap.size()) {
            ConcurrentLinkedHashMap.Node node = (ConcurrentLinkedHashMap.Node)iterator2.next();
            linkedHashMap.put(node.key, node.getValue());
         }

         return Collections.unmodifiableMap(linkedHashMap);
      } finally {
         this.evictionLock.unlock();
      }
   }

   public Object writeReplace() {
      return new ConcurrentLinkedHashMap.SerializationProxy<>(this);
   }

   public static final class AddTask implements Runnable {
      public final ConcurrentLinkedHashMap.Node node;
      public final int weight;
      public final ConcurrentLinkedHashMap map;

      public AddTask(ConcurrentLinkedHashMap concurrentLinkedHashMap, ConcurrentLinkedHashMap.Node node, int weight) {
         this.map = concurrentLinkedHashMap;
         this.weight = weight;
         this.node = node;
      }

      @GuardedBy("evictionLock")
      @Override
      public void run() {
         this.map.weightedSize.lazySet(this.map.weightedSize.get() + this.weight);
         if (((ConcurrentLinkedHashMap.WeightedValue)this.node.get()).isAlive()) {
            this.map.evictionDeque.add(this.node);
            this.map.evict();
         }
      }
   }

   public static final class BoundedEntryWeigher<K, V> implements EntryWeigher<K, V>, Serializable {
      public static final long serialVersionUID = 1L;
      public final EntryWeigher<? super K, ? super V> weigher;

      public BoundedEntryWeigher(EntryWeigher<? super K, ? super V> weigherIn) {
         ConcurrentLinkedHashMap.checkNotNull(weigherIn);
         this.weigher = weigherIn;
      }

      @Override
      public int weightOf(K key, V value) {
         int i = this.weigher.weightOf((K)key, (V)value);
         ConcurrentLinkedHashMap.checkArgument(i >= 1);
         return i;
      }

      public Object writeReplace() {
         return this.weigher;
      }
   }

   public static final class Builder<K, V> {
      public static final int DEFAULT_CONCURRENCY_LEVEL = 16;
      public static final int DEFAULT_INITIAL_CAPACITY = 16;
      public EvictionListener<K, V> listener;
      public EntryWeigher<? super K, ? super V> weigher;
      public int concurrencyLevel;
      public int initialCapacity;
      public long capacity = -1L;

      public Builder() {
         this.weigher = Weighers.entrySingleton();
         this.initialCapacity = 16;
         this.concurrencyLevel = 16;
         this.listener = (com.googlecode.concurrentlinkedhashmap.EvictionListener)(ConcurrentLinkedHashMap.DiscardingListener.INSTANCE);
      }

      public ConcurrentLinkedHashMap.Builder<K, V> initialCapacity(int initialCapacity) {
         ConcurrentLinkedHashMap.checkArgument(initialCapacity >= 0);
         this.initialCapacity = initialCapacity;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> maximumWeightedCapacity(long capacityIn) {
         ConcurrentLinkedHashMap.checkArgument(capacityIn >= 0L);
         this.capacity = capacityIn;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> concurrencyLevel(int concurrencyLevelIn) {
         ConcurrentLinkedHashMap.checkArgument(concurrencyLevelIn > 0);
         this.concurrencyLevel = concurrencyLevelIn;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> listener(EvictionListener<K, V> listenerIn) {
         ConcurrentLinkedHashMap.checkNotNull(listenerIn);
         this.listener = listenerIn;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> weigher(Weigher<? super V> weigherIn) {
         this.weigher = weigherIn == Weighers.singleton()
            ? Weighers.entrySingleton()
            : new ConcurrentLinkedHashMap.BoundedEntryWeigher<>(Weighers.asEntryWeigher(weigherIn));
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> weigher(EntryWeigher<? super K, ? super V> weigherIn) {
         this.weigher = weigherIn == Weighers.entrySingleton() ? Weighers.entrySingleton() : new ConcurrentLinkedHashMap.BoundedEntryWeigher<>(weigherIn);
         return this;
      }

      public ConcurrentLinkedHashMap<K, V> build() {
         ConcurrentLinkedHashMap.checkState(this.capacity >= 0L);
         return new ConcurrentLinkedHashMap<>(this);
      }
   }

   public final class ClhmEntrySet extends AbstractSet<Entry<K, V>> {
      public final ConcurrentLinkedHashMap<K, V> map = ConcurrentLinkedHashMap.this;

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public void clear() {
         this.map.clear();
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return (java.util.Iterator)(new ConcurrentLinkedHashMap.EntryIterator(ConcurrentLinkedHashMap.this));
      }

      @Override
      public boolean contains(Object object) {
         if (!(object instanceof Entry)) {
            return false;
         }

         Entry entry = (Entry)object;
         ConcurrentLinkedHashMap.Node node = this.map.data.get(entry.getKey());
         return node != null && node.getValue().equals(entry.getValue());
      }

      public boolean add(Entry<K, V> entry) {
         return this.map.putIfAbsent((K)entry.getKey(), (V)entry.getValue()) == null;
      }

      @Override
      public boolean remove(Object object) {
         if (!(object instanceof Entry)) {
            return false;
         }

         Entry entry = (Entry)object;
         return this.map.remove(entry.getKey(), entry.getValue());
      }
   }

   public final class ClhmKeySet extends AbstractSet<K> {
      public final ConcurrentLinkedHashMap<K, V> map = ConcurrentLinkedHashMap.this;

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public void clear() {
         this.map.clear();
      }

      @Override
      public Iterator<K> iterator() {
         return new ConcurrentLinkedHashMap.KeyIterator(ConcurrentLinkedHashMap.this);
      }

      @Override
      public boolean contains(Object object) {
         return ConcurrentLinkedHashMap.this.containsKey(object);
      }

      @Override
      public boolean remove(Object object) {
         return this.map.remove(object) != null;
      }

      @Override
      public Object[] toArray() {
         return this.map.data.keySet().toArray();
      }

      @Override
      public <T> T[] toArray(T[] objects) {
         return (T[])this.map.data.keySet().toArray(objects);
      }
   }

   public final class ClhmValues extends AbstractCollection<V> {
      @Override
      public int size() {
         return ConcurrentLinkedHashMap.this.size();
      }

      @Override
      public void clear() {
         ConcurrentLinkedHashMap.this.clear();
      }

      @Override
      public Iterator<V> iterator() {
         return new ConcurrentLinkedHashMap.ValueIterator(ConcurrentLinkedHashMap.this);
      }

      @Override
      public boolean contains(Object object) {
         return ConcurrentLinkedHashMap.this.containsValue(object);
      }
   }

   public enum DiscardingListener implements EvictionListener<Object, Object> {
      INSTANCE;

      @Override
      public void onEviction(Object key, Object value) {
      }
   }

   public static final class DiscardingQueue extends AbstractQueue<Object> {
      @Override
      public boolean add(Object e) {
         return true;
      }

      @Override
      public boolean offer(Object e) {
         return true;
      }

      @Override
      public Object poll() {
         return null;
      }

      @Override
      public Object peek() {
         return null;
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public Iterator<Object> iterator() {
         return Collections.<Object>emptyList().iterator();
      }
   }

   public enum DrainStatus {
      IDLE {
         @Override
         public boolean shouldDrainBuffers(boolean delayable) {
            return !delayable;
         }
      },
      REQUIRED {
         @Override
         public boolean shouldDrainBuffers(boolean delayable) {
            return true;
         }
      },
      PROCESSING {
         @Override
         public boolean shouldDrainBuffers(boolean delayable) {
            return false;
         }
      };

      DrainStatus() {
      }

      public abstract boolean shouldDrainBuffers(boolean bl);
   }

   public static final class EntryIterator implements Iterator<Entry> {
      public final Iterator iterator;
      public ConcurrentLinkedHashMap.Node current;
      public final ConcurrentLinkedHashMap map;

      public EntryIterator(ConcurrentLinkedHashMap concurrentLinkedHashMap) {
         this.map = concurrentLinkedHashMap;
         this.iterator = this.map.data.values().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Entry next() {
         this.current = (ConcurrentLinkedHashMap.Node)this.iterator.next();
         return new WriteThroughEntry(this.map, this.current);
      }

      @Override
      public void remove() {
         ConcurrentLinkedHashMap.checkState(this.current != null);
         this.map.remove(this.current.key);
         this.current = null;
      }
   }

   public static final class KeyIterator implements Iterator {
      public final Iterator iterator;
      public Object current;
      public final ConcurrentLinkedHashMap x;

      public KeyIterator(ConcurrentLinkedHashMap concurrentLinkedHashMap) {
         this.x = concurrentLinkedHashMap;
         this.iterator = this.x.data.keySet().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Override
      public Object next() {
         this.current = this.iterator.next();
         return this.current;
      }

      @Override
      public void remove() {
         ConcurrentLinkedHashMap.checkState(this.current != null);
         this.x.remove(this.current);
         this.current = null;
      }
   }

   public static final class Node<K, V> extends AtomicReference<ConcurrentLinkedHashMap.WeightedValue<V>> implements Linked<ConcurrentLinkedHashMap.Node<K, V>> {
      public final K key;
      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> prev;
      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> next;

      public Node(K key, ConcurrentLinkedHashMap.WeightedValue<V> weightedValue) {
         super(weightedValue);
         this.key = key;
      }

      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> getPrevious() {
         return this.prev;
      }

      @GuardedBy("evictionLock")
      public void setPrevious(ConcurrentLinkedHashMap.Node<K, V> prev) {
         this.prev = prev;
      }

      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> getNext() {
         return this.next;
      }

      @GuardedBy("evictionLock")
      public void setNext(ConcurrentLinkedHashMap.Node<K, V> nextIn) {
         this.next = nextIn;
      }

      public V getValue() {
         return this.get().value;
      }
   }

   public static final class RemovalTask implements Runnable {
      public final ConcurrentLinkedHashMap.Node node;
      public final ConcurrentLinkedHashMap map;

      public RemovalTask(ConcurrentLinkedHashMap concurrentLinkedHashMap, ConcurrentLinkedHashMap.Node node) {
         this.map = concurrentLinkedHashMap;
         this.node = node;
      }

      @GuardedBy("evictionLock")
      @Override
      public void run() {
         this.map.evictionDeque.remove(this.node);
         this.map.makeDead(this.node);
      }
   }

   public static final class SerializationProxy<K, V> implements Serializable {
      public final EntryWeigher<? super K, ? super V> weigher;
      public final EvictionListener<K, V> listener;
      public final int concurrencyLevel;
      public final Map<K, V> data;
      public final long capacity;
      public static final long serialVersionUID = 1L;

      public SerializationProxy(ConcurrentLinkedHashMap<K, V> map) {
         this.concurrencyLevel = map.concurrencyLevel;
         this.data = new HashMap<>(map);
         this.capacity = map.capacity.get();
         this.listener = map.listener;
         this.weigher = map.weigher;
      }

      public Object readResolve() {
         ConcurrentLinkedHashMap<K, V> map = new ConcurrentLinkedHashMap.Builder<K, V>()
            .concurrencyLevel(this.concurrencyLevel)
            .maximumWeightedCapacity(this.capacity)
            .listener(this.listener)
            .weigher(this.weigher)
            .build();
         map.putAll(this.data);
         return map;
      }
   }

   public static final class UpdateTask implements Runnable {
      public final int weightDifference;
      public final ConcurrentLinkedHashMap.Node node;
      public final ConcurrentLinkedHashMap map;

      public UpdateTask(ConcurrentLinkedHashMap concurrentLinkedHashMap, ConcurrentLinkedHashMap.Node node, int weightDifference) {
         this.map = concurrentLinkedHashMap;
         this.weightDifference = weightDifference;
         this.node = node;
      }

      @GuardedBy("evictionLock")
      @Override
      public void run() {
         this.map.weightedSize.lazySet(this.map.weightedSize.get() + this.weightDifference);
         this.map.applyRead(this.node);
         this.map.evict();
      }
   }

   public static final class ValueIterator implements Iterator {
      public final Iterator iterator;
      public ConcurrentLinkedHashMap.Node current;
      public final ConcurrentLinkedHashMap map;

      public ValueIterator(ConcurrentLinkedHashMap concurrentLinkedHashMap) {
         this.map = concurrentLinkedHashMap;
         this.iterator = this.map.data.values().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Override
      public Object next() {
         this.current = (ConcurrentLinkedHashMap.Node)this.iterator.next();
         return this.current.getValue();
      }

      @Override
      public void remove() {
         ConcurrentLinkedHashMap.checkState(this.current != null);
         this.map.remove(this.current.key);
         this.current = null;
      }
   }

   public static final class WeightedValue<V> {
      public final int weight;
      public final V value;

      public WeightedValue(V value, int weight) {
         this.weight = weight;
         this.value = value;
      }

      public boolean contains(Object o) {
         return o == this.value || this.value.equals(o);
      }

      public boolean isAlive() {
         return this.weight > 0;
      }

      public boolean isRetired() {
         return this.weight < 0;
      }

      public boolean isDead() {
         return this.weight == 0;
      }
   }
}
