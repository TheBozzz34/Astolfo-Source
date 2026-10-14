package net.minecraft.world.gen.layer;

public class GenLayerAddIsland extends GenLayer {
   public GenLayerAddIsland(long l, GenLayer genLayer) {
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
            if (i6 != 0 || i2 == 0 && i3 == 0 && i4 == 0 && i5 == 0) {
               if (i6 > 0 && (i2 == 0 || i3 == 0 || i4 == 0 || i5 == 0)) {
                  if (this.nextInt(5) == 0) {
                     if (i6 == 4) {
                        ints2[n + m * areaWidth] = 4;
                     } else {
                        ints2[n + m * areaWidth] = 0;
                     }
                  } else {
                     ints2[n + m * areaWidth] = i6;
                  }
               } else {
                  ints2[n + m * areaWidth] = i6;
               }
            } else {
               int i7 = 1;
               int i8 = 1;
               if (i2 != 0 && this.nextInt(i7++) == 0) {
                  i8 = i2;
               }

               if (i3 != 0 && this.nextInt(i7++) == 0) {
                  i8 = i3;
               }

               if (i4 != 0 && this.nextInt(i7++) == 0) {
                  i8 = i4;
               }

               if (i5 != 0 && this.nextInt(i7++) == 0) {
                  i8 = i5;
               }

               if (this.nextInt(3) == 0) {
                  ints2[n + m * areaWidth] = i8;
               } else if (i8 == 4) {
                  ints2[n + m * areaWidth] = 4;
               } else {
                  ints2[n + m * areaWidth] = 0;
               }
            }
         }
      }

      return ints2;
   }
}
