package com.github.andrewoma.dexx.collection.internal.hashmap;

import com.github.andrewoma.dexx.collection.Function;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.andrewoma.dexx.collection.Pair;
import java.util.Iterator;

public class HashTrieMap<K, V> extends CompactHashMap<K, V> {
   private final int bitmap;
   private final Object[] elems;
   private final int size;
   private static final long b = 119074530419114L;

   public HashTrieMap(int var1, Object[] var2, int var3) {
      this.bitmap = var1;
      this.elems = var2;
      this.size = var3;
   }

   @Override
   public int size() {
      return this.size;
   }

   public Object[] getElems() {
      return this.elems;
   }

   public CompactHashMap<K, V> getElem(int var1, KeyFunction<K, V> var2) {
      return this.G(this.elems[var1], var2);
   }

   @Override
   public V get0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      int var8 = var2 >>> var3 & 31;
      int var9 = 1 << var8;
      if (this.bitmap == -1) {
         return this.getElem(var8 & 31, var4).get0((K)var1, var2, var3 + 5, var4);
      } else if ((this.bitmap & var9) != 0) {
         int var10 = Integer.bitCount(this.bitmap & var9 - 1);
         return this.getElem(var10, var4).get0((K)var1, var2, var3 + 5, var4);
      } else {
         return null;
      }
   }

   @Override
   public CompactHashMap<K, V> updated0(K var1, int var2, int var3, V var4, Pair<K, V> var5, KeyFunction<K, V> var6) {
      int var11 = var2 >>> var3 & 31;
      int var12 = 1 << var11;
      int var13 = Integer.bitCount(this.bitmap & var12 - 1);
      if ((this.bitmap & var12) != 0) {
         CompactHashMap var17 = this.getElem(var13, var6);
         CompactHashMap var15 = var17.updated0(var1, var2, var3 + 5, var4, var5, var6);
         if (var15.equals(var17)) {
            return this;
         }

         Object[] var16 = new Object[this.elems.length];
         System.arraycopy(this.elems, 0, var16, 0, this.elems.length);
         var16[var13] = unwrap(var15);
         return new HashTrieMap<>(this.bitmap, var16, this.size + (var15.size() - var17.size()));
      } else {
         Object[] var14 = new Object[this.elems.length + 1];
         System.arraycopy(this.elems, 0, var14, 0, var13);
         var14[var13] = var4;
         System.arraycopy(this.elems, var13, var14, var13 + 1, this.elems.length - var13);
         return new HashTrieMap<>(this.bitmap | var12, var14, this.size + 1);
      }
   }

   @Override
   public CompactHashMap<K, V> removed0(K var1, int var2, int var3, KeyFunction<K, V> var4) {
      int var8 = var2 >>> var3 & 31;
      int var9 = 1 << var8;
      int var10 = Integer.bitCount(this.bitmap & var9 - 1);
      if ((this.bitmap & var9) != 0) {
         CompactHashMap var11 = this.getElem(var10, var4);
         CompactHashMap var12 = var11.removed0(var1, var2, var3 + 5, var4);
         if (var12.equals(var11)) {
            return this;
         }

         if (var12.size() == 0) {
            int var16 = this.bitmap ^ var9;
            if (var16 != 0) {
               Object[] var17 = new Object[this.elems.length - 1];
               System.arraycopy(this.elems, 0, var17, 0, var10);
               System.arraycopy(this.elems, var10 + 1, var17, var10, this.elems.length - var10 - 1);
               int var15 = this.size - var11.size();
               return var17.length == 1 && !(var17[0] instanceof HashTrieMap) ? this.G(var17[0], var4) : new HashTrieMap<>(var16, var17, var15);
            } else {
               return CompactHashMap.empty();
            }
         } else {
            if (this.elems.length == 1 && !(var12 instanceof HashTrieMap)) {
               return var12;
            }

            Object[] var13 = new Object[this.elems.length];
            System.arraycopy(this.elems, 0, var13, 0, this.elems.length);
            var13[var10] = unwrap(var12);
            int var14 = this.size + (var12.size() - var11.size());
            return new HashTrieMap<>(this.bitmap, var13, var14);
         }
      } else {
         return this;
      }
   }

   @Override
   public <U> void forEach(Function<Pair<K, V>, U> var1, KeyFunction<K, V> var2) {
      for (Object var9 : this.elems) {
         if (var9 instanceof CompactHashMap) {
            ((CompactHashMap)var9).forEach(var1, var2);
         } else {
            Object var10 = var9;
            var1.invoke(new Pair(var2.key((V)(var10)), var10));
         }
      }
   }

   @Override
   public Iterator<Pair<K, V>> iterator(KeyFunction<K, V> var1) {
      return new CompactHashMapIterator<>(this.elems, var1);
   }
}
