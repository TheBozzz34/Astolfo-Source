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
   private static final long a = 69386343082014L;

   public NextTickHashSet(Set var1) {
      for (Object var6 : var1) {
         this.add(var6);
      }
   }

   @Override
   public boolean contains(Object var1) {
      if (!(var1 instanceof NextTickListEntry)) {
         return false;
      }

      NextTickListEntry var5 = (NextTickListEntry)var1;
      Set var6 = this.getSubSet(var5, false);
      return var6 == null ? false : var6.contains(var5);
   }

   @Override
   public boolean add(Object var1) {
      if (!(var1 instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry var5 = (NextTickListEntry)var1;
         if (var5 == null) {
            return false;
         } else {
            Set var6 = this.getSubSet(var5, true);
            boolean var7 = var6.add(var5);
            boolean var8 = super.add(var1);
            if (var7 != var8) {
               throw new IllegalStateException("Added: " + var7 + ", addedParent: " + var8);
            } else {
               return var8;
            }
         }
      }
   }

   @Override
   public boolean remove(Object var1) {
      if (!(var1 instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry var5 = (NextTickListEntry)var1;
         Set var6 = this.getSubSet(var5, false);
         if (var6 == null) {
            return false;
         } else {
            boolean var7 = var6.remove(var5);
            boolean var8 = super.remove(var5);
            if (var7 != var8) {
               throw new IllegalStateException("Added: " + var7 + ", addedParent: " + var8);
            } else {
               return var8;
            }
         }
      }
   }

   private Set getSubSet(NextTickListEntry var1, boolean var2) {
      if (var1 == null) {
         return null;
      }

      BlockPos var6 = var1.position;
      int var7 = var6.getX() >> 4;
      int var8 = var6.getZ() >> 4;
      return this.getSubSet(var7, var8, var2);
   }

   private Set getSubSet(int var1, int var2, boolean var3) {
      long var7 = ChunkCoordIntPair.chunkXZ2Int(var1, var2);
      HashSet var9 = (HashSet)this.longHashMap.getValueByKey(var7);
      if (var9 == null && var3) {
         var9 = new HashSet();
         this.longHashMap.add(var7, var9);
      }

      return var9;
   }

   @Override
   public Iterator iterator() {
      if (this.minX == Integer.MIN_VALUE) {
         return super.iterator();
      }

      if (this.size() <= 0) {
         return Iterators.emptyIterator();
      }

      int var4 = this.minX >> 4;
      int var5 = this.minZ >> 4;
      int var6 = this.maxX >> 4;
      int var7 = this.maxZ >> 4;
      ArrayList var8 = new ArrayList();

      for (int var9 = var4; var9 <= var6; var9++) {
         for (int var10 = var5; var10 <= var7; var10++) {
            Set var11 = this.getSubSet(var9, var10, false);
            if (var11 != null) {
               var8.add(var11.iterator());
            }
         }
      }

      if (var8.size() <= 0) {
         return Iterators.emptyIterator();
      } else {
         return var8.size() == 1 ? (Iterator)var8.get(0) : Iterators.concat(var8.iterator());
      }
   }

   public void setIteratorLimits(int var1, int var2, int var3, int var4) {
      this.minX = Math.min(var1, var3);
      this.minZ = Math.min(var2, var4);
      this.maxX = Math.max(var1, var3);
      this.maxZ = Math.max(var2, var4);
   }

   public void clearIteratorLimits() {
      this.minX = Integer.MIN_VALUE;
      this.minZ = Integer.MIN_VALUE;
      this.maxX = Integer.MIN_VALUE;
      this.maxZ = Integer.MIN_VALUE;
   }

   static {
      long var11 = a ^ 80801652512264L;
      long var10000 = var11 ^ 1358809103004623724L;
   }
}
