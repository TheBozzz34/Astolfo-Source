package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRareBiome extends GenLayer {
   public GenLayerRareBiome(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] ints2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            this.initChunkSeed(j + areaX, i + areaY);
            int k = ints[j + 1 + (i + 1) * (areaWidth + 2)];
            if (this.nextInt(57) == 0) {
               if (k == BiomeGenBase.plains.biomeID) {
                  ints2[j + i * areaWidth] = BiomeGenBase.plains.biomeID + 128;
               } else {
                  ints2[j + i * areaWidth] = k;
               }
            } else {
               ints2[j + i * areaWidth] = k;
            }
         }
      }

      return ints2;
   }
}
