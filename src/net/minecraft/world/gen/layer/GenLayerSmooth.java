package net.minecraft.world.gen.layer;

public class GenLayerSmooth extends GenLayer {
   private static final long c = 109530341732165L;

   public GenLayerSmooth(long l, GenLayer genLayer) {
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
            int i2 = ints[n + 0 + (m + 1) * k];
            int i3 = ints[n + 2 + (m + 1) * k];
            int i4 = ints[n + 1 + (m + 0) * k];
            int i5 = ints[n + 1 + (m + 2) * k];
            int i6 = ints[n + 1 + (m + 1) * k];
            if (i2 == i3 && i4 == i5) {
               this.initChunkSeed(n + areaX, m + areaY);
               if (this.nextInt(2) == 0) {
                  i6 = i2;
               } else {
                  i6 = i4;
               }
            } else {
               if (i2 == i3) {
                  i6 = i2;
               }

               if (i4 == i5) {
                  i6 = i4;
               }
            }

            ints2[n + m * areaWidth] = i6;
         }
      }

      return ints2;
   }
}
