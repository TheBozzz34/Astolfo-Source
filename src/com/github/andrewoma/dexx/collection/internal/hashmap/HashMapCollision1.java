package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Iterator;

public class HashMapCollision1<K, V> extends CompactHashMap<K, V> {
   private final int hash;
   private final ListMap<K, V> kvs;
   private static final long c = 90223640519476L;

   public HashMapCollision1(int var1, ListMap<K, V> var2) {
      this.hash = var1;
      this.kvs = var2;
   }

   @Override
   public int size() {
      return this.kvs.size();
   }

   @Override
   public V get0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      return var2 == this.hash ? this.kvs.get((K)var1) : null;
   }

   @Override
   public CompactHashMap<K, V> updated0(K var1, int var2, int var3, V var4, Pair<K, V> var5, KeyFunction<K, V> var6) {
      if (var2 == this.hash) {
         return new HashMapCollision1<>(var2, this.kvs.put((K)var1, (V)var4));
      }

      HashMap1 var9 = new HashMap1<>(var1, var2, var4, var5);
      return makeHashTrieMap(this.hash, this, var2, var9, var3, this.size() + 1);
   }

   @Override
   public CompactHashMap<K, V> removed0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      if (var2 == this.hash) {
         ListMap var8 = this.kvs.remove((K)var1);
         if (var8.isEmpty()) {
            return CompactHashMap.empty();
         } else if (var8.tail().isEmpty()) {
            Pair var9 = (Pair)var8.iterator().next();
            return new HashMap1<>((K)var9.component1(), var2, (V)var9.component2(), var9);
         } else {
            return new HashMapCollision1<>(var2, var8);
         }
      } else {
         return this;
      }
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> var1, KeyFunction<K, V> var2) {
      for (Pair var7 : this.kvs) {
         var1.invoke(var7);
      }
   }

   @Override
   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> var1) {
      return this.kvs.iterator();
   }
}
