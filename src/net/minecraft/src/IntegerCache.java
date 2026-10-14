package net.minecraft.src;

public class IntegerCache {
   private static final Integer[] F = new Integer[65535];

   public static Integer I(int i) {
      return i > 0 && i < F.length ? F[i] : i;
   }

   static {
      int i = 0;

      for (int j = F.length; i < j; i++) {
         F[i] = i;
      }
   }
}
