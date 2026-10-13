package net.minecraft.world;

public class ColorizerFoliage {
   private static int[] foliageBuffer = new int[65536];
   private static final long a = 29972406408919L;

   public static void setFoliageBiomeColorizer(int[] var0) {
      foliageBuffer = var0;
   }

   public static int getFoliageColor(double var0, double var2) {
      var2 *= var0;
      int var6 = (int)((1.0 - var0) * 255.0);
      int var7 = (int)((1.0 - var2) * 255.0);
      return foliageBuffer[var7 << 8 | var6];
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
