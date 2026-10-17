package net.minecraft.world;

public class ColorizerGrass {
   private static int[] grassBuffer = new int[65536];

   public static void setGrassBiomeColorizer(int[] ints) {
      grassBuffer = ints;
   }

   public static int getGrassColor(double d, double e) {
      e *= d;
      int i = (int)((1.0 - d) * 255.0);
      int j = (int)((1.0 - e) * 255.0);
      int k = j << 8 | i;
      return k > grassBuffer.length ? -65281 : grassBuffer[k];
   }
}
