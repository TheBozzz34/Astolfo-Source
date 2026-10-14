package net.minecraft.src;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;

public class NextTickHashSet extends TreeSet {
   private LongHashMap longHashMap = new LongHashMap();
   private int minX = Integer.MIN_VALUE;
   private int minZ = Integer.MIN_VALUE;
   private int maxX = Integer.MIN_VALUE;
   private int maxZ = Integer.MIN_VALUE;
   private static final int UNDEFINED = Integer.MIN_VALUE;

   public NextTickHashSet(Set oldSet) {
      for (Object object : oldSet) {
         this.add(object);
      }
   }

   @Override
   public boolean contains(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      }

      NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
      Set set = this.getSubSet(nextTickListEntry, false);
      return set == null ? false : set.contains(nextTickListEntry);
   }

   @Override
   public boolean add(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
         if (nextTickListEntry == null) {
            return false;
         } else {
            Set set = this.getSubSet(nextTickListEntry, true);
            boolean bl = set.add(nextTickListEntry);
            boolean bl2 = super.add(obj);
            if (bl != bl2) {
               throw new IllegalStateException("Added: " + bl + ", addedParent: " + bl2);
            } else {
               return bl2;
            }
         }
      }
   }

   @Override
   public boolean remove(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
         Set set = this.getSubSet(nextTickListEntry, false);
         if (set == null) {
            return false;
         } else {
            boolean bl = set.remove(nextTickListEntry);
            boolean bl2 = super.remove(nextTickListEntry);
            if (bl != bl2) {
               throw new IllegalStateException("Added: " + bl + ", addedParent: " + bl2);
            } else {
               return bl2;
            }
         }
      }
   }

   private Set getSubSet(NextTickListEntry entry, boolean autoCreate) {
      if (entry == null) {
         return null;
      }

      BlockPos blockPos = entry.position;
      int i = blockPos.getX() >> 4;
      int j = blockPos.getZ() >> 4;
      return this.getSubSet(i, j, autoCreate);
   }

   private Set getSubSet(int cx, int cz, boolean autoCreate) {
      long l = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
      HashSet hashSet = (HashSet)this.longHashMap.getValueByKey(l);
      if (hashSet == null && autoCreate) {
         hashSet = new HashSet();
         this.longHashMap.add(l, hashSet);
      }

      return hashSet;
   }

   @Override
   public Iterator iterator() {
      if (this.minX == Integer.MIN_VALUE) {
         return super.iterator();
      }

      if (this.size() <= 0) {
         return Iterators.emptyIterator();
      }

      int i = this.minX >> 4;
      int j = this.minZ >> 4;
      int k = this.maxX >> 4;
      int l = this.maxZ >> 4;
      ArrayList arrayList = new ArrayList();

      for (int m = i; m <= k; m++) {
         for (int n = j; n <= l; n++) {
            Set set = this.getSubSet(m, n, false);
            if (set != null) {
               arrayList.add(set.iterator());
            }
         }
      }

      if (arrayList.size() <= 0) {
         return Iterators.emptyIterator();
      } else {
         return arrayList.size() == 1 ? (Iterator)arrayList.get(0) : Iterators.concat(arrayList.iterator());
      }
   }

   public void setIteratorLimits(int minX, int minZ, int maxX, int maxZ) {
      this.minX = Math.min(minX, maxX);
      this.minZ = Math.min(minZ, maxZ);
      this.maxX = Math.max(minX, maxX);
      this.maxZ = Math.max(minZ, maxZ);
   }

   public void clearIteratorLimits() {
      this.minX = Integer.MIN_VALUE;
      this.minZ = Integer.MIN_VALUE;
      this.maxX = Integer.MIN_VALUE;
      this.maxZ = Integer.MIN_VALUE;
   }
}
