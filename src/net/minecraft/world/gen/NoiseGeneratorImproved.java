package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator {
   private int[] permutations = new int[512];
   public double xCoord;
   public double yCoord;
   public double zCoord;
   private static final double[] field_152381_e = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] field_152382_f = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
   private static final double[] field_152383_g = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   private static final double[] field_152384_h = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] field_152385_i = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   private static final long a = 130639010661693L;

   public NoiseGeneratorImproved() {
      this(new Random());
   }

   public NoiseGeneratorImproved(Random random2) {
      this.xCoord = random2.nextDouble() * 256.0;
      this.yCoord = random2.nextDouble() * 256.0;
      this.zCoord = random2.nextDouble() * 256.0;
      int i = 0;

      while (i < 256) {
         this.permutations[i] = i++;
      }

      for (int j = 0; j < 256; j++) {
         int k = random2.nextInt(256 - j) + j;
         int l = this.permutations[j];
         this.permutations[j] = this.permutations[k];
         this.permutations[k] = l;
         this.permutations[j + 256] = this.permutations[j];
      }
   }

   public final double lerp(double d, double d2, double d3) {
      return d2 + d * (d3 - d2);
   }

   public final double func_76309_a(int i, double d, double d2) {
      int j = i & 15;
      return field_152384_h[j] * d + field_152385_i[j] * d2;
   }

   public final double grad(int i, double d, double d2, double d3) {
      int j = i & 15;
      return field_152381_e[j] * d + field_152382_f[j] * d2 + field_152383_g[j] * d3;
   }

   public void populateNoiseArray(
      double[] noiseArray,
      double xOffset,
      double yOffset,
      double zOffset,
      int xSize,
      int ySize,
      int zSize,
      double xScale,
      double yScale,
      double zScale,
      double noiseScale
   ) {
      if (ySize == 1) {
         int i = 0;
         int j = 0;
         int k = 0;
         int l = 0;
         double d = 0.0;
         double e = 0.0;
         int m = 0;
         double d2 = 1.0 / noiseScale;

         for (int n = 0; n < xSize; n++) {
            double d3 = xOffset + n * xScale + this.xCoord;
            int i2 = (int)d3;
            if (d3 < i2) {
               i2--;
            }

            int i3 = i2 & 0xFF;
            d3 -= i2;
            double d4 = d3 * d3 * d3 * (d3 * (d3 * 6.0 - 15.0) + 10.0);

            for (int i4 = 0; i4 < zSize; i4++) {
               double d5 = zOffset + i4 * zScale + this.zCoord;
               int i5 = (int)d5;
               if (d5 < i5) {
                  i5--;
               }

               int i6 = i5 & 0xFF;
               d5 -= i5;
               double d6 = d5 * d5 * d5 * (d5 * (d5 * 6.0 - 15.0) + 10.0);
               i = this.permutations[i3] + 0;
               j = this.permutations[i] + i6;
               k = this.permutations[i3 + 1] + 0;
               l = this.permutations[k] + i6;
               d = this.lerp(d4, this.func_76309_a(this.permutations[j], d3, d5), this.grad(this.permutations[l], d3 - 1.0, 0.0, d5));
               e = this.lerp(
                  d4,
                  this.grad(this.permutations[j + 1], d3, 0.0, d5 - 1.0),
                  this.grad(this.permutations[l + 1], d3 - 1.0, 0.0, d5 - 1.0)
               );
               double d7 = this.lerp(d6, d, e);
               int i7 = m++;
               noiseArray[i7] += d7 * d2;
            }
         }
      } else {
         int i8 = 0;
         double d8 = 1.0 / noiseScale;
         int i9 = -1;
         int i10 = 0;
         int i11 = 0;
         int i12 = 0;
         int i13 = 0;
         int i14 = 0;
         int i15 = 0;
         double d9 = 0.0;
         double d10 = 0.0;
         double d11 = 0.0;
         double d12 = 0.0;

         for (int i16 = 0; i16 < xSize; i16++) {
            double d13 = xOffset + i16 * xScale + this.xCoord;
            int i17 = (int)d13;
            if (d13 < i17) {
               i17--;
            }

            int i18 = i17 & 0xFF;
            d13 -= i17;
            double d14 = d13 * d13 * d13 * (d13 * (d13 * 6.0 - 15.0) + 10.0);

            for (int i19 = 0; i19 < zSize; i19++) {
               double d15 = zOffset + i19 * zScale + this.zCoord;
               int i20 = (int)d15;
               if (d15 < i20) {
                  i20--;
               }

               int i21 = i20 & 0xFF;
               d15 -= i20;
               double d16 = d15 * d15 * d15 * (d15 * (d15 * 6.0 - 15.0) + 10.0);

               for (int i22 = 0; i22 < ySize; i22++) {
                  double d17 = yOffset + i22 * yScale + this.yCoord;
                  int i23 = (int)d17;
                  if (d17 < i23) {
                     i23--;
                  }

                  int i24 = i23 & 0xFF;
                  d17 -= i23;
                  double d18 = d17 * d17 * d17 * (d17 * (d17 * 6.0 - 15.0) + 10.0);
                  if (i22 == 0 || i24 != i9) {
                     i9 = i24;
                     i10 = this.permutations[i18] + i24;
                     i11 = this.permutations[i10] + i21;
                     i12 = this.permutations[i10 + 1] + i21;
                     i13 = this.permutations[i18 + 1] + i24;
                     i14 = this.permutations[i13] + i21;
                     i15 = this.permutations[i13 + 1] + i21;
                     d9 = this.lerp(
                        d14, this.grad(this.permutations[i11], d13, d17, d15), this.grad(this.permutations[i14], d13 - 1.0, d17, d15)
                     );
                     d10 = this.lerp(
                        d14,
                        this.grad(this.permutations[i12], d13, d17 - 1.0, d15),
                        this.grad(this.permutations[i15], d13 - 1.0, d17 - 1.0, d15)
                     );
                     d11 = this.lerp(
                        d14,
                        this.grad(this.permutations[i11 + 1], d13, d17, d15 - 1.0),
                        this.grad(this.permutations[i14 + 1], d13 - 1.0, d17, d15 - 1.0)
                     );
                     d12 = this.lerp(
                        d14,
                        this.grad(this.permutations[i12 + 1], d13, d17 - 1.0, d15 - 1.0),
                        this.grad(this.permutations[i15 + 1], d13 - 1.0, d17 - 1.0, d15 - 1.0)
                     );
                  }

                  double d19 = this.lerp(d18, d9, d10);
                  double d20 = this.lerp(d18, d11, d12);
                  double d21 = this.lerp(d16, d19, d20);
                  int i25 = i8++;
                  noiseArray[i25] += d21 * d8;
               }
            }
         }
      }
   }
}
