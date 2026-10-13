package net.minecraft.src;

public class MathUtils {
   private static final long a = 4076898180838L;

   public static int o(int[] var0) {
      if (var0.length <= 0) {
         return 0;
      }

      int var4 = 0;

      for (int var5 = 0; var5 < var0.length; var5++) {
         int var6 = var0[var5];
         var4 += var6;
      }

      return var4 / var0.length;
   }
}
