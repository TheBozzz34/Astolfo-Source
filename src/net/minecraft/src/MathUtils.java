package net.minecraft.src;

public class MathUtils {
   public static int getAverage(int[] vals) {
      if (vals.length <= 0) {
         return 0;
      }

      int i = 0;

      for (int j = 0; j < vals.length; j++) {
         int k = vals[j];
         i += k;
      }

      return i / vals.length;
   }
}
