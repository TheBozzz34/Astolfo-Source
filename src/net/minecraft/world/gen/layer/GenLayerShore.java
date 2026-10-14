package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;

public class GenLayerShore extends GenLayer {
   private static final long c = 23285428648181L;

   public GenLayerShore(long l, GenLayer genLayer) {
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
            BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(k);
            if (k == BiomeGenBase.mushroomIsland.biomeID) {
               int l = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
               int m = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
               int n = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
               int i2 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
               if (l != BiomeGenBase.ocean.biomeID
                  && m != BiomeGenBase.ocean.biomeID
                  && n != BiomeGenBase.ocean.biomeID
                  && i2 != BiomeGenBase.ocean.biomeID) {
                  ints2[j + i * areaWidth] = k;
               } else {
                  ints2[j + i * areaWidth] = BiomeGenBase.mushroomIslandShore.biomeID;
               }
            } else if (biomeGenBase != null && biomeGenBase.getBiomeClass() == BiomeGenJungle.class) {
               int i3 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
               int i4 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
               int i5 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
               int i6 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
               if (!this.I(i3) || !this.I(i4) || !this.I(i5) || !this.I(i6)) {
                  ints2[j + i * areaWidth] = BiomeGenBase.jungleEdge.biomeID;
               } else if (!isBiomeOceanic(i3) && !isBiomeOceanic(i4) && !isBiomeOceanic(i5) && !isBiomeOceanic(i6)) {
                  ints2[j + i * areaWidth] = k;
               } else {
                  ints2[j + i * areaWidth] = BiomeGenBase.beach.biomeID;
               }
            } else if (k == BiomeGenBase.extremeHills.biomeID
               || k == BiomeGenBase.extremeHillsPlus.biomeID
               || k == BiomeGenBase.extremeHillsEdge.biomeID) {
               this.func_151632_a(ints, ints2, j, i, areaWidth, k, BiomeGenBase.stoneBeach.biomeID);
            } else if (biomeGenBase != null && biomeGenBase.isSnowyBiome()) {
               this.func_151632_a(ints, ints2, j, i, areaWidth, k, BiomeGenBase.coldBeach.biomeID);
            } else if (k == BiomeGenBase.mesa.biomeID || k == BiomeGenBase.mesaPlateau_F.biomeID) {
               int i7 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
               int i8 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
               int i9 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
               int i10 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
               if (isBiomeOceanic(i7) || isBiomeOceanic(i8) || isBiomeOceanic(i9) || isBiomeOceanic(i10)) {
                  ints2[j + i * areaWidth] = k;
               } else if (this.p(i7) && this.p(i8) && this.p(i9) && this.p(i10)) {
                  ints2[j + i * areaWidth] = k;
               } else {
                  ints2[j + i * areaWidth] = BiomeGenBase.desert.biomeID;
               }
            } else if (k != BiomeGenBase.ocean.biomeID
               && k != BiomeGenBase.deepOcean.biomeID
               && k != BiomeGenBase.river.biomeID
               && k != BiomeGenBase.swampland.biomeID) {
               int i11 = ints[j + 1 + (i + 1 - 1) * (areaWidth + 2)];
               int i12 = ints[j + 1 + 1 + (i + 1) * (areaWidth + 2)];
               int i13 = ints[j + 1 - 1 + (i + 1) * (areaWidth + 2)];
               int i14 = ints[j + 1 + (i + 1 + 1) * (areaWidth + 2)];
               if (!isBiomeOceanic(i11) && !isBiomeOceanic(i12) && !isBiomeOceanic(i13) && !isBiomeOceanic(i14)) {
                  ints2[j + i * areaWidth] = k;
               } else {
                  ints2[j + i * areaWidth] = BiomeGenBase.beach.biomeID;
               }
            } else {
               ints2[j + i * areaWidth] = k;
            }
         }
      }

      return ints2;
   }

   private void func_151632_a(int[] ints, int[] ints2, int i, int j, int k, int l, int m) {
      if (isBiomeOceanic(l)) {
         ints2[i + j * k] = l;
      } else {
         int n = ints[i + 1 + (j + 1 - 1) * (k + 2)];
         int i2 = ints[i + 1 + 1 + (j + 1) * (k + 2)];
         int i3 = ints[i + 1 - 1 + (j + 1) * (k + 2)];
         int i4 = ints[i + 1 + (j + 1 + 1) * (k + 2)];
         if (!isBiomeOceanic(n) && !isBiomeOceanic(i2) && !isBiomeOceanic(i3) && !isBiomeOceanic(i4)) {
            ints2[i + j * k] = l;
         } else {
            ints2[i + j * k] = m;
         }
      }
   }

   private boolean I(int i) {
      return BiomeGenBase.getBiome(i) != null && BiomeGenBase.getBiome(i).getBiomeClass() == BiomeGenJungle.class
         ? true
         : i == BiomeGenBase.jungleEdge.biomeID
            || i == BiomeGenBase.jungle.biomeID
            || i == BiomeGenBase.jungleHills.biomeID
            || i == BiomeGenBase.forest.biomeID
            || i == BiomeGenBase.taiga.biomeID
            || isBiomeOceanic(i);
   }

   private boolean p(int i) {
      return BiomeGenBase.getBiome(i) instanceof BiomeGenMesa;
   }
}
