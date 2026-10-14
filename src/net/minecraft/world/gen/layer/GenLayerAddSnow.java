package net.minecraft.world.gen.layer;

public class GenLayerAddSnow extends GenLayer {
   public GenLayerAddSnow(long l, GenLayer genLayer) {
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
            int i2 = ints[n + 1 + (m + 1) * k];
            this.initChunkSeed(n + areaX, m + areaY);
            if (i2 == 0) {
               ints2[n + m * areaWidth] = 0;
            } else {
               int i3 = this.nextInt(6);
               byte b;
               if (i3 == 0) {
                  b = 4;
               } else if (i3 <= 1) {
                  b = 3;
               } else {
                  b = 1;
               }

               ints2[n + m * areaWidth] = b;
            }
         }
      }

      return ints2;
   }
}
