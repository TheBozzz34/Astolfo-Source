package net.minecraft.src;

public class IntegerCache {
   private static final Integer[] cache = new Integer[65535];

   public static Integer valueOf(int value) {
      return value > 0 && value < cache.length ? cache[value] : value;
   }

   static {
      int i = 0;

      for (int j = cache.length; i < j; i++) {
         cache[i] = i;
      }
   }
}
