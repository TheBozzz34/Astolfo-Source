package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
   private NoiseGeneratorImproved[] generatorCollection;
   private int octaves;
   private static final long a = 75402371729958L;
   private static final long c = -6920340848240492534L;

   public NoiseGeneratorOctaves(Random var1, int var2) {
      this.octaves = var2;
      this.generatorCollection = new NoiseGeneratorImproved[var2];

      for (int var6 = 0; var6 < var2; var6++) {
         this.generatorCollection[var6] = new NoiseGeneratorImproved(var1);
      }
   }

   public double[] generateNoiseOctaves(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7, double var8, double var10, double var12) {
      if (var1 == null) {
         var1 = new double[var5 * var6 * var7];
      } else {
         for (int var17 = 0; var17 < var1.length; var17++) {
            var1[var17] = 0.0;
         }
      }

      double var30 = 1.0;

      for (int var19 = 0; var19 < this.octaves; var19++) {
         double var20 = var2 * var30 * var8;
         double var22 = var3 * var30 * var10;
         double var24 = var4 * var30 * var12;
         long var26 = MathHelper.floor_double_long(var20);
         long var28 = MathHelper.floor_double_long(var24);
         var20 -= var26;
         var24 -= var28;
         var26 %= 16777216L;
         var28 %= 16777216L;
         var20 += var26;
         var24 += var28;
         this.generatorCollection[var19].populateNoiseArray(var1, var20, var22, var24, var5, var6, var7, var8 * var30, var10 * var30, var12 * var30, var30);
         var30 /= 2.0;
      }

      return var1;
   }

   public double[] generateNoiseOctaves(double[] var1, int var2, int var3, int var4, int var5, double var6, double var8, double var10) {
      return this.generateNoiseOctaves(var1, var2, (int)c, var3, var4, 1, var5, var6, 1.0, var8);
   }
}
