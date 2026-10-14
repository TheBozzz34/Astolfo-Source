package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerAddMushroomIsland extends GenLayer {
   private static final long c = 8388269400365L;
   private static final long g = 5102773274836729956L;

   public GenLayerAddMushroomIsland(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int i = areaX - 1;
      int j = areaY - 1;
      int k = areaWidth + 2;
      int l = areaHeight + 2;
      int[] ints = this.parent.getInts(i, j, k, l);
      int[] ints2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int m = 0; m < areaHeight; m++) {
         for (int n = 0; n < areaWidth; n++) {
            int i2 = ints[n + 0 + (m + 0) * k];
            int i3 = ints[n + 2 + (m + 0) * k];
            int i4 = ints[n + 0 + (m + 2) * k];
            int i5 = ints[n + 2 + (m + 2) * k];
            int i6 = ints[n + 1 + (m + 1) * k];
            this.initChunkSeed(n + areaX, m + areaY);
            if (i6 == 0 && i2 == 0 && i3 == 0 && i4 == 0 && i5 == 0 && this.nextInt((int)g) == 0) {
               ints2[n + m * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
            } else {
               ints2[n + m * areaWidth] = i6;
            }
         }
      }

      return ints2;
   }
}
