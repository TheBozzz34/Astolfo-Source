package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Collections;
import java.util.Iterator;

public class CompactHashMap<K, V> {
   public static final CompactHashMap EMPTY = new CompactHashMap();
   private static final long a = 66779442934562L;

   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> var1) {
      return Collections.<Pair<K, V>>emptyList().iterator();
   }

   public int size() {
      return 0;
   }

   public static <K, V> CompactHashMap<K, V> empty() {
      return EMPTY;
   }

   public <U> void forEach(Function<Pair<K, V>, U> var1, KeyFunction<K, V> var2) {
   }

   public V get(K var1, KeyFunction<K, V> var2) {
      return this.get0((K)var1, this.computeHash((K)var1), 0, var2);
   }

   public CompactHashMap<K, V> put(K var1, V var2, KeyFunction<K, V> var3) {
      assert var1.equals(var3.key(var2));
      return this.updated0((K)var1, this.computeHash((K)var1), 0, (V)var2, null, var3);
   }

   public CompactHashMap<K, V> remove(K var1, KeyFunction<K, V> var2) {
      return this.removed0((K)var1, this.computeHash((K)var1), 0, var2);
   }

   public int elemHashCode(K var1) {
      return var1.hashCode();
   }

   public final int improve(int var1) {
      int var4 = var1 + ~(var1 << 9);
      var4 ^= var4 >>> 14;
      var4 += var4 << 4;
      return var4 ^ var4 >>> 10;
   }

   public int computeHash(K var1) {
      return this.improve(this.elemHashCode((K)var1));
   }

   public V get0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      return null;
   }

   public CompactHashMap<K, V> updated0(K var1, int var2, int var3, V var4, Pair<K, V> var5, KeyFunction<K, V> var6) {
      return new HashMap1<>((K)var1, var2, (V)var4, var5);
   }

   public CompactHashMap<K, V> removed0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      return this;
   }

   public static <K, V> HashTrieMap<K, V> makeHashTrieMap(int var0, CompactHashMap<K, V> var1, int var2, CompactHashMap<K, V> var3, int var4, int var5) {
      int var9 = var0 >>> var4 & 31;
      int var10 = var2 >>> var4 & 31;
      if (var9 != var10) {
         int var13 = 1 << var9 | 1 << var10;
         Object[] var14 = new Object[2];
         if (var9 < var10) {
            var14[0] = unwrap(var1);
            var14[1] = unwrap(var3);
         } else {
            var14[0] = unwrap(var3);
            var14[1] = unwrap(var1);
         }

         return new HashTrieMap<>(var13, var14, var5);
      } else {
         Object[] var11 = new Object[1];
         int var12 = 1 << var9;
         var11[0] = makeHashTrieMap(var0, var1, var2, var3, var4 + 5, var5);
         return new HashTrieMap<>(var12, var11, var5);
      }
   }

   public static <K, V> Object unwrap(CompactHashMap<K, V> var0) {
      return var0 instanceof HashMap1 ? ((HashMap1)var0).value : var0;
   }

   public CompactHashMap<K, V> G(Object var1, KeyFunction<K, V> var2) {
      if (var1 instanceof CompactHashMap) {
         return (CompactHashMap<K, V>)var1;
      }

      Object var6 = var1;
      Object var7 = var2.key((V)(var6));
      return new HashMap1<>((K)var7, this.computeHash((K)var7), (V)var6, null);
   }
}
