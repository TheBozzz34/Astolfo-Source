package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRareBiome extends GenLayer {
   private static final long c = 38103569670656L;

   public GenLayerRareBiome(long var1, GenLayer var3) {
      super(var1);
      this.parent = var3;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var8 = this.parent.getInts(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var9 = IntCache.getIntCache(var3 * var4);

      for (int var10 = 0; var10 < var4; var10++) {
         for (int var11 = 0; var11 < var3; var11++) {
            this.initChunkSeed(var11 + var1, var10 + var2);
            int var12 = var8[var11 + 1 + (var10 + 1) * (var3 + 2)];
            if (this.nextInt(57) == 0) {
               if (var12 == BiomeGenBase.plains.biomeID) {
                  var9[var11 + var10 * var3] = BiomeGenBase.plains.biomeID + 128;
               } else {
                  var9[var11 + var10 * var3] = var12;
               }
            } else {
               var9[var11 + var10 * var3] = var12;
            }
         }
      }

      return var9;
   }
}
