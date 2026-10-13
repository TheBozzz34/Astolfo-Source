package net.minecraft.world.gen.layer;

public class GenLayerRiverInit extends GenLayer {
   private static final long c = 125836227461777L;
   private static final long g = -1210118275577113633L;

   public GenLayerRiverInit(long var1, GenLayer var3) {
      super(var1);
      this.parent = var3;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var8 = this.parent.getInts(var1, var2, var3, var4);
      int[] var9 = IntCache.getIntCache(var3 * var4);

      for (int var10 = 0; var10 < var4; var10++) {
         for (int var11 = 0; var11 < var3; var11++) {
            this.initChunkSeed(var11 + var1, var10 + var2);
            var9[var11 + var10 * var3] = var8[var11 + var10 * var3] > 0 ? this.nextInt((int)g) + 2 : 0;
         }
      }

      return var9;
   }
}
