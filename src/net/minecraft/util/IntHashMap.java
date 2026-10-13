package net.minecraft.util;

public class IntHashMap<V> {
   private transient IntHashMap.Entry<V>[] slots = new IntHashMap.Entry[16];
   private transient int count;
   private int threshold = 12;
   private final float growFactor = 0.75F;
   private static final long c = 111968634505315L;

   private static int computeHash(int var0) {
      var0 = var0 ^ var0 >>> 20 ^ var0 >>> 12;
      return var0 ^ var0 >>> 7 ^ var0 >>> 4;
   }

   private static int getSlotIndex(int var0, int var1) {
      return var0 & var1 - 1;
   }

   public V lookup(int var1) {
      int var5 = computeHash(var1);

      for (IntHashMap.Entry var6 = this.slots[getSlotIndex(var5, this.slots.length)]; var6 != null; var6 = var6.nextEntry) {
         if (var6.hashEntry == var1) {
            return (V)(var6.valueEntry);
         }
      }

      return null;
   }

   public boolean containsItem(int var1) {
      return this.lookupEntry(var1) != null;
   }

   public final IntHashMap.Entry<V> lookupEntry(int var1) {
      int var5 = computeHash(var1);

      for (IntHashMap.Entry var6 = this.slots[getSlotIndex(var5, this.slots.length)]; var6 != null; var6 = var6.nextEntry) {
         if (var6.hashEntry == var1) {
            return var6;
         }
      }

      return null;
   }

   public void addKey(int var1, V var2) {
      int var6 = computeHash(var1);
      int var7 = getSlotIndex(var6, this.slots.length);

      for (IntHashMap.Entry var8 = this.slots[var7]; var8 != null; var8 = var8.nextEntry) {
         if (var8.hashEntry == var1) {
            var8.valueEntry = (V)var2;
            return;
         }
      }

      this.insert(var6, var1, (V)var2, var7);
   }

   private void grow(int var1) {
      IntHashMap.Entry[] var5 = this.slots;
      int var6 = var5.length;
      if (var6 == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         IntHashMap.Entry[] var7 = new IntHashMap.Entry[var1];
         this.copyTo(var7);
         this.slots = var7;
         this.threshold = (int)(var1 * 0.75F);
      }
   }

   private void copyTo(IntHashMap.Entry<V>[] var1) {
      IntHashMap.Entry[] var5 = this.slots;
      int var6 = var1.length;

      for (int var7 = 0; var7 < var5.length; var7++) {
         IntHashMap.Entry var8 = var5[var7];
         if (var8 != null) {
            var5[var7] = null;

            while (true) {
               IntHashMap.Entry var9 = var8.nextEntry;
               int var10 = getSlotIndex(var8.slotHash, var6);
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

   public V removeObject(int var1) {
      IntHashMap.Entry var5 = this.removeEntry(var1);
      return var5 == null ? null : (V)(var5.valueEntry);
   }

   public final IntHashMap.Entry<V> removeEntry(int var1) {
      int var5 = computeHash(var1);
      int var6 = getSlotIndex(var5, this.slots.length);
      IntHashMap.Entry var7 = this.slots[var6];
      IntHashMap.Entry var8 = var7;

      while (var8 != null) {
         IntHashMap.Entry var9 = var8.nextEntry;
         if (var8.hashEntry == var1) {
            this.count--;
            if (var7 == var8) {
               this.slots[var6] = var9;
            } else {
               var7.nextEntry = var9;
            }

            return var8;
         }

         var7 = var8;
         var8 = var9;
      }

      return var8;
   }

   public void clearMap() {
      IntHashMap.Entry[] var4 = this.slots;

      for (int var5 = 0; var5 < var4.length; var5++) {
         var4[var5] = null;
      }

      this.count = 0;
   }

   private void insert(int var1, int var2, V var3, int var4) {
      IntHashMap.Entry var8 = this.slots[var4];
      this.slots[var4] = new IntHashMap.Entry<>(var1, var2, (V)var3, var8);
      if (this.count++ >= this.threshold) {
         this.grow(2 * this.slots.length);
      }
   }

   public static class Entry<V> {
      public final int hashEntry;
      public V valueEntry;
      public IntHashMap.Entry<V> nextEntry;
      public final int slotHash;
      private static final long a = 69096216502792L;

      public Entry(int var1, int var2, V var3, IntHashMap.Entry<V> var4) {
         this.valueEntry = (V)var3;
         this.nextEntry = var4;
         this.hashEntry = var2;
         this.slotHash = var1;
      }

      public final int getHash() {
         return this.hashEntry;
      }

      public final V getValue() {
         return this.valueEntry;
      }

      @Override
      public final boolean equals(Object var1) {
         if (!(var1 instanceof IntHashMap.Entry)) {
            return false;
         }

         IntHashMap.Entry var5 = (IntHashMap.Entry)var1;
         Integer var6 = this.getHash();
         Integer var7 = var5.getHash();
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
         return IntHashMap.computeHash(this.hashEntry);
      }

      @Override
      public final String toString() {
         return this.getHash() + "=" + this.getValue();
      }
   }
}
