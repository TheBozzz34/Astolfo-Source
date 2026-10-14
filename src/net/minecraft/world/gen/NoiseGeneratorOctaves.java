package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
   private NoiseGeneratorImproved[] generatorCollection;
   private int octaves;

   public NoiseGeneratorOctaves(Random seed, int octavesIn) {
      this.octaves = octavesIn;
      this.generatorCollection = new NoiseGeneratorImproved[octavesIn];

      for (int i = 0; i < octavesIn; i++) {
         this.generatorCollection[i] = new NoiseGeneratorImproved(seed);
      }
   }

   public double[] generateNoiseOctaves(
      double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale
   ) {
      if (noiseArray == null) {
         noiseArray = new double[xSize * ySize * zSize];
      } else {
         for (int i = 0; i < noiseArray.length; i++) {
            noiseArray[i] = 0.0;
         }
      }

      double d = 1.0;

      for (int j = 0; j < this.octaves; j++) {
         double e = xOffset * d * xScale;
         double d2 = yOffset * d * yScale;
         double d3 = zOffset * d * zScale;
         long l = MathHelper.floor_double_long(e);
         long m = MathHelper.floor_double_long(d3);
         e -= l;
         d3 -= m;
         l %= 16777216L;
         m %= 16777216L;
         e += l;
         d3 += m;
         this.generatorCollection[j]
            .populateNoiseArray(noiseArray, e, d2, d3, xSize, ySize, zSize, xScale * d, yScale * d, zScale * d, d);
         d /= 2.0;
      }

      return noiseArray;
   }

   public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale, double d) {
      return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0, zScale);
   }
}
