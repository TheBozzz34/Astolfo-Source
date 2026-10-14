package net.minecraft.world.gen.layer;

public class GenLayerZoom extends GenLayer {
   public GenLayerZoom(long l, GenLayer genLayer) {
      super(l);
      super.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int i = areaX >> 1;
      int j = areaY >> 1;
      int k = (areaWidth >> 1) + 2;
      int l = (areaHeight >> 1) + 2;
      int[] ints = this.parent.getInts(i, j, k, l);
      int m = k - 1 << 1;
      int n = l - 1 << 1;
      int[] ints2 = IntCache.getIntCache(m * n);

      for (int i2 = 0; i2 < l - 1; i2++) {
         int i3 = (i2 << 1) * m;
         int i4 = 0;
         int i5 = ints[i4 + 0 + (i2 + 0) * k];
         int i6 = ints[i4 + 0 + (i2 + 1) * k];

         while (i4 < k - 1) {
            this.initChunkSeed(i4 + i << 1, i2 + j << 1);
            int i7 = ints[i4 + 1 + (i2 + 0) * k];
            int i8 = ints[i4 + 1 + (i2 + 1) * k];
            ints2[i3] = i5;
            ints2[i3++ + m] = this.selectRandom(i5, i6);
            ints2[i3] = this.selectRandom(i5, i7);
            ints2[i3++ + m] = this.selectModeOrRandom(i5, i7, i6, i8);
            i5 = i7;
            i6 = i8;
            i4++;
         }
      }

      int[] ints3 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i9 = 0; i9 < areaHeight; i9++) {
         System.arraycopy(ints2, (i9 + (areaY & 1)) * m + (areaX & 1), ints3, i9 * areaWidth, areaWidth);
      }

      return ints3;
   }

   public static GenLayer magnify(long l, GenLayer genLayer, int i) {
      GenLayer genLayer2 = genLayer;

      for (int j = 0; j < i; j++) {
         genLayer2 = new GenLayerZoom(l + j, genLayer2);
      }

      return genLayer2;
   }
}
