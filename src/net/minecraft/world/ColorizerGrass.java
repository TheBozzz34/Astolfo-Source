package net.minecraft.world;

public class ColorizerGrass {
   private static int[] grassBuffer = new int[65536];
   private static final long a = 33174285667782L;

   public static void setGrassBiomeColorizer(int[] ints) {
      grassBuffer = ints;
   }

   public static int getGrassColor(double d, double d2) {
      d2 *= d;
      int i = (int)((1.0 - d) * 255.0);
      int j = (int)((1.0 - d2) * 255.0);
      int k = j << 8 | i;
      return k > grassBuffer.length ? -65281 : grassBuffer[k];
   }
}
