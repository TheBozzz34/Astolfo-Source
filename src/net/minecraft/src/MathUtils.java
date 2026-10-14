package net.minecraft.src;

public class MathUtils {
   private static final long a = 4076898180838L;

   public static int o(int[] ints) {
      if (ints.length <= 0) {
         return 0;
      }

      int i = 0;

      for (int j = 0; j < ints.length; j++) {
         int k = ints[j];
         i += k;
      }

      return i / ints.length;
   }
}
