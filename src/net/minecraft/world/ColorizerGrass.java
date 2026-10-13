package net.minecraft.world;

public class ColorizerGrass {
   private static int[] grassBuffer = new int[65536];
   private static final long a = 33174285667782L;

   public static void setGrassBiomeColorizer(int[] var0) {
      grassBuffer = var0;
   }

   public static int getGrassColor(double var0, double var2) {
      var2 *= var0;
      int var7 = (int)((1.0 - var0) * 255.0);
      int var8 = (int)((1.0 - var2) * 255.0);
      int var9 = var8 << 8 | var7;
      return var9 > grassBuffer.length ? -65281 : grassBuffer[var9];
   }
}
