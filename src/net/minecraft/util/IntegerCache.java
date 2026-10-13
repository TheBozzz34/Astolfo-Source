package net.minecraft.util;

public class IntegerCache {
   private static final int Y = 4096;
   private static final Integer[] CACHE = v(4096);
   private static final long a = 103307658487378L;

   private static Integer[] v(int var0) {
      Integer[] var3 = new Integer[var0];

      for (int var4 = 0; var4 < var0; var4++) {
         var3[var4] = new Integer(var4);
      }

      return var3;
   }

   public static Integer getInteger(int var0) {
      return var0 >= 0 && var0 < 4096 ? CACHE[var0] : new Integer(var0);
   }

   static {
      long var11 = a ^ 131595654935356L;
      long var10000 = var11 ^ 6681108975686996300L;
   }
}
