package net.minecraft.util;

public class LongHashMap<V> {
   private transient LongHashMap.Entry<V>[] hashArray = new LongHashMap.Entry[4096];
   private transient int numHashElements;
   private int mask;
   private int capacity = 3072;
   private final float percentUseable = 0.75F;
   private transient volatile int modCount;

   public LongHashMap() {
      this.mask = this.hashArray.length - 1;
   }

   private static int getHashedKey(long originalKey) {
      return (int)(originalKey ^ originalKey >>> 27);
   }

   private static int getHashIndex(int i, int j) {
      return i & j;
   }

   public int getNumHashElements() {
      return this.numHashElements;
   }

   public V getValueByKey(long l) {
      int i = getHashedKey(l);

      for (LongHashMap.Entry entry = this.hashArray[getHashIndex(i, this.mask)]; entry != null; entry = entry.nextEntry) {
         if (entry.key == l) {
            return (V)(entry.value);
         }
      }

      return null;
   }

   public boolean containsItem(long l) {
      return this.getEntry(l) != null;
   }

   public final LongHashMap.Entry<V> getEntry(long l) {
      int i = getHashedKey(l);

      for (LongHashMap.Entry entry = this.hashArray[getHashIndex(i, this.mask)]; entry != null; entry = entry.nextEntry) {
         if (entry.key == l) {
            return entry;
         }
      }

      return null;
   }

   public void add(long l, V object) {
      int i = getHashedKey(l);
      int j = getHashIndex(i, this.mask);

      for (LongHashMap.Entry entry = this.hashArray[j]; entry != null; entry = entry.nextEntry) {
         if (entry.key == l) {
            entry.value = (V)object;
            return;
         }
      }

      this.modCount++;
      this.createKey(i, l, (V)object, j);
   }

   private void resizeTable(int i) {
      LongHashMap.Entry[] entrys = this.hashArray;
      int j = entrys.length;
      if (j == 1073741824) {
         this.capacity = Integer.MAX_VALUE;
      } else {
         LongHashMap.Entry[] entrys2 = new LongHashMap.Entry[i];
         this.copyHashTableTo(entrys2);
         this.hashArray = entrys2;
         this.mask = this.hashArray.length - 1;
         float f = i;
         this.getClass();
         this.capacity = (int)(f * 0.75F);
      }
   }

   private void copyHashTableTo(LongHashMap.Entry<V>[] entrys) {
      LongHashMap.Entry[] entrys2 = this.hashArray;
      int i = entrys.length;

      for (int j = 0; j < entrys2.length; j++) {
         LongHashMap.Entry entry = entrys2[j];
         if (entry != null) {
            entrys2[j] = null;

            while (true) {
               LongHashMap.Entry entry2 = entry.nextEntry;
               int k = getHashIndex(entry.hash, i - 1);
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

   public V remove(long l) {
      LongHashMap.Entry entry = this.removeKey(l);
      return entry == null ? null : (V)(entry.value);
   }

   public final LongHashMap.Entry removeKey(long l) {
      int i = getHashedKey(l);
      int j = getHashIndex(i, this.mask);
      LongHashMap.Entry entry = this.hashArray[j];
      LongHashMap.Entry entry2 = entry;

      while (entry2 != null) {
         LongHashMap.Entry entry3 = entry2.nextEntry;
         if (entry2.key == l) {
            this.modCount++;
            this.numHashElements--;
            if (entry == entry2) {
               this.hashArray[j] = entry3;
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

   private void createKey(int i, long l, V object, int j) {
      LongHashMap.Entry entry = this.hashArray[j];
      this.hashArray[j] = new LongHashMap.Entry<>(i, l, (V)object, entry);
      if (this.numHashElements++ >= this.capacity) {
         this.resizeTable(2 * this.hashArray.length);
      }
   }

   public double getKeyDistribution() {
      int i = 0;

      for (int j = 0; j < this.hashArray.length; j++) {
         if (this.hashArray[j] != null) {
            i++;
         }
      }

      return 1.0 * i / this.numHashElements;
   }

   public static class Entry<V> {
      public final long key;
      public V value;
      public LongHashMap.Entry<V> nextEntry;
      public final int hash;

      public Entry(int i, long l, V object, LongHashMap.Entry<V> entry) {
         this.value = (V)object;
         this.nextEntry = entry;
         this.key = l;
         this.hash = i;
      }

      public final long getKey() {
         return this.key;
      }

      public final V getValue() {
         return this.value;
      }

      @Override
      public final boolean equals(Object object) {
         if (!(object instanceof LongHashMap.Entry)) {
            return false;
         }

         LongHashMap.Entry entry = (LongHashMap.Entry)object;
         Long long2 = this.getKey();
         Long long3 = entry.getKey();
         if (long2 == long3 || long2 != null && long2.equals(long3)) {
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
         return LongHashMap.getHashedKey(this.key);
      }

      @Override
      public final String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
