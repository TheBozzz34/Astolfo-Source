package net.minecraft.world.gen.layer;

public class GenLayerRiverInit extends GenLayer {
   private static final long c = 125836227461777L;
   private static final long g = -1210118275577113633L;

   public GenLayerRiverInit(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] ints2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            this.initChunkSeed(j + areaX, i + areaY);
            ints2[j + i * areaWidth] = ints[j + i * areaWidth] > 0 ? this.nextInt((int)g) + 2 : 0;
         }
      }

      return ints2;
   }
}
