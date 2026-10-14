package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Iterator;

public class HashMapCollision1<K, V> extends CompactHashMap<K, V> {
   private final int hash;
   private final ListMap<K, V> kvs;

   public HashMapCollision1(int hash, ListMap<K, V> kvs) {
      this.hash = hash;
      this.kvs = kvs;
   }

   @Override
   public int size() {
      return this.kvs.size();
   }

   @Override
   public V get0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      return hash == this.hash ? this.kvs.get(key) : null;
   }

   @Override
   public CompactHashMap<K, V> updated0(K key, int hash, int level, V value, Pair<K, V> kv, KeyFunction<K, V> keyFunction) {
      if (hash == this.hash) {
         return new HashMapCollision1<>(hash, this.kvs.put((K)key, (V)value));
      }

      HashMap1 hashMap1 = new HashMap1<>(key, hash, value, kv);
      return makeHashTrieMap(this.hash, this, hash, hashMap1, level, this.size() + 1);
   }

   @Override
   public CompactHashMap<K, V> removed0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      if (hash == this.hash) {
         ListMap listMap = this.kvs.remove((K)key);
         if (listMap.isEmpty()) {
            return CompactHashMap.empty();
         } else if (listMap.tail().isEmpty()) {
            Pair pair = (Pair)listMap.iterator().next();
            return new HashMap1<>((K)pair.component1(), hash, (V)pair.component2(), pair);
         } else {
            return new HashMapCollision1<>(hash, listMap);
         }
      } else {
         return this;
      }
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> f, KeyFunction<K, V> keyFunction) {
      for (Pair pair : this.kvs) {
         f.invoke(pair);
      }
   }

   @Override
   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> kf) {
      return this.kvs.iterator();
   }
}
