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
   public static final long y = 9223372034707292160L;
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
   private static final long d = 125362754432684L;

   public static int ceilingNextPowerOfTwo(int var0) {
      return 1 << 32 - Integer.numberOfLeadingZeros(var0 - 1);
   }

   public ConcurrentLinkedHashMap(ConcurrentLinkedHashMap.Builder<K, V> var1) {
      this.concurrencyLevel = var1.concurrencyLevel;
      this.capacity = new AtomicLong(Math.min(var1.capacity, 9223372034707292160L));
      this.data = new ConcurrentHashMap<>(var1.initialCapacity, 0.75F, this.concurrencyLevel);
      this.weigher = var1.weigher;
      this.evictionLock = new ReentrantLock();
      this.weightedSize = new AtomicLong();
      this.evictionDeque = new LinkedDeque<>();
      this.writeBuffer = new ConcurrentLinkedQueue<>();
      this.drainStatus = new AtomicReference<>(ConcurrentLinkedHashMap.DrainStatus.IDLE);
      this.readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
      this.readBufferWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
      this.readBufferDrainAtWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
      this.readBuffers = new AtomicReference[NUMBER_OF_READ_BUFFERS][128];

      for (int var5 = 0; var5 < NUMBER_OF_READ_BUFFERS; var5++) {
         this.readBufferWriteCount[var5] = new AtomicLong();
         this.readBufferDrainAtWriteCount[var5] = new AtomicLong();
         this.readBuffers[var5] = new AtomicReference[128];

         for (int var6 = 0; var6 < 128; var6++) {
            this.readBuffers[var5][var6] = new AtomicReference<>();
         }
      }

      this.listener = var1.listener;
      this.pendingNotifications = this.listener == ConcurrentLinkedHashMap.DiscardingListener.INSTANCE ? (java.util.Queue)(DISCARDING_QUEUE) : new ConcurrentLinkedQueue<>();
   }

   public static void checkNotNull(Object var0) {
      if (var0 == null) {
         throw new NullPointerException();
      }
   }

   public static void checkArgument(boolean var0) {
      if (!var0) {
         throw new IllegalArgumentException();
      }
   }

   public static void checkState(boolean var0) {
      if (!var0) {
         throw new IllegalStateException();
      }
   }

   public long capacity() {
      return this.capacity.get();
   }

   public void setCapacity(long var1) {
      checkArgument(var1 >= 0L);
      this.evictionLock.lock();

      try {
         this.capacity.lazySet(Math.min(var1, 9223372034707292160L));
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
         ConcurrentLinkedHashMap.Node var4 = this.evictionDeque.poll();
         if (var4 == null) {
            return;
         }

         if (this.data.remove(var4.key, var4)) {
            this.pendingNotifications.add(var4);
         }

         this.makeDead(var4);
      }
   }

   public void afterRead(ConcurrentLinkedHashMap.Node<K, V> var1) {
      int var2 = readBufferIndex();
      long var3 = this.recordRead(var2, var1);
      this.drainOnReadIfNeeded(var2, var3);
      this.notifyListener();
   }

   public static int readBufferIndex() {
      return (int)Thread.currentThread().getId() & READ_BUFFERS_MASK;
   }

   public long recordRead(int var1, ConcurrentLinkedHashMap.Node<K, V> var2) {
      AtomicLong var3 = this.readBufferWriteCount[var1];
      long var4 = var3.get();
      var3.lazySet(var4 + 1L);
      int var6 = (int)(var4 & 127L);
      this.readBuffers[var1][var6].lazySet(var2);
      return var4;
   }

   public void drainOnReadIfNeeded(int var1, long var2) {
      long var7 = var2 - this.readBufferDrainAtWriteCount[var1].get();
      boolean var9 = var7 < 32L;
      ConcurrentLinkedHashMap.DrainStatus var10 = this.drainStatus.get();
      if (var10.shouldDrainBuffers(var9)) {
         this.tryToDrainBuffers();
      }
   }

   public void afterWrite(Runnable var1) {
      this.writeBuffer.add(var1);
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
      int var4 = (int)Thread.currentThread().getId();
      int var5 = var4 + NUMBER_OF_READ_BUFFERS;

      for (int var6 = var4; var6 < var5; var6++) {
         this.drainReadBuffer(var6 & READ_BUFFERS_MASK);
      }
   }

   @GuardedBy("evictionLock")
   public void drainReadBuffer(int var1) {
      long var5 = this.readBufferWriteCount[var1].get();

      for (int var7 = 0; var7 < 64; var7++) {
         int var8 = (int)(this.readBufferReadCount[var1] & 127L);
         AtomicReference var9 = this.readBuffers[var1][var8];
         ConcurrentLinkedHashMap.Node var10 = (ConcurrentLinkedHashMap.Node)var9.get();
         if (var10 == null) {
            break;
         }

         var9.lazySet(null);
         this.applyRead(var10);
         this.readBufferReadCount[var1]++;
      }

      this.readBufferDrainAtWriteCount[var1].lazySet(var5);
   }

   @GuardedBy("evictionLock")
   public void applyRead(ConcurrentLinkedHashMap.Node<K, V> var1) {
      if (this.evictionDeque.Z(var1)) {
         this.evictionDeque.R(var1);
      }
   }

   @GuardedBy("evictionLock")
   public void drainWriteBuffer() {
      for (int var4 = 0; var4 < 16; var4++) {
         Runnable var5 = this.writeBuffer.poll();
         if (var5 == null) {
            break;
         }

         var5.run();
      }
   }

   public boolean tryToRetire(ConcurrentLinkedHashMap.Node<K, V> var1, ConcurrentLinkedHashMap.WeightedValue<V> var2) {
      if (var2.isAlive()) {
         ConcurrentLinkedHashMap.WeightedValue var6 = new ConcurrentLinkedHashMap.WeightedValue(var2.value, -var2.weight);
         return var1.compareAndSet(var2, var6);
      } else {
         return false;
      }
   }

   public void makeRetired(ConcurrentLinkedHashMap.Node<K, V> var1) {
      ConcurrentLinkedHashMap.WeightedValue var4;
      ConcurrentLinkedHashMap.WeightedValue var5;
      do {
         var4 = (ConcurrentLinkedHashMap.WeightedValue)var1.get();
         if (!var4.isAlive()) {
            return;
         }

         var5 = new ConcurrentLinkedHashMap.WeightedValue(var4.value, -var4.weight);
      } while (!var1.compareAndSet(var4, var5));
   }

   @GuardedBy("evictionLock")
   public void makeDead(ConcurrentLinkedHashMap.Node<K, V> var1) {
      ConcurrentLinkedHashMap.WeightedValue var4;
      ConcurrentLinkedHashMap.WeightedValue var5;
      do {
         var4 = (ConcurrentLinkedHashMap.WeightedValue)var1.get();
         var5 = new ConcurrentLinkedHashMap.WeightedValue(var4.value, 0);
      } while (!var1.compareAndSet(var4, var5));

      this.weightedSize.lazySet(this.weightedSize.get() - Math.abs(var4.weight));
   }

   public void notifyListener() {
      ConcurrentLinkedHashMap.Node var4;
      while ((var4 = this.pendingNotifications.poll()) != null) {
         this.listener.onEviction((K)(var4.key), (V)var4.getValue());
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
         ConcurrentLinkedHashMap.Node var4;
         while ((var4 = this.evictionDeque.poll()) != null) {
            this.data.remove(var4.key, var4);
            this.makeDead(var4);
         }

         for (AtomicReference[] var8 : this.readBuffers) {
            for (AtomicReference var12 : var8) {
               var12.lazySet(null);
            }
         }

         Runnable var16;
         while ((var16 = this.writeBuffer.poll()) != null) {
            var16.run();
         }
      } finally {
         this.evictionLock.unlock();
      }
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.data.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      checkNotNull(var1);

      for (ConcurrentLinkedHashMap.Node var6 : this.data.values()) {
         if (var6.getValue().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public V get(Object var1) {
      ConcurrentLinkedHashMap.Node var5 = this.data.get(var1);
      if (var5 == null) {
         return null;
      }

      this.afterRead(var5);
      return (V)var5.getValue();
   }

   public V B(Object var1) {
      ConcurrentLinkedHashMap.Node var5 = this.data.get(var1);
      return (V)(var5 == null ? null : var5.getValue());
   }

   @Override
   public V put(K var1, V var2) {
      return this.put((K)var1, (V)var2, false);
   }

   @Override
   public V putIfAbsent(K var1, V var2) {
      return this.put((K)var1, (V)var2, true);
   }

   public V put(K var1, V var2, boolean var3) {
      checkNotNull(var1);
      checkNotNull(var2);
      int var7 = this.weigher.weightOf((K)var1, (V)var2);
      ConcurrentLinkedHashMap.WeightedValue var8 = new ConcurrentLinkedHashMap.WeightedValue<>(var2, var7);
      ConcurrentLinkedHashMap.Node var9 = new ConcurrentLinkedHashMap.Node<>(var1, var8);

      label27:
      while (true) {
         ConcurrentLinkedHashMap.Node var10 = this.data.putIfAbsent((K)(var9.key), var9);
         if (var10 == null) {
            this.afterWrite(new ConcurrentLinkedHashMap.AddTask(this, var9, var7));
            return null;
         }

         if (var3) {
            this.afterRead(var10);
            return (V)var10.getValue();
         }

         ConcurrentLinkedHashMap.WeightedValue var11;
         do {
            var11 = (ConcurrentLinkedHashMap.WeightedValue)var10.get();
            if (!var11.isAlive()) {
               continue label27;
            }
         } while (!var10.compareAndSet(var11, var8));

         int var12 = var7 - var11.weight;
         if (var12 == 0) {
            this.afterRead(var10);
         } else {
            this.afterWrite(new ConcurrentLinkedHashMap.UpdateTask(this, var10, var12));
         }

         return (V)(var11.value);
      }
   }

   @Override
   public V remove(Object var1) {
      ConcurrentLinkedHashMap.Node var5 = this.data.remove(var1);
      if (var5 == null) {
         return null;
      }

      this.makeRetired(var5);
      this.afterWrite(new ConcurrentLinkedHashMap.RemovalTask(this, var5));
      return (V)var5.getValue();
   }

   @Override
   public boolean remove(Object var1, Object var2) {
      ConcurrentLinkedHashMap.Node var6 = this.data.get(var1);
      if (var6 != null && var2 != null) {
         ConcurrentLinkedHashMap.WeightedValue var7 = (ConcurrentLinkedHashMap.WeightedValue)var6.get();

         while (var7.contains(var2)) {
            if (this.tryToRetire(var6, var7)) {
               if (this.data.remove(var1, var6)) {
                  this.afterWrite(new ConcurrentLinkedHashMap.RemovalTask(this, var6));
                  return true;
               }
               break;
            }

            var7 = (ConcurrentLinkedHashMap.WeightedValue)var6.get();
            if (!var7.isAlive()) {
               break;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public V replace(K var1, V var2) {
      checkNotNull(var1);
      checkNotNull(var2);
      int var6 = this.weigher.weightOf((K)var1, (V)var2);
      ConcurrentLinkedHashMap.WeightedValue var7 = new ConcurrentLinkedHashMap.WeightedValue<>(var2, var6);
      ConcurrentLinkedHashMap.Node var8 = this.data.get(var1);
      if (var8 == null) {
         return null;
      }

      ConcurrentLinkedHashMap.WeightedValue var9;
      do {
         var9 = (ConcurrentLinkedHashMap.WeightedValue)var8.get();
         if (!var9.isAlive()) {
            return null;
         }
      } while (!var8.compareAndSet(var9, var7));

      int var10 = var6 - var9.weight;
      if (var10 == 0) {
         this.afterRead(var8);
      } else {
         this.afterWrite(new ConcurrentLinkedHashMap.UpdateTask(this, var8, var10));
      }

      return (V)(var9.value);
   }

   @Override
   public boolean replace(K var1, V var2, V var3) {
      checkNotNull(var1);
      checkNotNull(var2);
      checkNotNull(var3);
      int var7 = this.weigher.weightOf((K)var1, (V)var3);
      ConcurrentLinkedHashMap.WeightedValue var8 = new ConcurrentLinkedHashMap.WeightedValue<>(var3, var7);
      ConcurrentLinkedHashMap.Node var9 = this.data.get(var1);
      if (var9 == null) {
         return false;
      }

      ConcurrentLinkedHashMap.WeightedValue var10;
      do {
         var10 = (ConcurrentLinkedHashMap.WeightedValue)var9.get();
         if (!var10.isAlive() || !var10.contains(var2)) {
            return false;
         }
      } while (!var9.compareAndSet(var10, var8));

      int var11 = var7 - var10.weight;
      if (var11 == 0) {
         this.afterRead(var9);
      } else {
         this.afterWrite(new ConcurrentLinkedHashMap.UpdateTask(this, var9, var11));
      }

      return true;
   }

   @Override
   public Set<K> keySet() {
      Set var3 = this.keySet;
      return var3 == null ? (this.keySet = new ConcurrentLinkedHashMap.ClhmKeySet()) : var3;
   }

   public Set<K> I_Set() {
      return this.t(Integer.MAX_VALUE);
   }

   public Set<K> t(int var1) {
      return this.orderedKeySet(true, var1);
   }

   public Set<K> Z() {
      return this.s(Integer.MAX_VALUE);
   }

   public Set<K> s(int var1) {
      return this.orderedKeySet(false, var1);
   }

   public Set<K> orderedKeySet(boolean var1, int var2) {
      checkArgument(var2 >= 0);
      this.evictionLock.lock();

      try {
         this.drainBuffers();
         int var6 = this.weigher == Weighers.entrySingleton() ? Math.min(var2, (int)this.weightedSize()) : 16;
         LinkedHashSet var7 = new LinkedHashSet(var6);
         Iterator var8 = var1 ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();

         while (var8.hasNext() && var2 > var7.size()) {
            var7.add(((ConcurrentLinkedHashMap.Node)var8.next()).key);
         }

         return Collections.unmodifiableSet(var7);
      } finally {
         this.evictionLock.unlock();
      }
   }

   @Override
   public Collection<V> values() {
      Collection var3 = this.values;
      return var3 == null ? (this.values = new ConcurrentLinkedHashMap.ClhmValues()) : var3;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      Set var3 = this.entrySet;
      return var3 == null ? (this.entrySet = new ConcurrentLinkedHashMap.ClhmEntrySet()) : var3;
   }

   public Map<K, V> i() {
      return this.C(Integer.MAX_VALUE);
   }

   public Map<K, V> C(int var1) {
      return this.orderedMap(true, var1);
   }

   public Map<K, V> I() {
      return this.f(Integer.MAX_VALUE);
   }

   public Map<K, V> f(int var1) {
      return this.orderedMap(false, var1);
   }

   public Map<K, V> orderedMap(boolean var1, int var2) {
      checkArgument(var2 >= 0);
      this.evictionLock.lock();

      try {
         this.drainBuffers();
         int var7 = this.weigher == Weighers.entrySingleton() ? Math.min(var2, (int)this.weightedSize()) : 16;
         LinkedHashMap var8 = new LinkedHashMap(var7);
         Iterator var9 = var1 ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();

         while (var9.hasNext() && var2 > var8.size()) {
            ConcurrentLinkedHashMap.Node var10 = (ConcurrentLinkedHashMap.Node)var9.next();
            var8.put(var10.key, var10.getValue());
         }

         return Collections.unmodifiableMap(var8);
      } finally {
         this.evictionLock.unlock();
      }
   }

   public Object writeReplace() {
      return new ConcurrentLinkedHashMap.SerializationProxy<>(this);
   }

   static {
      long var14 = d ^ 26463284218207L;
      long var10000 = var14 ^ 5300131460240206170L;
      var10000 = var14 ^ 3552745960900727916L;
      var10000 = var14 ^ 1116637831333105181L;
      var10000 = var14 ^ 4538595175672076505L;
      var10000 = var14 ^ 8878058454201498752L;
   }

   public static final class AddTask implements Runnable {
      public final ConcurrentLinkedHashMap.Node D;
      public final int N;
      public final ConcurrentLinkedHashMap O;
      private static final long a = 22628997384805L;

      public AddTask(ConcurrentLinkedHashMap var1, ConcurrentLinkedHashMap.Node var2, int var3) {
         this.O = var1;
         this.N = var3;
         this.D = var2;
      }

      @GuardedBy("evictionLock")
      @Override
      public void run() {
         this.O.weightedSize.lazySet(this.O.weightedSize.get() + this.N);
         if (((ConcurrentLinkedHashMap.WeightedValue)this.D.get()).isAlive()) {
            this.O.evictionDeque.add(this.D);
            this.O.evict();
         }
      }
   }

   public static final class BoundedEntryWeigher<K, V> implements EntryWeigher<K, V>, Serializable {
      public static final long serialVersionUID = 1L;
      public final EntryWeigher<? super K, ? super V> weigher;
      private static final long a = 140235792019413L;

      public BoundedEntryWeigher(EntryWeigher<? super K, ? super V> var1) {
         ConcurrentLinkedHashMap.checkNotNull(var1);
         this.weigher = var1;
      }

      @Override
      public int weightOf(K var1, V var2) {
         int var6 = this.weigher.weightOf((K)var1, (V)var2);
         ConcurrentLinkedHashMap.checkArgument(var6 >= 1);
         return var6;
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
      private static final long b = 75202585502420L;

      public Builder() {
         this.weigher = Weighers.entrySingleton();
         this.initialCapacity = 16;
         this.concurrencyLevel = 16;
         this.listener = (com.googlecode.concurrentlinkedhashmap.EvictionListener)(ConcurrentLinkedHashMap.DiscardingListener.INSTANCE);
      }

      public ConcurrentLinkedHashMap.Builder<K, V> y(int var1) {
         ConcurrentLinkedHashMap.checkArgument(var1 >= 0);
         this.initialCapacity = var1;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> maximumWeightedCapacity(long var1) {
         ConcurrentLinkedHashMap.checkArgument(var1 >= 0L);
         this.capacity = var1;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> concurrencyLevel(int var1) {
         ConcurrentLinkedHashMap.checkArgument(var1 > 0);
         this.concurrencyLevel = var1;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> listener(EvictionListener<K, V> var1) {
         ConcurrentLinkedHashMap.checkNotNull(var1);
         this.listener = var1;
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> weigher(Weigher<? super V> var1) {
         this.weigher = var1 == Weighers.singleton()
            ? Weighers.entrySingleton()
            : new ConcurrentLinkedHashMap.BoundedEntryWeigher<>(Weighers.asEntryWeigher(var1));
         return this;
      }

      public ConcurrentLinkedHashMap.Builder<K, V> weigher(EntryWeigher<? super K, ? super V> var1) {
         this.weigher = var1 == Weighers.entrySingleton() ? Weighers.entrySingleton() : new ConcurrentLinkedHashMap.BoundedEntryWeigher<>(var1);
         return this;
      }

      public ConcurrentLinkedHashMap<K, V> build() {
         ConcurrentLinkedHashMap.checkState(this.capacity >= 0L);
         return new ConcurrentLinkedHashMap<>(this);
      }

      static {
         long var0 = b ^ 27376990829648L;
         long var10000 = var0 ^ 1367886192841469285L;
         var10000 = var0 ^ 1367886192841469285L;
      }
   }

   public final class ClhmEntrySet extends AbstractSet<Entry<K, V>> {
      public final ConcurrentLinkedHashMap<K, V> N = ConcurrentLinkedHashMap.this;
      private static final long a = 95080245286215L;

      @Override
      public int size() {
         return this.N.size();
      }

      @Override
      public void clear() {
         this.N.clear();
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return (java.util.Iterator)(new ConcurrentLinkedHashMap.EntryIterator(ConcurrentLinkedHashMap.this));
      }

      @Override
      public boolean contains(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         }

         Entry var5 = (Entry)var1;
         ConcurrentLinkedHashMap.Node var6 = this.N.data.get(var5.getKey());
         return var6 != null && var6.getValue().equals(var5.getValue());
      }

      public boolean add(Entry<K, V> var1) {
         return this.N.putIfAbsent((K)var1.getKey(), (V)var1.getValue()) == null;
      }

      @Override
      public boolean remove(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         }

         Entry var5 = (Entry)var1;
         return this.N.remove(var5.getKey(), var5.getValue());
      }
   }

   public final class ClhmKeySet extends AbstractSet<K> {
      public final ConcurrentLinkedHashMap<K, V> u = ConcurrentLinkedHashMap.this;
      private static final long a = 122433357233754L;

      @Override
      public int size() {
         return this.u.size();
      }

      @Override
      public void clear() {
         this.u.clear();
      }

      @Override
      public Iterator<K> iterator() {
         return new ConcurrentLinkedHashMap.KeyIterator(ConcurrentLinkedHashMap.this);
      }

      @Override
      public boolean contains(Object var1) {
         return ConcurrentLinkedHashMap.this.containsKey(var1);
      }

      @Override
      public boolean remove(Object var1) {
         return this.u.remove(var1) != null;
      }

      @Override
      public Object[] toArray() {
         return this.u.data.keySet().toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.u.data.keySet().toArray(var1);
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
      public boolean contains(Object var1) {
         return ConcurrentLinkedHashMap.this.containsValue(var1);
      }
   }

   public enum DiscardingListener implements EvictionListener<Object, Object> {
      INSTANCE;

      @Override
      public void onEviction(Object var1, Object var2) {
      }
   }

   public static final class DiscardingQueue extends AbstractQueue<Object> {
      @Override
      public boolean add(Object var1) {
         return true;
      }

      @Override
      public boolean offer(Object var1) {
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
         private static final long a = 49481285082805L;

         @Override
         public boolean shouldDrainBuffers(boolean var1) {
            return !var1;
         }
      },
      REQUIRED {
         @Override
         public boolean shouldDrainBuffers(boolean var1) {
            return true;
         }
      },
      PROCESSING {
         @Override
         public boolean shouldDrainBuffers(boolean var1) {
            return false;
         }
      };

      DrainStatus() {
      }

      public abstract boolean shouldDrainBuffers(boolean var1);
   }

   public static final class EntryIterator implements Iterator<Entry> {
      public final Iterator I;
      public ConcurrentLinkedHashMap.Node H;
      public final ConcurrentLinkedHashMap B;
      private static final long a = 97436253875443L;

      public EntryIterator(ConcurrentLinkedHashMap var1) {
         this.B = var1;
         this.I = this.B.data.values().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.I.hasNext();
      }

      public Entry next() {
         this.H = (ConcurrentLinkedHashMap.Node)this.I.next();
         return new WriteThroughEntry(this.B, this.H);
      }

      @Override
      public void remove() {
         ConcurrentLinkedHashMap.checkState(this.H != null);
         this.B.remove(this.H.key);
         this.H = null;
      }
   }

   public static final class KeyIterator implements Iterator {
      public final Iterator L;
      public Object S;
      public final ConcurrentLinkedHashMap x;
      private static final long a = 14737733799715L;

      public KeyIterator(ConcurrentLinkedHashMap var1) {
         this.x = var1;
         this.L = this.x.data.keySet().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.L.hasNext();
      }

      @Override
      public Object next() {
         this.S = this.L.next();
         return this.S;
      }

      @Override
      public void remove() {
         ConcurrentLinkedHashMap.checkState(this.S != null);
         this.x.remove(this.S);
         this.S = null;
      }
   }

   public static final class Node<K, V> extends AtomicReference<ConcurrentLinkedHashMap.WeightedValue<V>> implements Linked<ConcurrentLinkedHashMap.Node<K, V>> {
      public final K key;
      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> prev;
      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> next;

      public Node(K var1, ConcurrentLinkedHashMap.WeightedValue<V> var2) {
         super(var2);
         this.key = (K)var1;
      }

      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> U() {
         return this.prev;
      }

      @GuardedBy("evictionLock")
      public void setPrevious(ConcurrentLinkedHashMap.Node<K, V> var1) {
         this.prev = var1;
      }

      @GuardedBy("evictionLock")
      public ConcurrentLinkedHashMap.Node<K, V> getNext() {
         return this.next;
      }

      @GuardedBy("evictionLock")
      public void setNext(ConcurrentLinkedHashMap.Node<K, V> var1) {
         this.next = var1;
      }

      public V getValue() {
         return this.get().value;
      }
   }

   public static final class RemovalTask implements Runnable {
      public final ConcurrentLinkedHashMap.Node v;
      public final ConcurrentLinkedHashMap F;

      public RemovalTask(ConcurrentLinkedHashMap var1, ConcurrentLinkedHashMap.Node var2) {
         this.F = var1;
         this.v = var2;
      }

      @GuardedBy("evictionLock")
      @Override
      public void run() {
         this.F.evictionDeque.remove(this.v);
         this.F.makeDead(this.v);
      }
   }

   public static final class SerializationProxy<K, V> implements Serializable {
      public final EntryWeigher<? super K, ? super V> weigher;
      public final EvictionListener<K, V> listener;
      public final int concurrencyLevel;
      public final Map<K, V> data;
      public final long capacity;
      public static final long serialVersionUID = 1L;

      public SerializationProxy(ConcurrentLinkedHashMap<K, V> var1) {
         this.concurrencyLevel = var1.concurrencyLevel;
         this.data = new HashMap<>(var1);
         this.capacity = var1.capacity.get();
         this.listener = var1.listener;
         this.weigher = var1.weigher;
      }

      public Object readResolve() {
         ConcurrentLinkedHashMap var1 = new ConcurrentLinkedHashMap.Builder<K, V>()
            .concurrencyLevel(this.concurrencyLevel)
            .maximumWeightedCapacity(this.capacity)
            .listener(this.listener)
            .weigher(this.weigher)
            .build();
         var1.putAll(this.data);
         return var1;
      }
   }

   public static final class UpdateTask implements Runnable {
      public final int P;
      public final ConcurrentLinkedHashMap.Node D;
      public final ConcurrentLinkedHashMap h;

      public UpdateTask(ConcurrentLinkedHashMap var1, ConcurrentLinkedHashMap.Node var2, int var3) {
         this.h = var1;
         this.P = var3;
         this.D = var2;
      }

      @GuardedBy("evictionLock")
      @Override
      public void run() {
         this.h.weightedSize.lazySet(this.h.weightedSize.get() + this.P);
         this.h.applyRead(this.D);
         this.h.evict();
      }
   }

   public static final class ValueIterator implements Iterator {
      public final Iterator k;
      public ConcurrentLinkedHashMap.Node b;
      public final ConcurrentLinkedHashMap M;
      private static final long a = 33756788131189L;

      public ValueIterator(ConcurrentLinkedHashMap var1) {
         this.M = var1;
         this.k = this.M.data.values().iterator();
      }

      @Override
      public boolean hasNext() {
         return this.k.hasNext();
      }

      @Override
      public Object next() {
         this.b = (ConcurrentLinkedHashMap.Node)this.k.next();
         return this.b.getValue();
      }

      @Override
      public void remove() {
         ConcurrentLinkedHashMap.checkState(this.b != null);
         this.M.remove(this.b.key);
         this.b = null;
      }
   }

   public static final class WeightedValue<V> {
      public final int weight;
      public final V value;
      private static final long a = 97197698016317L;

      public WeightedValue(V var1, int var2) {
         this.weight = var2;
         this.value = (V)var1;
      }

      public boolean contains(Object var1) {
         return var1 == this.value || this.value.equals(var1);
      }

      public boolean isAlive() {
         return this.weight > 0;
      }

      public boolean B() {
         return this.weight < 0;
      }

      public boolean H() {
         return this.weight == 0;
      }
   }
}
