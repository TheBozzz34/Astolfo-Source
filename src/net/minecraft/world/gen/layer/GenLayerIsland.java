package net.minecraft.world.gen.layer;

public class GenLayerIsland extends GenLayer {
   private static final long c = 25149160613381L;
   private static final long g = -5916581019498053622L;

   public GenLayerIsland(long var1) {
      super(var1);
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var8 = IntCache.getIntCache(var3 * var4);

      for (int var9 = 0; var9 < var4; var9++) {
         for (int var10 = 0; var10 < var3; var10++) {
            this.initChunkSeed(var1 + var10, var2 + var9);
            var8[var10 + var9 * var3] = this.nextInt((int)g) == 0 ? 1 : 0;
         }
      }

      if (var1 > -var3 && var1 <= 0 && var2 > -var4 && var2 <= 0) {
         var8[-var1 + -var2 * var3] = 1;
      }

      return var8;
   }
}
