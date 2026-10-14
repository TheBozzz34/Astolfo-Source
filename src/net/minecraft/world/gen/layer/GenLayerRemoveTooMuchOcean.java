package net.minecraft.world.gen.layer;

public class GenLayerRemoveTooMuchOcean extends GenLayer {
   public GenLayerRemoveTooMuchOcean(long l, GenLayer genLayer) {
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
            int i2 = ints[n + 1 + (m + 1 - 1) * (areaWidth + 2)];
            int i3 = ints[n + 1 + 1 + (m + 1) * (areaWidth + 2)];
            int i4 = ints[n + 1 - 1 + (m + 1) * (areaWidth + 2)];
            int i5 = ints[n + 1 + (m + 1 + 1) * (areaWidth + 2)];
            int i6 = ints[n + 1 + (m + 1) * k];
            ints2[n + m * areaWidth] = i6;
            this.initChunkSeed(n + areaX, m + areaY);
            if (i6 == 0 && i2 == 0 && i3 == 0 && i4 == 0 && i5 == 0 && this.nextInt(2) == 0) {
               ints2[n + m * areaWidth] = 1;
            }
         }
      }

      return ints2;
   }
}
