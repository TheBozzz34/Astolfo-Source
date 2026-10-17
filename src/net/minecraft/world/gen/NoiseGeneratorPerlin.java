package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator {
   private NoiseGeneratorSimplex[] field_151603_a;
   private int field_151602_b;

   public NoiseGeneratorPerlin(Random random2, int i) {
      this.field_151602_b = i;
      this.field_151603_a = new NoiseGeneratorSimplex[i];

      for (int j = 0; j < i; j++) {
         this.field_151603_a[j] = new NoiseGeneratorSimplex(random2);
      }
   }

   public double func_151601_a(double d, double e) {
      double d2 = 0.0;
      double d3 = 1.0;

      for (int i = 0; i < this.field_151602_b; i++) {
         d2 += this.field_151603_a[i].func_151605_a(d * d3, e * d3) / d3;
         d3 /= 2.0;
      }

      return d2;
   }

   public double[] func_151599_a(double[] doubles, double d, double e, int i, int j, double d2, double d3, double d4) {
      return this.func_151600_a(doubles, d, e, i, j, d2, d3, d4, 0.5);
   }

   public double[] func_151600_a(double[] doubles, double d, double e, int i, int j, double d2, double d3, double d4, double d5) {
      if (doubles != null && doubles.length >= i * j) {
         for (int k = 0; k < doubles.length; k++) {
            doubles[k] = 0.0;
         }
      } else {
         doubles = new double[i * j];
      }

      double d6 = 1.0;
      double d7 = 1.0;

      for (int l = 0; l < this.field_151602_b; l++) {
         this.field_151603_a[l].func_151606_a(doubles, d, e, i, j, d2 * d7 * d6, d3 * d7 * d6, 0.55 / d6);
         d7 *= d4;
         d6 *= d5;
      }

      return doubles;
   }
}
