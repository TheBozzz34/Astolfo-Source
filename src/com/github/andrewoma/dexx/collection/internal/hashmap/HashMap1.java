package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Collections;
import java.util.Iterator;

public class HashMap1<K, V> extends CompactHashMap<K, V> {
   public final K key;
   public final int hash;
   public final V value;
   public Pair<K, V> kv;

   public HashMap1(K key, int hash, V value, Pair<K, V> kv) {
      this.key = key;
      this.hash = hash;
      this.value = value;
      this.kv = kv;
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public V get0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      return hash == this.hash && key.equals(this.key) ? this.value : null;
   }

   @Override
   public CompactHashMap<K, V> updated0(K key, int hash, int level, V value, Pair<K, V> kv, KeyFunction<K, V> keyFunction) {
      if (hash == this.hash && key.equals(this.key)) {
         return new HashMap1<>((K)key, hash, (V)value, kv);
      } else if (hash != this.hash) {
         HashMap1 hashMap1 = new HashMap1<>(key, hash, value, kv);
         return makeHashTrieMap(this.hash, this, hash, hashMap1, level, 2);
      } else {
         return new HashMapCollision1<>(hash, ListMap.<K, V>empty().put(this.key, this.value).put((K)key, (V)value));
      }
   }

   @Override
   public CompactHashMap<K, V> removed0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      return hash == this.hash && key.equals(this.key) ? CompactHashMap.empty() : this;
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> f, KeyFunction<K, V> keyFunction) {
      f.invoke(new Pair<>(this.key, this.value));
   }

   private Pair<K, V> ensureKv() {
      if (this.kv == null) {
         this.kv = new Pair<>(this.key, this.value);
      }

      return this.kv;
   }

   @Override
   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> kf) {
      return Collections.singleton(this.ensureKv()).iterator();
   }
}
