package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorSimplex {
   private static int[][] field_151611_e = new int[][]{
      {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
   };
   public static final double field_151614_a = Math.sqrt(3.0);
   private int[] field_151608_f = new int[512];
   public double field_151612_b;
   public double field_151613_c;
   public double field_151610_d;
   private static final double field_151609_g = 0.5 * (field_151614_a - 1.0);
   private static final double field_151615_h = (3.0 - field_151614_a) / 6.0;
   private static final long a = 68990847919484L;

   public NoiseGeneratorSimplex() {
      this(new Random());
   }

   public NoiseGeneratorSimplex(Random random2) {
      this.field_151612_b = random2.nextDouble() * 256.0;
      this.field_151613_c = random2.nextDouble() * 256.0;
      this.field_151610_d = random2.nextDouble() * 256.0;
      int i = 0;

      while (i < 256) {
         this.field_151608_f[i] = i++;
      }

      for (int j = 0; j < 256; j++) {
         int k = random2.nextInt(256 - j) + j;
         int l = this.field_151608_f[j];
         this.field_151608_f[j] = this.field_151608_f[k];
         this.field_151608_f[k] = l;
         this.field_151608_f[j + 256] = this.field_151608_f[j];
      }
   }

   private static int func_151607_a(double d) {
      return d > 0.0 ? (int)d : (int)d - 1;
   }

   private static double func_151604_a(int[] ints, double d, double d2) {
      return ints[0] * d + ints[1] * d2;
   }

   public double func_151605_a(double d, double d2) {
      double e = 0.5 * (field_151614_a - 1.0);
      double d3 = (d + d2) * e;
      int i = func_151607_a(d + d3);
      int j = func_151607_a(d2 + d3);
      double d4 = (3.0 - field_151614_a) / 6.0;
      double d5 = (i + j) * d4;
      double d6 = i - d5;
      double d7 = j - d5;
      double d8 = d - d6;
      double d9 = d2 - d7;
      byte b;
      byte b2;
      if (d8 > d9) {
         b = 1;
         b2 = 0;
      } else {
         b = 0;
         b2 = 1;
      }

      double d10 = d8 - b + d4;
      double d11 = d9 - b2 + d4;
      double d12 = d8 - 1.0 + 2.0 * d4;
      double d13 = d9 - 1.0 + 2.0 * d4;
      int k = i & 0xFF;
      int l = j & 0xFF;
      int m = this.field_151608_f[k + this.field_151608_f[l]] % 12;
      int n = this.field_151608_f[k + b + this.field_151608_f[l + b2]] % 12;
      int i2 = this.field_151608_f[k + 1 + this.field_151608_f[l + 1]] % 12;
      double d14 = 0.5 - d8 * d8 - d9 * d9;
      double d15;
      if (d14 < 0.0) {
         d15 = 0.0;
      } else {
         d14 *= d14;
         d15 = d14 * d14 * func_151604_a(field_151611_e[m], d8, d9);
      }

      double d16 = 0.5 - d10 * d10 - d11 * d11;
      double d17;
      if (d16 < 0.0) {
         d17 = 0.0;
      } else {
         d16 *= d16;
         d17 = d16 * d16 * func_151604_a(field_151611_e[n], d10, d11);
      }

      double d18 = 0.5 - d12 * d12 - d13 * d13;
      double d19;
      if (d18 < 0.0) {
         d19 = 0.0;
      } else {
         d18 *= d18;
         d19 = d18 * d18 * func_151604_a(field_151611_e[i2], d12, d13);
      }

      return 70.0 * (d15 + d17 + d19);
   }

   public void func_151606_a(double[] doubles, double d, double d2, int i, int j, double d3, double d4, double d5) {
      int k = 0;

      for (int l = 0; l < j; l++) {
         double e = (d2 + l) * d4 + this.field_151613_c;

         for (int m = 0; m < i; m++) {
            double d6 = (d + m) * d3 + this.field_151612_b;
            double d7 = (d6 + e) * field_151609_g;
            int n = func_151607_a(d6 + d7);
            int i2 = func_151607_a(e + d7);
            double d8 = (n + i2) * field_151615_h;
            double d9 = n - d8;
            double d10 = i2 - d8;
            double d11 = d6 - d9;
            double d12 = e - d10;
            byte b;
            byte b2;
            if (d11 > d12) {
               b = 1;
               b2 = 0;
            } else {
               b = 0;
               b2 = 1;
            }

            double d13 = d11 - b + field_151615_h;
            double d14 = d12 - b2 + field_151615_h;
            double d15 = d11 - 1.0 + 2.0 * field_151615_h;
            double d16 = d12 - 1.0 + 2.0 * field_151615_h;
            int i3 = n & 0xFF;
            int i4 = i2 & 0xFF;
            int i5 = this.field_151608_f[i3 + this.field_151608_f[i4]] % 12;
            int i6 = this.field_151608_f[i3 + b + this.field_151608_f[i4 + b2]] % 12;
            int i7 = this.field_151608_f[i3 + 1 + this.field_151608_f[i4 + 1]] % 12;
            double d17 = 0.5 - d11 * d11 - d12 * d12;
            double d18;
            if (d17 < 0.0) {
               d18 = 0.0;
            } else {
               d17 *= d17;
               d18 = d17 * d17 * func_151604_a(field_151611_e[i5], d11, d12);
            }

            double d19 = 0.5 - d13 * d13 - d14 * d14;
            double d20;
            if (d19 < 0.0) {
               d20 = 0.0;
            } else {
               d19 *= d19;
               d20 = d19 * d19 * func_151604_a(field_151611_e[i6], d13, d14);
            }

            double d21 = 0.5 - d15 * d15 - d16 * d16;
            double d22;
            if (d21 < 0.0) {
               d22 = 0.0;
            } else {
               d21 *= d21;
               d22 = d21 * d21 * func_151604_a(field_151611_e[i7], d15, d16);
            }

            int i8 = k++;
            doubles[i8] += 70.0 * (d18 + d20 + d22) * d5;
         }
      }
   }
}
