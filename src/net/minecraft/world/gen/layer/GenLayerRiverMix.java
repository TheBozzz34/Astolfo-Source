package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer {
   private GenLayer biomePatternGeneratorChain;
   private GenLayer riverPatternGeneratorChain;
   private static final long c = 120602038762368L;
   private static final long g = 6570475552497467647L;

   public GenLayerRiverMix(long l, GenLayer genLayer, GenLayer genLayer2) {
      super(l);
      this.biomePatternGeneratorChain = genLayer;
      this.riverPatternGeneratorChain = genLayer2;
   }

   @Override
   public void initWorldGenSeed(long seed) {
      this.biomePatternGeneratorChain.initWorldGenSeed(seed);
      this.riverPatternGeneratorChain.initWorldGenSeed(seed);
      super.initWorldGenSeed(seed);
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] ints2 = this.riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] ints3 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaWidth * areaHeight; i++) {
         if (ints[i] == BiomeGenBase.ocean.biomeID || ints[i] == BiomeGenBase.deepOcean.biomeID) {
            ints3[i] = ints[i];
         } else if (ints2[i] == BiomeGenBase.river.biomeID) {
            if (ints[i] == BiomeGenBase.icePlains.biomeID) {
               ints3[i] = BiomeGenBase.frozenRiver.biomeID;
            } else if (ints[i] != BiomeGenBase.mushroomIsland.biomeID && ints[i] != BiomeGenBase.mushroomIslandShore.biomeID) {
               ints3[i] = ints2[i] & (int)g;
            } else {
               ints3[i] = BiomeGenBase.mushroomIslandShore.biomeID;
            }
         } else {
            ints3[i] = ints[i];
         }
      }

      return ints3;
   }
}
