package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Collections;
import java.util.Iterator;

public class CompactHashMap<K, V> {
   public static final CompactHashMap EMPTY = new CompactHashMap();

   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> kf) {
      return Collections.<Pair<K, V>>emptyList().iterator();
   }

   public int size() {
      return 0;
   }

   public static <K, V> CompactHashMap<K, V> empty() {
      return EMPTY;
   }

   public <U> void forEach(Function<Pair<K, V>, U> f, KeyFunction<K, V> keyFunction) {
   }

   public V get(K key, KeyFunction<K, V> keyFunction) {
      return this.get0(key, this.computeHash(key), 0, keyFunction);
   }

   public CompactHashMap<K, V> put(K key, V value, KeyFunction<K, V> keyFunction) {
      assert key.equals(keyFunction.key(value));
      return this.updated0(key, this.computeHash(key), 0, value, null, keyFunction);
   }

   public CompactHashMap<K, V> remove(K key, KeyFunction<K, V> keyFunction) {
      return this.removed0(key, this.computeHash(key), 0, keyFunction);
   }

   public int elemHashCode(K key) {
      return key.hashCode();
   }

   public final int improve(int hashCode) {
      int i = hashCode + ~(hashCode << 9);
      i ^= i >>> 14;
      i += i << 4;
      return i ^ i >>> 10;
   }

   public int computeHash(K key) {
      return this.improve(this.elemHashCode(key));
   }

   public V get0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      return null;
   }

   public CompactHashMap<K, V> updated0(K key, int hash, int level, V value, Pair<K, V> kv, KeyFunction<K, V> keyFunction) {
      return new HashMap1<>(key, hash, value, kv);
   }

   public CompactHashMap<K, V> removed0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      return this;
   }

   public static <K, V> HashTrieMap<K, V> makeHashTrieMap(int hash0, CompactHashMap<K, V> elem0, int hash1, CompactHashMap<K, V> elem1, int level, int size) {
      int i = hash0 >>> level & 31;
      int j = hash1 >>> level & 31;
      if (i != j) {
         int k = 1 << i | 1 << j;
         Object[] objects = new Object[2];
         if (i < j) {
            objects[0] = unwrap(elem0);
            objects[1] = unwrap(elem1);
         } else {
            objects[0] = unwrap(elem1);
            objects[1] = unwrap(elem0);
         }

         return new HashTrieMap<>(k, objects, size);
      } else {
         Object[] objects2 = new Object[1];
         int l = 1 << i;
         objects2[0] = makeHashTrieMap(hash0, elem0, hash1, elem1, level + 5, size);
         return new HashTrieMap<>(l, objects2, size);
      }
   }

   public static <K, V> Object unwrap(CompactHashMap<K, V> hashMap) {
      return hashMap instanceof HashMap1 ? ((HashMap1)hashMap).value : hashMap;
   }

   public CompactHashMap<K, V> wrap(Object object, KeyFunction<K, V> keyFunction) {
      if (object instanceof CompactHashMap) {
         return (CompactHashMap<K, V>)object;
      }

      Object object2 = object;
      Object object3 = keyFunction.key((V)(object2));
      return new HashMap1<>((K)object3, this.computeHash((K)object3), (V)object2, null);
   }
}
