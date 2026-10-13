package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer {
   private GenLayer biomePatternGeneratorChain;
   private GenLayer riverPatternGeneratorChain;
   private static final long c = 120602038762368L;
   private static final long g = 6570475552497467647L;

   public GenLayerRiverMix(long var1, GenLayer var3, GenLayer var4) {
      super(var1);
      this.biomePatternGeneratorChain = var3;
      this.riverPatternGeneratorChain = var4;
   }

   @Override
   public void initWorldGenSeed(long var1) {
      this.biomePatternGeneratorChain.initWorldGenSeed(var1);
      this.riverPatternGeneratorChain.initWorldGenSeed(var1);
      super.initWorldGenSeed(var1);
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var8 = this.biomePatternGeneratorChain.getInts(var1, var2, var3, var4);
      int[] var9 = this.riverPatternGeneratorChain.getInts(var1, var2, var3, var4);
      int[] var10 = IntCache.getIntCache(var3 * var4);

      for (int var11 = 0; var11 < var3 * var4; var11++) {
         if (var8[var11] == BiomeGenBase.ocean.biomeID || var8[var11] == BiomeGenBase.deepOcean.biomeID) {
            var10[var11] = var8[var11];
         } else if (var9[var11] == BiomeGenBase.river.biomeID) {
            if (var8[var11] == BiomeGenBase.icePlains.biomeID) {
               var10[var11] = BiomeGenBase.frozenRiver.biomeID;
            } else if (var8[var11] != BiomeGenBase.mushroomIsland.biomeID && var8[var11] != BiomeGenBase.mushroomIslandShore.biomeID) {
               var10[var11] = var9[var11] & (int)g;
            } else {
               var10[var11] = BiomeGenBase.mushroomIslandShore.biomeID;
            }
         } else {
            var10[var11] = var8[var11];
         }
      }

      return var10;
   }
}
