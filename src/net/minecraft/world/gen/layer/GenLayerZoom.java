package net.minecraft.world.gen.layer;

public class GenLayerZoom extends GenLayer {
   private static final long c = 39007506209129L;

   public GenLayerZoom(long var1, GenLayer var3) {
      super(var1);
      super.parent = var3;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int var8 = var1 >> 1;
      int var9 = var2 >> 1;
      int var10 = (var3 >> 1) + 2;
      int var11 = (var4 >> 1) + 2;
      int[] var12 = this.parent.getInts(var8, var9, var10, var11);
      int var13 = var10 - 1 << 1;
      int var14 = var11 - 1 << 1;
      int[] var15 = IntCache.getIntCache(var13 * var14);

      for (int var16 = 0; var16 < var11 - 1; var16++) {
         int var17 = (var16 << 1) * var13;
         int var18 = 0;
         int var19 = var12[var18 + 0 + (var16 + 0) * var10];
         int var20 = var12[var18 + 0 + (var16 + 1) * var10];

         while (var18 < var10 - 1) {
            this.initChunkSeed(var18 + var8 << 1, var16 + var9 << 1);
            int var21 = var12[var18 + 1 + (var16 + 0) * var10];
            int var22 = var12[var18 + 1 + (var16 + 1) * var10];
            var15[var17] = var19;
            var15[var17++ + var13] = this.selectRandom(var19, var20);
            var15[var17] = this.selectRandom(var19, var21);
            var15[var17++ + var13] = this.selectModeOrRandom(var19, var21, var20, var22);
            var19 = var21;
            var20 = var22;
            var18++;
         }
      }

      int[] var23 = IntCache.getIntCache(var3 * var4);

      for (int var25 = 0; var25 < var4; var25++) {
         System.arraycopy(var15, (var25 + (var2 & 1)) * var13 + (var1 & 1), var23, var25 * var3, var3);
      }

      return var23;
   }

   public static GenLayer magnify(long var0, GenLayer var2, int var3) {
      GenLayer var7 = var2;

      for (int var8 = 0; var8 < var3; var8++) {
         var7 = new GenLayerZoom(var0 + var8, var7);
      }

      return var7;
   }
}
