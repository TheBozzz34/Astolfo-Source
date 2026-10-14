package net.minecraft.util;

public class IntegerCache {
   private static final int Y = 4096;
   private static final Integer[] CACHE = v(4096);
   private static final long a = 103307658487378L;

   private static Integer[] v(int i) {
      Integer[] integers = new Integer[i];

      for (int j = 0; j < i; j++) {
         integers[j] = new Integer(j);
      }

      return integers;
   }

   public static Integer getInteger(int value) {
      return value >= 0 && value < 4096 ? CACHE[value] : new Integer(value);
   }

   static {
      long l = a ^ 131595654935356L;
      long m = l ^ 6681108975686996300L;
   }
}
