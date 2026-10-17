package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Iterator;

public class HashTrieMap<K, V> extends CompactHashMap<K, V> {
   private final int bitmap;
   private final Object[] elems;
   private final int size;

   public HashTrieMap(int bitmap, Object[] elems, int size) {
      this.bitmap = bitmap;
      this.elems = elems;
      this.size = size;
   }

   @Override
   public int size() {
      return this.size;
   }

   public Object[] getElems() {
      return this.elems;
   }

   public CompactHashMap<K, V> getElem(int index, KeyFunction<K, V> keyFunction) {
      return this.wrap(this.elems[index], keyFunction);
   }

   @Override
   public V get0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      int i = hash >>> level & 31;
      int j = 1 << i;
      if (this.bitmap == -1) {
         return this.getElem(i & 31, keyFunction).get0((K)key, hash, level + 5, keyFunction);
      } else if ((this.bitmap & j) != 0) {
         int k = Integer.bitCount(this.bitmap & j - 1);
         return this.getElem(k, keyFunction).get0((K)key, hash, level + 5, keyFunction);
      } else {
         return null;
      }
   }

   @Override
   public CompactHashMap<K, V> updated0(K key, int hash, int level, V value, Pair<K, V> kv, KeyFunction<K, V> keyFunction) {
      int i = hash >>> level & 31;
      int j = 1 << i;
      int k = Integer.bitCount(this.bitmap & j - 1);
      if ((this.bitmap & j) != 0) {
         CompactHashMap compactHashMap = this.getElem(k, keyFunction);
         CompactHashMap compactHashMap2 = compactHashMap.updated0(key, hash, level + 5, value, kv, keyFunction);
         if (compactHashMap2.equals(compactHashMap)) {
            return this;
         }

         Object[] objects = new Object[this.elems.length];
         System.arraycopy(this.elems, 0, objects, 0, this.elems.length);
         objects[k] = unwrap(compactHashMap2);
         return new HashTrieMap<>(this.bitmap, objects, this.size + (compactHashMap2.size() - compactHashMap.size()));
      } else {
         Object[] objects2 = new Object[this.elems.length + 1];
         System.arraycopy(this.elems, 0, objects2, 0, k);
         objects2[k] = value;
         System.arraycopy(this.elems, k, objects2, k + 1, this.elems.length - k);
         return new HashTrieMap<>(this.bitmap | j, objects2, this.size + 1);
      }
   }

   @Override
   public CompactHashMap<K, V> removed0(K key, int hash, int level, KeyFunction<K, V> keyFunction) {
      int i = hash >>> level & 31;
      int j = 1 << i;
      int k = Integer.bitCount(this.bitmap & j - 1);
      if ((this.bitmap & j) != 0) {
         CompactHashMap compactHashMap = this.getElem(k, keyFunction);
         CompactHashMap compactHashMap2 = compactHashMap.removed0(key, hash, level + 5, keyFunction);
         if (compactHashMap2.equals(compactHashMap)) {
            return this;
         }

         if (compactHashMap2.size() == 0) {
            int l = this.bitmap ^ j;
            if (l != 0) {
               Object[] objects = new Object[this.elems.length - 1];
               System.arraycopy(this.elems, 0, objects, 0, k);
               System.arraycopy(this.elems, k + 1, objects, k, this.elems.length - k - 1);
               int m = this.size - compactHashMap.size();
               return objects.length == 1 && !(objects[0] instanceof HashTrieMap) ? this.wrap(objects[0], keyFunction) : new HashTrieMap<>(l, objects, m);
            } else {
               return CompactHashMap.empty();
            }
         } else {
            if (this.elems.length == 1 && !(compactHashMap2 instanceof HashTrieMap)) {
               return compactHashMap2;
            }

            Object[] objects2 = new Object[this.elems.length];
            System.arraycopy(this.elems, 0, objects2, 0, this.elems.length);
            objects2[k] = unwrap(compactHashMap2);
            int n = this.size + (compactHashMap2.size() - compactHashMap.size());
            return new HashTrieMap<>(this.bitmap, objects2, n);
         }
      } else {
         return this;
      }
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> f, KeyFunction<K, V> keyFunction) {
      for (Object object : this.elems) {
         if (object instanceof CompactHashMap) {
            ((CompactHashMap)object).forEach(f, keyFunction);
         } else {
            Object object2 = object;
            f.invoke(new Pair(keyFunction.key((V)(object2)), object2));
         }
      }
   }

   @Override
   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> kf) {
      return new CompactHashMapIterator<>(this.elems, kf);
   }
}
