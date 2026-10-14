package net.minecraft.world;

public class ColorizerFoliage {
   private static int[] foliageBuffer = new int[65536];
   private static final long a = 29972406408919L;

   public static void setFoliageBiomeColorizer(int[] ints) {
      foliageBuffer = ints;
   }

   public static int getFoliageColor(double d, double d2) {
      d2 *= d;
      int i = (int)((1.0 - d) * 255.0);
      int j = (int)((1.0 - d2) * 255.0);
      return foliageBuffer[j << 8 | i];
   }

   public static int getFoliageColorPine() {
      return 6396257;
   }

   public static int getFoliageColorBirch() {
      return 8431445;
   }

   public static int getFoliageColorBasic() {
      return 4764952;
   }
}
