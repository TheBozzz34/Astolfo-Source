package net.minecraft.util;

public class LongHashMap<V> {
   private transient LongHashMap.Entry<V>[] hashArray = new LongHashMap.Entry[4096];
   private transient int numHashElements;
   private int mask;
   private int capacity = 3072;
   private final float percentUseable = 0.75F;
   private transient volatile int modCount;
   private static final long a = 22799504171269L;

   public LongHashMap() {
      this.mask = this.hashArray.length - 1;
   }

   private static int getHashedKey(long var0) {
      return (int)(var0 ^ var0 >>> 27);
   }

   private static int getHashIndex(int var0, int var1) {
      return var0 & var1;
   }

   public int getNumHashElements() {
      return this.numHashElements;
   }

   public V getValueByKey(long var1) {
      int var6 = getHashedKey(var1);

      for (LongHashMap.Entry var7 = this.hashArray[getHashIndex(var6, this.mask)]; var7 != null; var7 = var7.nextEntry) {
         if (var7.key == var1) {
            return (V)(var7.value);
         }
      }

      return null;
   }

   public boolean containsItem(long var1) {
      return this.getEntry(var1) != null;
   }

   public final LongHashMap.Entry<V> getEntry(long var1) {
      int var6 = getHashedKey(var1);

      for (LongHashMap.Entry var7 = this.hashArray[getHashIndex(var6, this.mask)]; var7 != null; var7 = var7.nextEntry) {
         if (var7.key == var1) {
            return var7;
         }
      }

      return null;
   }

   public void add(long var1, V var3) {
      int var7 = getHashedKey(var1);
      int var8 = getHashIndex(var7, this.mask);

      for (LongHashMap.Entry var9 = this.hashArray[var8]; var9 != null; var9 = var9.nextEntry) {
         if (var9.key == var1) {
            var9.value = (V)var3;
            return;
         }
      }

      this.modCount++;
      this.createKey(var7, var1, (V)var3, var8);
   }

   private void resizeTable(int var1) {
      LongHashMap.Entry[] var5 = this.hashArray;
      int var6 = var5.length;
      if (var6 == 1073741824) {
         this.capacity = Integer.MAX_VALUE;
      } else {
         LongHashMap.Entry[] var7 = new LongHashMap.Entry[var1];
         this.copyHashTableTo(var7);
         this.hashArray = var7;
         this.mask = this.hashArray.length - 1;
         float var8 = var1;
         this.getClass();
         this.capacity = (int)(var8 * 0.75F);
      }
   }

   private void copyHashTableTo(LongHashMap.Entry<V>[] var1) {
      LongHashMap.Entry[] var5 = this.hashArray;
      int var6 = var1.length;

      for (int var7 = 0; var7 < var5.length; var7++) {
         LongHashMap.Entry var8 = var5[var7];
         if (var8 != null) {
            var5[var7] = null;

            while (true) {
               LongHashMap.Entry var9 = var8.nextEntry;
               int var10 = getHashIndex(var8.hash, var6 - 1);
               var8.nextEntry = var1[var10];
               var1[var10] = var8;
               var8 = var9;
               if (var9 == null) {
                  break;
               }
            }
         }
      }
   }

   public V remove(long var1) {
      LongHashMap.Entry var6 = this.removeKey(var1);
      return var6 == null ? null : (V)(var6.value);
   }

   public final LongHashMap.Entry removeKey(long var1) {
      int var6 = getHashedKey(var1);
      int var7 = getHashIndex(var6, this.mask);
      LongHashMap.Entry var8 = this.hashArray[var7];
      LongHashMap.Entry var9 = var8;

      while (var9 != null) {
         LongHashMap.Entry var10 = var9.nextEntry;
         if (var9.key == var1) {
            this.modCount++;
            this.numHashElements--;
            if (var8 == var9) {
               this.hashArray[var7] = var10;
            } else {
               var8.nextEntry = var10;
            }

            return var9;
         }

         var8 = var9;
         var9 = var10;
      }

      return var9;
   }

   private void createKey(int var1, long var2, V var4, int var5) {
      LongHashMap.Entry var9 = this.hashArray[var5];
      this.hashArray[var5] = new LongHashMap.Entry<>(var1, var2, (V)var4, var9);
      if (this.numHashElements++ >= this.capacity) {
         this.resizeTable(2 * this.hashArray.length);
      }
   }

   public double getKeyDistribution() {
      int var4 = 0;

      for (int var5 = 0; var5 < this.hashArray.length; var5++) {
         if (this.hashArray[var5] != null) {
            var4++;
         }
      }

      return 1.0 * var4 / this.numHashElements;
   }

   public static class Entry<V> {
      public final long key;
      public V value;
      public LongHashMap.Entry<V> nextEntry;
      public final int hash;
      private static final long a = 21262378104440L;

      public Entry(int var1, long var2, V var4, LongHashMap.Entry<V> var5) {
         this.value = (V)var4;
         this.nextEntry = var5;
         this.key = var2;
         this.hash = var1;
      }

      public final long getKey() {
         return this.key;
      }

      public final V getValue() {
         return this.value;
      }

      @Override
      public final boolean equals(Object var1) {
         if (!(var1 instanceof LongHashMap.Entry)) {
            return false;
         }

         LongHashMap.Entry var5 = (LongHashMap.Entry)var1;
         Long var6 = this.getKey();
         Long var7 = var5.getKey();
         if (var6 == var7 || var6 != null && var6.equals(var7)) {
            Object var8 = this.getValue();
            Object var9 = var5.getValue();
            if (var8 == var9 || var8 != null && var8.equals(var9)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public final int hashCode() {
         return LongHashMap.getHashedKey(this.key);
      }

      @Override
      public final String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
