package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator {
   private NoiseGeneratorSimplex[] field_151603_a;
   private int field_151602_b;
   private static final long a = 90250542319733L;

   public NoiseGeneratorPerlin(Random random2, int i) {
      this.field_151602_b = i;
      this.field_151603_a = new NoiseGeneratorSimplex[i];

      for (int j = 0; j < i; j++) {
         this.field_151603_a[j] = new NoiseGeneratorSimplex(random2);
      }
   }

   public double func_151601_a(double d, double d2) {
      double e = 0.0;
      double d3 = 1.0;

      for (int i = 0; i < this.field_151602_b; i++) {
         e += this.field_151603_a[i].func_151605_a(d * d3, d2 * d3) / d3;
         d3 /= 2.0;
      }

      return e;
   }

   public double[] func_151599_a(double[] doubles, double d, double d2, int i, int j, double d3, double d4, double d5) {
      return this.func_151600_a(doubles, d, d2, i, j, d3, d4, d5, 0.5);
   }

   public double[] func_151600_a(double[] doubles, double d, double d2, int i, int j, double d3, double d4, double d5, double d6) {
      if (doubles != null && doubles.length >= i * j) {
         for (int k = 0; k < doubles.length; k++) {
            doubles[k] = 0.0;
         }
      } else {
         doubles = new double[i * j];
      }

      double e = 1.0;
      double d7 = 1.0;

      for (int l = 0; l < this.field_151602_b; l++) {
         this.field_151603_a[l].func_151606_a(doubles, d, d2, i, j, d3 * d7 * e, d4 * d7 * e, 0.55 / e);
         d7 *= d5;
         e *= d6;
      }

      return doubles;
   }
}
