package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiver extends GenLayer {
   public GenLayerRiver(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
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
            int i2 = this.func_151630_c(ints[n + 0 + (m + 1) * k]);
            int i3 = this.func_151630_c(ints[n + 2 + (m + 1) * k]);
            int i4 = this.func_151630_c(ints[n + 1 + (m + 0) * k]);
            int i5 = this.func_151630_c(ints[n + 1 + (m + 2) * k]);
            int i6 = this.func_151630_c(ints[n + 1 + (m + 1) * k]);
            if (i6 == i2 && i6 == i4 && i6 == i3 && i6 == i5) {
               ints2[n + m * areaWidth] = -1;
            } else {
               ints2[n + m * areaWidth] = BiomeGenBase.river.biomeID;
            }
         }
      }

      return ints2;
   }

   private int func_151630_c(int i) {
      return i >= 2 ? 2 + (i & 1) : i;
   }
}
