package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerBiomeEdge extends GenLayer {
   private static final long c = 50075045644253L;

   public GenLayerBiomeEdge(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] ints = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] ints2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int i = 0; i < areaHeight; i++) {
         for (int j = 0; j < areaWidth; j++) {
            this.initChunkSeed(j + areaX, i + areaY);
            int k = ints[j + 1 + (i + 1) * (areaWidth + 2)];
            if (!this.replaceBiomeEdgeIfNecessary(
                  ints, ints2, j, i, areaWidth, k, BiomeGenBase.extremeHills.biomeID, BiomeGenBase.extremeHillsEdge.biomeID
               )
               && !this.replaceBiomeEdge(ints, ints2, j, i, areaWidth, k, BiomeGenBase.mesaPlateau_F.biomeID, BiomeGenBase.mesa.biomeID)
               && !this.replaceBiomeEdge(ints, ints2, j, i, areaWidth, k, BiomeGenBase.mesaPlateau.biomeID, BiomeGenBase.mesa.biomeID)
               && !this.replaceBiomeEdge(ints, ints2, j, i, areaWidth, k, BiomeGenBase.megaTaiga.biomeID, BiomeGenBase.taiga.biomeID)) {
               if (k == BiomeGenBase.desert.biomeID) {
                  int l = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                  int m = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                  int n = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                  int i2 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                  if (l != BiomeGenBase.icePlains.biomeID
                     && m != BiomeGenBase.icePlains.biomeID
                     && n != BiomeGenBase.icePlains.biomeID
                     && i2 != BiomeGenBase.icePlains.biomeID) {
                     ints2[j + i * areaWidth] = k;
                  } else {
                     ints2[j + i * areaWidth] = BiomeGenBase.extremeHillsPlus.biomeID;
                  }
               } else if (k == BiomeGenBase.swampland.biomeID) {
                  int i3 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
                  int i4 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
                  int i5 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
                  int i6 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
                  if (i3 == BiomeGenBase.desert.biomeID
                     || i4 == BiomeGenBase.desert.biomeID
                     || i5 == BiomeGenBase.desert.biomeID
                     || i6 == BiomeGenBase.desert.biomeID
                     || i3 == BiomeGenBase.coldTaiga.biomeID
                     || i4 == BiomeGenBase.coldTaiga.biomeID
                     || i5 == BiomeGenBase.coldTaiga.biomeID
                     || i6 == BiomeGenBase.coldTaiga.biomeID
                     || i3 == BiomeGenBase.icePlains.biomeID
                     || i4 == BiomeGenBase.icePlains.biomeID
                     || i5 == BiomeGenBase.icePlains.biomeID
                     || i6 == BiomeGenBase.icePlains.biomeID) {
                     ints2[j + i * areaWidth] = BiomeGenBase.plains.biomeID;
                  } else if (i3 != BiomeGenBase.jungle.biomeID
                     && i6 != BiomeGenBase.jungle.biomeID
                     && i4 != BiomeGenBase.jungle.biomeID
                     && i5 != BiomeGenBase.jungle.biomeID) {
                     ints2[j + i * areaWidth] = k;
                  } else {
                     ints2[j + i * areaWidth] = BiomeGenBase.jungleEdge.biomeID;
                  }
               } else {
                  ints2[j + i * areaWidth] = k;
               }
            }
         }
      }

      return ints2;
   }

   private boolean replaceBiomeEdgeIfNecessary(int[] ints, int[] ints2, int i, int j, int k, int l, int m, int n) {
      if (!biomesEqualOrMesaPlateau(l, m)) {
         return false;
      }

      int i2 = ints[i + 1 + (j + 1 - 1) * (k + 2)];
      int i3 = ints[i + 1 + 1 + (j + 1) * (k + 2)];
      int i4 = ints[i + 1 - 1 + (j + 1) * (k + 2)];
      int i5 = ints[i + 1 + (j + 1 + 1) * (k + 2)];
      if (this.canBiomesBeNeighbors(i2, m)
         && this.canBiomesBeNeighbors(i3, m)
         && this.canBiomesBeNeighbors(i4, m)
         && this.canBiomesBeNeighbors(i5, m)) {
         ints2[i + j * k] = l;
      } else {
         ints2[i + j * k] = n;
      }

      return true;
   }

   private boolean replaceBiomeEdge(int[] ints, int[] ints2, int i, int j, int k, int l, int m, int n) {
      if (l != m) {
         return false;
      }

      int i2 = ints[i + 1 + (j + 1 - 1) * (k + 2)];
      int i3 = ints[i + 1 + 1 + (j + 1) * (k + 2)];
      int i4 = ints[i + 1 - 1 + (j + 1) * (k + 2)];
      int i5 = ints[i + 1 + (j + 1 + 1) * (k + 2)];
      if (biomesEqualOrMesaPlateau(i2, m) && biomesEqualOrMesaPlateau(i3, m) && biomesEqualOrMesaPlateau(i4, m) && biomesEqualOrMesaPlateau(i5, m)) {
         ints2[i + j * k] = l;
      } else {
         ints2[i + j * k] = n;
      }

      return true;
   }

   private boolean canBiomesBeNeighbors(int i, int j) {
      if (biomesEqualOrMesaPlateau(i, j)) {
         return true;
      } else {
         BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(i);
         BiomeGenBase biomeGenBase2 = BiomeGenBase.getBiome(j);
         if (biomeGenBase != null && biomeGenBase2 != null) {
            BiomeGenBase.TempCategory tempCategory = biomeGenBase.getTempCategory();
            BiomeGenBase.TempCategory tempCategory2 = biomeGenBase2.getTempCategory();
            return tempCategory == tempCategory2 || tempCategory == BiomeGenBase.TempCategory.MEDIUM || tempCategory2 == BiomeGenBase.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}
