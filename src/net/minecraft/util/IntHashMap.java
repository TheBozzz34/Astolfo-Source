package net.minecraft.util;

public class IntHashMap<V> {
   private transient IntHashMap.Entry<V>[] slots = new IntHashMap.Entry[16];
   private transient int count;
   private int threshold = 12;
   private final float growFactor = 0.75F;

   private static int computeHash(int integer) {
      integer = integer ^ integer >>> 20 ^ integer >>> 12;
      return integer ^ integer >>> 7 ^ integer >>> 4;
   }

   private static int getSlotIndex(int hash, int slotCount) {
      return hash & slotCount - 1;
   }

   public V lookup(int i) {
      int j = computeHash(i);

      for (IntHashMap.Entry entry = this.slots[getSlotIndex(j, this.slots.length)]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == i) {
            return (V)(entry.valueEntry);
         }
      }

      return null;
   }

   public boolean containsItem(int i) {
      return this.lookupEntry(i) != null;
   }

   public final IntHashMap.Entry<V> lookupEntry(int i) {
      int j = computeHash(i);

      for (IntHashMap.Entry entry = this.slots[getSlotIndex(j, this.slots.length)]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == i) {
            return entry;
         }
      }

      return null;
   }

   public void addKey(int i, V object) {
      int j = computeHash(i);
      int k = getSlotIndex(j, this.slots.length);

      for (IntHashMap.Entry entry = this.slots[k]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == i) {
            entry.valueEntry = (V)object;
            return;
         }
      }

      this.insert(j, i, (V)object, k);
   }

   private void grow(int i) {
      IntHashMap.Entry[] entrys = this.slots;
      int j = entrys.length;
      if (j == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         IntHashMap.Entry[] entrys2 = new IntHashMap.Entry[i];
         this.copyTo(entrys2);
         this.slots = entrys2;
         this.threshold = (int)(i * 0.75F);
      }
   }

   private void copyTo(IntHashMap.Entry<V>[] entrys) {
      IntHashMap.Entry[] entrys2 = this.slots;
      int i = entrys.length;

      for (int j = 0; j < entrys2.length; j++) {
         IntHashMap.Entry entry = entrys2[j];
         if (entry != null) {
            entrys2[j] = null;

            while (true) {
               IntHashMap.Entry entry2 = entry.nextEntry;
               int k = getSlotIndex(entry.slotHash, i);
               entry.nextEntry = entrys[k];
               entrys[k] = entry;
               entry = entry2;
               if (entry2 == null) {
                  break;
               }
            }
         }
      }
   }

   public V removeObject(int i) {
      IntHashMap.Entry entry = this.removeEntry(i);
      return entry == null ? null : (V)(entry.valueEntry);
   }

   public final IntHashMap.Entry<V> removeEntry(int i) {
      int j = computeHash(i);
      int k = getSlotIndex(j, this.slots.length);
      IntHashMap.Entry entry = this.slots[k];
      IntHashMap.Entry entry2 = entry;

      while (entry2 != null) {
         IntHashMap.Entry entry3 = entry2.nextEntry;
         if (entry2.hashEntry == i) {
            this.count--;
            if (entry == entry2) {
               this.slots[k] = entry3;
            } else {
               entry.nextEntry = entry3;
            }

            return entry2;
         }

         entry = entry2;
         entry2 = entry3;
      }

      return entry2;
   }

   public void clearMap() {
      IntHashMap.Entry[] entrys = this.slots;

      for (int i = 0; i < entrys.length; i++) {
         entrys[i] = null;
      }

      this.count = 0;
   }

   private void insert(int i, int j, V object, int k) {
      IntHashMap.Entry entry = this.slots[k];
      this.slots[k] = new IntHashMap.Entry<>(i, j, (V)object, entry);
      if (this.count++ >= this.threshold) {
         this.grow(2 * this.slots.length);
      }
   }

   public static class Entry<V> {
      public final int hashEntry;
      public V valueEntry;
      public IntHashMap.Entry<V> nextEntry;
      public final int slotHash;

      public Entry(int i, int j, V object, IntHashMap.Entry<V> entry) {
         this.valueEntry = (V)object;
         this.nextEntry = entry;
         this.hashEntry = j;
         this.slotHash = i;
      }

      public final int getHash() {
         return this.hashEntry;
      }

      public final V getValue() {
         return this.valueEntry;
      }

      @Override
      public final boolean equals(Object object) {
         if (!(object instanceof IntHashMap.Entry)) {
            return false;
         }

         IntHashMap.Entry entry = (IntHashMap.Entry)object;
         Integer integer = this.getHash();
         Integer integer2 = entry.getHash();
         if (integer == integer2 || integer != null && integer.equals(integer2)) {
            Object object2 = this.getValue();
            Object object3 = entry.getValue();
            if (object2 == object3 || object2 != null && object2.equals(object3)) {
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
