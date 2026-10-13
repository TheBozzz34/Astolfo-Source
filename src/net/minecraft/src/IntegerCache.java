package net.minecraft.src;

public class IntegerCache {
   private static final Integer[] F = new Integer[65535];
   private static final long a = 73208234742281L;

   public static Integer I(int var0) {
      return var0 > 0 && var0 < F.length ? F[var0] : var0;
   }

   static {
      int var8 = 0;

      for (int var9 = F.length; var8 < var9; var8++) {
         F[var8] = var8;
      }
   }
}
