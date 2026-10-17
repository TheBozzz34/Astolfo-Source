package net.minecraft.world.gen.layer;

public class GenLayerEdge extends GenLayer {
   private final GenLayerEdge.Mode field_151627_c;

   public GenLayerEdge(long l, GenLayer genLayer, GenLayerEdge.Mode mode) {
      super(l);
      this.parent = genLayer;
      this.field_151627_c = mode;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      switch (this.field_151627_c) {
         case COOL_WARM:
         default:
            return this.getIntsCoolWarm(areaX, areaY, areaWidth, areaHeight);
         case HEAT_ICE:
            return this.getIntsHeatIce(areaX, areaY, areaWidth, areaHeight);
         case SPECIAL:
            return this.getIntsSpecial(areaX, areaY, areaWidth, areaHeight);
      }
   }

   private int[] getIntsCoolWarm(int i, int j, int k, int l) {
      int m = i - 1;
      int n = j - 1;
      int i2 = 1 + k + 1;
      int i3 = 1 + l + 1;
      int[] ints = this.parent.getInts(m, n, i2, i3);
      int[] ints2 = IntCache.getIntCache(k * l);

      for (int i4 = 0; i4 < l; i4++) {
         for (int i5 = 0; i5 < k; i5++) {
            this.initChunkSeed(i5 + i, i4 + j);
            int i6 = ints[i5 + 1 + (i4 + 1) * i2];
            if (i6 == 1) {
               int i7 = ints[i5 + 1 + (i4 + 1 - 1) * i2];
               int i8 = ints[i5 + 1 + 1 + (i4 + 1) * i2];
               int i9 = ints[i5 + 1 - 1 + (i4 + 1) * i2];
               int i10 = ints[i5 + 1 + (i4 + 1 + 1) * i2];
               boolean bl = i7 == 3 || i8 == 3 || i9 == 3 || i10 == 3;
               boolean bl2 = i7 == 4 || i8 == 4 || i9 == 4 || i10 == 4;
               if (bl || bl2) {
                  i6 = 2;
               }
            }

            ints2[i5 + i4 * k] = i6;
         }
      }

      return ints2;
   }

   private int[] getIntsHeatIce(int i, int j, int k, int l) {
      int m = i - 1;
      int n = j - 1;
      int i2 = 1 + k + 1;
      int i3 = 1 + l + 1;
      int[] ints = this.parent.getInts(m, n, i2, i3);
      int[] ints2 = IntCache.getIntCache(k * l);

      for (int i4 = 0; i4 < l; i4++) {
         for (int i5 = 0; i5 < k; i5++) {
            int i6 = ints[i5 + 1 + (i4 + 1) * i2];
            if (i6 == 4) {
               int i7 = ints[i5 + 1 + (i4 + 1 - 1) * i2];
               int i8 = ints[i5 + 1 + 1 + (i4 + 1) * i2];
               int i9 = ints[i5 + 1 - 1 + (i4 + 1) * i2];
               int i10 = ints[i5 + 1 + (i4 + 1 + 1) * i2];
               boolean bl = i7 == 2 || i8 == 2 || i9 == 2 || i10 == 2;
               boolean bl2 = i7 == 1 || i8 == 1 || i9 == 1 || i10 == 1;
               if (bl2 || bl) {
                  i6 = 3;
               }
            }

            ints2[i5 + i4 * k] = i6;
         }
      }

      return ints2;
   }

   private int[] getIntsSpecial(int i, int j, int k, int l) {
      int[] ints = this.parent.getInts(i, j, k, l);
      int[] ints2 = IntCache.getIntCache(k * l);

      for (int m = 0; m < l; m++) {
         for (int n = 0; n < k; n++) {
            this.initChunkSeed(n + i, m + j);
            int i2 = ints[n + m * k];
            if (i2 != 0 && this.nextInt(13) == 0) {
               i2 |= 1 + this.nextInt(15) << 8 & 3840;
            }

            ints2[n + m * k] = i2;
         }
      }

      return ints2;
   }

   public enum Mode {
      COOL_WARM,
      HEAT_ICE,
      SPECIAL;
   }
}
