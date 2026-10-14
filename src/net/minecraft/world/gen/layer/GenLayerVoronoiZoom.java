package net.minecraft.world.gen.layer;

public class GenLayerVoronoiZoom extends GenLayer {
   private static final long c = 51371569411676L;

   public GenLayerVoronoiZoom(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      areaX -= 2;
      areaY -= 2;
      int i = areaX >> 2;
      int j = areaY >> 2;
      int k = (areaWidth >> 2) + 2;
      int l = (areaHeight >> 2) + 2;
      int[] ints = this.parent.getInts(i, j, k, l);
      int m = k - 1 << 2;
      int n = l - 1 << 2;
      int[] ints2 = IntCache.getIntCache(m * n);

      for (int i2 = 0; i2 < l - 1; i2++) {
         int i3 = 0;
         int i4 = ints[i3 + 0 + (i2 + 0) * k];
         int i5 = ints[i3 + 0 + (i2 + 1) * k];

         while (i3 < k - 1) {
            this.initChunkSeed(i3 + i << 2, i2 + j << 2);
            double d = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double e = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(i3 + i + 1 << 2, i2 + j << 2);
            double d2 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d3 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(i3 + i << 2, i2 + j + 1 << 2);
            double d4 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double d5 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            this.initChunkSeed(i3 + i + 1 << 2, i2 + j + 1 << 2);
            double d6 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double d7 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            int i6 = ints[i3 + 1 + (i2 + 0) * k] & 0xFF;
            int i7 = ints[i3 + 1 + (i2 + 1) * k] & 0xFF;

            for (int i8 = 0; i8 < 4; i8++) {
               int i9 = ((i2 << 2) + i8) * m + (i3 << 2);

               for (int i10 = 0; i10 < 4; i10++) {
                  double d8 = (i8 - e) * (i8 - e) + (i10 - d) * (i10 - d);
                  double d9 = (i8 - d3) * (i8 - d3) + (i10 - d2) * (i10 - d2);
                  double d10 = (i8 - d5) * (i8 - d5) + (i10 - d4) * (i10 - d4);
                  double d11 = (i8 - d7) * (i8 - d7) + (i10 - d6) * (i10 - d6);
                  if (d8 < d9 && d8 < d10 && d8 < d11) {
                     ints2[i9++] = i4;
                  } else if (d9 < d8 && d9 < d10 && d9 < d11) {
                     ints2[i9++] = i6;
                  } else if (d10 < d8 && d10 < d9 && d10 < d11) {
                     ints2[i9++] = i5;
                  } else {
                     ints2[i9++] = i7;
                  }
               }
            }

            i4 = i6;
            i5 = i7;
            i3++;
         }
      }

      int[] ints3 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i11 = 0; i11 < areaHeight; i11++) {
         System.arraycopy(ints2, (i11 + (areaY & 3)) * m + (areaX & 3), ints3, i11 * areaWidth, areaWidth);
      }

      return ints3;
   }
}
