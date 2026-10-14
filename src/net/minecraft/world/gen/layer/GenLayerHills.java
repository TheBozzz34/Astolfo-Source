package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {
   private static final Logger logger = LogManager.getLogger();
   private GenLayer field_151628_d;

   public GenLayerHills(long l, GenLayer genLayer, GenLayer genLayer2) {
      super(l);
      this.parent = genLayer;
      this.field_151628_d = genLayer2;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] ints2 = this.field_151628_d.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] ints3 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            this.initChunkSeed(j + areaX, i + areaY);
            int k = ints[j + 1 + (i + 1) * (areaWidth + 2)];
            int l = ints2[j + 1 + (i + 1) * (areaWidth + 2)];
            boolean bl = (l - 2) % 29 == 0;
            if (k > 255) {
               logger.debug("old! " + k);
            }

            if (k != 0 && l >= 2 && (l - 2) % 29 == 1 && k < 128) {
               if (BiomeGenBase.getBiome(k + 128) != null) {
                  ints3[j + i * areaWidth] = k + 128;
               } else {
                  ints3[j + i * areaWidth] = k;
               }
            } else if (this.nextInt(3) != 0 && !bl) {
               ints3[j + i * areaWidth] = k;
            } else {
               int m = k;
               if (k == BiomeGenBase.desert.biomeID) {
                  m = BiomeGenBase.desertHills.biomeID;
               } else if (k == BiomeGenBase.forest.biomeID) {
                  m = BiomeGenBase.forestHills.biomeID;
               } else if (k == BiomeGenBase.birchForest.biomeID) {
                  m = BiomeGenBase.birchForestHills.biomeID;
               } else if (k == BiomeGenBase.roofedForest.biomeID) {
                  m = BiomeGenBase.plains.biomeID;
               } else if (k == BiomeGenBase.taiga.biomeID) {
                  m = BiomeGenBase.taigaHills.biomeID;
               } else if (k == BiomeGenBase.megaTaiga.biomeID) {
                  m = BiomeGenBase.megaTaigaHills.biomeID;
               } else if (k == BiomeGenBase.coldTaiga.biomeID) {
                  m = BiomeGenBase.coldTaigaHills.biomeID;
               } else if (k == BiomeGenBase.plains.biomeID) {
                  if (this.nextInt(3) == 0) {
                     m = BiomeGenBase.forestHills.biomeID;
                  } else {
                     m = BiomeGenBase.forest.biomeID;
                  }
               } else if (k == BiomeGenBase.icePlains.biomeID) {
                  m = BiomeGenBase.iceMountains.biomeID;
               } else if (k == BiomeGenBase.jungle.biomeID) {
                  m = BiomeGenBase.jungleHills.biomeID;
               } else if (k == BiomeGenBase.ocean.biomeID) {
                  m = BiomeGenBase.deepOcean.biomeID;
               } else if (k == BiomeGenBase.extremeHills.biomeID) {
                  m = BiomeGenBase.extremeHillsPlus.biomeID;
               } else if (k == BiomeGenBase.savanna.biomeID) {
                  m = BiomeGenBase.savannaPlateau.biomeID;
               } else if (biomesEqualOrMesaPlateau(k, BiomeGenBase.mesaPlateau_F.biomeID)) {
                  m = BiomeGenBase.mesa.biomeID;
               } else if (k == BiomeGenBase.deepOcean.biomeID && this.nextInt(3) == 0) {
                  int n = this.nextInt(2);
                  if (n == 0) {
                     m = BiomeGenBase.plains.biomeID;
                  } else {
                     m = BiomeGenBase.forest.biomeID;
                  }
               }

               if (bl && m != k) {
                  if (BiomeGenBase.getBiome(m + 128) != null) {
                     m += 128;
                  } else {
                     m = k;
                  }
               }

               if (m == k) {
                  ints3[j + i * areaWidth] = k;
               } else {
                  int i2 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                  int i3 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                  int i4 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                  int i5 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                  int i6 = 0;
                  if (biomesEqualOrMesaPlateau(i2, k)) {
                     i6++;
                  }

                  if (biomesEqualOrMesaPlateau(i3, k)) {
                     i6++;
                  }

                  if (biomesEqualOrMesaPlateau(i4, k)) {
                     i6++;
                  }

                  if (biomesEqualOrMesaPlateau(i5, k)) {
                     i6++;
                  }

                  if (i6 >= 3) {
                     ints3[j + i * areaWidth] = m;
                  } else {
                     ints3[j + i * areaWidth] = k;
                  }
               }
            }
         }
      }

      return ints3;
   }
}
