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

   public NoiseGeneratorImproved(Random var1) {
      this.xCoord = var1.nextDouble() * 256.0;
      this.yCoord = var1.nextDouble() * 256.0;
      this.zCoord = var1.nextDouble() * 256.0;
      int var5 = 0;

      while (var5 < 256) {
         this.permutations[var5] = var5++;
      }

      for (int var8 = 0; var8 < 256; var8++) {
         int var6 = var1.nextInt(256 - var8) + var8;
         int var7 = this.permutations[var8];
         this.permutations[var8] = this.permutations[var6];
         this.permutations[var6] = var7;
         this.permutations[var8 + 256] = this.permutations[var8];
      }
   }

   public final double lerp(double var1, double var3, double var5) {
      return var3 + var1 * (var5 - var3);
   }

   public final double func_76309_a(int var1, double var2, double var4) {
      int var8 = var1 & 15;
      return field_152384_h[var8] * var2 + field_152385_i[var8] * var4;
   }

   public final double grad(int var1, double var2, double var4, double var6) {
      int var10 = var1 & 15;
      return field_152381_e[var10] * var2 + field_152382_f[var10] * var4 + field_152383_g[var10] * var6;
   }

   public void populateNoiseArray(
      double[] var1, double var2, double var4, double var6, int var8, int var9, int var10, double var11, double var13, double var15, double var17
   ) {
      if (var9 == 1) {
         int var22 = 0;
         int var23 = 0;
         int var24 = 0;
         int var25 = 0;
         double var26 = 0.0;
         double var28 = 0.0;
         int var30 = 0;
         double var31 = 1.0 / var17;

         for (int var33 = 0; var33 < var8; var33++) {
            double var34 = var2 + var33 * var11 + this.xCoord;
            int var36 = (int)var34;
            if (var34 < var36) {
               var36--;
            }

            int var37 = var36 & 0xFF;
            var34 -= var36;
            double var38 = var34 * var34 * var34 * (var34 * (var34 * 6.0 - 15.0) + 10.0);

            for (int var40 = 0; var40 < var10; var40++) {
               double var41 = var6 + var40 * var15 + this.zCoord;
               int var43 = (int)var41;
               if (var41 < var43) {
                  var43--;
               }

               int var44 = var43 & 0xFF;
               var41 -= var43;
               double var45 = var41 * var41 * var41 * (var41 * (var41 * 6.0 - 15.0) + 10.0);
               var22 = this.permutations[var37] + 0;
               var23 = this.permutations[var22] + var44;
               var24 = this.permutations[var37 + 1] + 0;
               var25 = this.permutations[var24] + var44;
               var26 = this.lerp(var38, this.func_76309_a(this.permutations[var23], var34, var41), this.grad(this.permutations[var25], var34 - 1.0, 0.0, var41));
               var28 = this.lerp(
                  var38,
                  this.grad(this.permutations[var23 + 1], var34, 0.0, var41 - 1.0),
                  this.grad(this.permutations[var25 + 1], var34 - 1.0, 0.0, var41 - 1.0)
               );
               double var47 = this.lerp(var45, var26, var28);
               int var49 = var30++;
               var1[var49] += var47 * var31;
            }
         }
      } else {
         int var69 = 0;
         double var71 = 1.0 / var17;
         int var74 = -1;
         int var76 = 0;
         int var27 = 0;
         int var80 = 0;
         int var29 = 0;
         int var83 = 0;
         int var85 = 0;
         double var32 = 0.0;
         double var88 = 0.0;
         double var89 = 0.0;
         double var90 = 0.0;

         for (int var91 = 0; var91 < var8; var91++) {
            double var93 = var2 + var91 * var11 + this.xCoord;
            int var95 = (int)var93;
            if (var93 < var95) {
               var95--;
            }

            int var96 = var95 & 0xFF;
            var93 -= var95;
            double var97 = var93 * var93 * var93 * (var93 * (var93 * 6.0 - 15.0) + 10.0);

            for (int var98 = 0; var98 < var10; var98++) {
               double var48 = var6 + var98 * var15 + this.zCoord;
               int var50 = (int)var48;
               if (var48 < var50) {
                  var50--;
               }

               int var51 = var50 & 0xFF;
               var48 -= var50;
               double var52 = var48 * var48 * var48 * (var48 * (var48 * 6.0 - 15.0) + 10.0);

               for (int var54 = 0; var54 < var9; var54++) {
                  double var55 = var4 + var54 * var13 + this.yCoord;
                  int var57 = (int)var55;
                  if (var55 < var57) {
                     var57--;
                  }

                  int var58 = var57 & 0xFF;
                  var55 -= var57;
                  double var59 = var55 * var55 * var55 * (var55 * (var55 * 6.0 - 15.0) + 10.0);
                  if (var54 == 0 || var58 != var74) {
                     var74 = var58;
                     var76 = this.permutations[var96] + var58;
                     var27 = this.permutations[var76] + var51;
                     var80 = this.permutations[var76 + 1] + var51;
                     var29 = this.permutations[var96 + 1] + var58;
                     var83 = this.permutations[var29] + var51;
                     var85 = this.permutations[var29 + 1] + var51;
                     var32 = this.lerp(
                        var97, this.grad(this.permutations[var27], var93, var55, var48), this.grad(this.permutations[var83], var93 - 1.0, var55, var48)
                     );
                     var88 = this.lerp(
                        var97,
                        this.grad(this.permutations[var80], var93, var55 - 1.0, var48),
                        this.grad(this.permutations[var85], var93 - 1.0, var55 - 1.0, var48)
                     );
                     var89 = this.lerp(
                        var97,
                        this.grad(this.permutations[var27 + 1], var93, var55, var48 - 1.0),
                        this.grad(this.permutations[var83 + 1], var93 - 1.0, var55, var48 - 1.0)
                     );
                     var90 = this.lerp(
                        var97,
                        this.grad(this.permutations[var80 + 1], var93, var55 - 1.0, var48 - 1.0),
                        this.grad(this.permutations[var85 + 1], var93 - 1.0, var55 - 1.0, var48 - 1.0)
                     );
                  }

                  double var61 = this.lerp(var59, var32, var88);
                  double var63 = this.lerp(var59, var89, var90);
                  double var65 = this.lerp(var52, var61, var63);
                  int var67 = var69++;
                  var1[var67] += var65 * var71;
               }
            }
         }
      }
   }
}
