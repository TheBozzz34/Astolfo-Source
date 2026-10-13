package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerDeepOcean extends GenLayer {
   private static final long c = 129578246388544L;

   public GenLayerDeepOcean(long var1, GenLayer var3) {
      super(var1);
      this.parent = var3;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int var8 = var1 - 1;
      int var9 = var2 - 1;
      int var10 = var3 + 2;
      int var11 = var4 + 2;
      int[] var12 = this.parent.getInts(var8, var9, var10, var11);
      int[] var13 = IntCache.getIntCache(var3 * var4);

      for (int var14 = 0; var14 < var4; var14++) {
         for (int var15 = 0; var15 < var3; var15++) {
            int var16 = var12[var15 + 1 + (var14 + 1 - 1) * (var3 + 2)];
            int var17 = var12[var15 + 1 + 1 + (var14 + 1) * (var3 + 2)];
            int var18 = var12[var15 + 1 - 1 + (var14 + 1) * (var3 + 2)];
            int var19 = var12[var15 + 1 + (var14 + 1 + 1) * (var3 + 2)];
            int var20 = var12[var15 + 1 + (var14 + 1) * var10];
            int var21 = 0;
            if (var16 == 0) {
               var21++;
            }

            if (var17 == 0) {
               var21++;
            }

            if (var18 == 0) {
               var21++;
            }

            if (var19 == 0) {
               var21++;
            }

            if (var20 == 0 && var21 > 3) {
               var13[var15 + var14 * var3] = BiomeGenBase.deepOcean.biomeID;
            } else {
               var13[var15 + var14 * var3] = var20;
            }
         }
      }

      return var13;
   }
}
