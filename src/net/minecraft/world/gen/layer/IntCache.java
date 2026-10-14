package net.minecraft.world.gen.layer;

import com.google.common.collect.Lists;
import java.util.List;

public class IntCache {
   private static int intCacheSize = 256;
   private static List<int[]> freeSmallArrays = Lists.newArrayList();
   private static List<int[]> inUseSmallArrays = Lists.newArrayList();
   private static List<int[]> freeLargeArrays = Lists.newArrayList();
   private static List<int[]> inUseLargeArrays = Lists.newArrayList();
   private static final long b = 101472601094776L;

   public static synchronized int[] getIntCache(int i) {
      if (i <= 256) {
         if (freeSmallArrays.isEmpty()) {
            int[] ints = new int[256];
            inUseSmallArrays.add(ints);
            return ints;
         } else {
            int[] ints2 = freeSmallArrays.remove(freeSmallArrays.size() - 1);
            inUseSmallArrays.add(ints2);
            return ints2;
         }
      } else if (i > intCacheSize) {
         intCacheSize = i;
         freeLargeArrays.clear();
         inUseLargeArrays.clear();
         int[] ints3 = new int[intCacheSize];
         inUseLargeArrays.add(ints3);
         return ints3;
      } else if (freeLargeArrays.isEmpty()) {
         int[] ints4 = new int[intCacheSize];
         inUseLargeArrays.add(ints4);
         return ints4;
      } else {
         int[] ints5 = freeLargeArrays.remove(freeLargeArrays.size() - 1);
         inUseLargeArrays.add(ints5);
         return ints5;
      }
   }

   public static synchronized void resetIntCache() {
      if (!freeLargeArrays.isEmpty()) {
         freeLargeArrays.remove(freeLargeArrays.size() - 1);
      }

      if (!freeSmallArrays.isEmpty()) {
         freeSmallArrays.remove(freeSmallArrays.size() - 1);
      }

      freeLargeArrays.addAll(inUseLargeArrays);
      freeSmallArrays.addAll(inUseSmallArrays);
      inUseLargeArrays.clear();
      inUseSmallArrays.clear();
   }

   public static synchronized String getCacheSizes() {
      return "cache: "
         + freeLargeArrays.size()
         + ", tcache: "
         + freeSmallArrays.size()
         + ", allocated: "
         + inUseLargeArrays.size()
         + ", tallocated: "
         + inUseSmallArrays.size();
   }
}
