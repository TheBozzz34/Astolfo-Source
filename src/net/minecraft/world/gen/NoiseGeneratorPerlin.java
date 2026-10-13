package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator {
   private NoiseGeneratorSimplex[] field_151603_a;
   private int field_151602_b;
   private static final long a = 90250542319733L;

   public NoiseGeneratorPerlin(Random var1, int var2) {
      this.field_151602_b = var2;
      this.field_151603_a = new NoiseGeneratorSimplex[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         this.field_151603_a[var5] = new NoiseGeneratorSimplex(var1);
      }
   }

   public double func_151601_a(double var1, double var3) {
      double var8 = 0.0;
      double var10 = 1.0;

      for (int var12 = 0; var12 < this.field_151602_b; var12++) {
         var8 += this.field_151603_a[var12].func_151605_a(var1 * var10, var3 * var10) / var10;
         var10 /= 2.0;
      }

      return var8;
   }

   public double[] func_151599_a(double[] var1, double var2, double var4, int var6, int var7, double var8, double var10, double var12) {
      return this.func_151600_a(var1, var2, var4, var6, var7, var8, var10, var12, 0.5);
   }

   public double[] func_151600_a(double[] var1, double var2, double var4, int var6, int var7, double var8, double var10, double var12, double var14) {
      if (var1 != null && var1.length >= var6 * var7) {
         for (int var19 = 0; var19 < var1.length; var19++) {
            var1[var19] = 0.0;
         }
      } else {
         var1 = new double[var6 * var7];
      }

      double var24 = 1.0;
      double var21 = 1.0;

      for (int var23 = 0; var23 < this.field_151602_b; var23++) {
         this.field_151603_a[var23].func_151606_a(var1, var2, var4, var6, var7, var8 * var21 * var24, var10 * var21 * var24, 0.55 / var24);
         var21 *= var12;
         var24 *= var14;
      }

      return var1;
   }
}
