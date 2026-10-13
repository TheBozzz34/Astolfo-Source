package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerBiomeEdge extends GenLayer {
   private static final long c = 50075045644253L;

   public GenLayerBiomeEdge(long var1, GenLayer var3) {
      super(var1);
      this.parent = var3;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var8 = this.parent.getInts(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var9 = IntCache.getIntCache(var3 * var4);

      for (int var10 = 0; var10 < var4; var10++) {
         for (int var11 = 0; var11 < var3; var11++) {
            this.initChunkSeed(var11 + var1, var10 + var2);
            int var12 = var8[var11 + 1 + (var10 + 1) * (var3 + 2)];
            if (!this.replaceBiomeEdgeIfNecessary(
                  var8, var9, var11, var10, var3, var12, BiomeGenBase.extremeHills.biomeID, BiomeGenBase.extremeHillsEdge.biomeID
               )
               && !this.replaceBiomeEdge(var8, var9, var11, var10, var3, var12, BiomeGenBase.mesaPlateau_F.biomeID, BiomeGenBase.mesa.biomeID)
               && !this.replaceBiomeEdge(var8, var9, var11, var10, var3, var12, BiomeGenBase.mesaPlateau.biomeID, BiomeGenBase.mesa.biomeID)
               && !this.replaceBiomeEdge(var8, var9, var11, var10, var3, var12, BiomeGenBase.megaTaiga.biomeID, BiomeGenBase.taiga.biomeID)) {
               if (var12 == BiomeGenBase.desert.biomeID) {
                  int var13 = var8[var11 + 1 + (var10 + 1 - 1) * (var3 + 2)];
                  int var14 = var8[var11 + 1 + 1 + (var10 + 1) * (var3 + 2)];
                  int var15 = var8[var11 + 1 - 1 + (var10 + 1) * (var3 + 2)];
                  int var16 = var8[var11 + 1 + (var10 + 1 + 1) * (var3 + 2)];
                  if (var13 != BiomeGenBase.icePlains.biomeID
                     && var14 != BiomeGenBase.icePlains.biomeID
                     && var15 != BiomeGenBase.icePlains.biomeID
                     && var16 != BiomeGenBase.icePlains.biomeID) {
                     var9[var11 + var10 * var3] = var12;
                  } else {
                     var9[var11 + var10 * var3] = BiomeGenBase.extremeHillsPlus.biomeID;
                  }
               } else if (var12 == BiomeGenBase.swampland.biomeID) {
                  int var17 = var8[var11 + 1 + (var10 + 1 - 1) * (var3 + 2)];
                  int var18 = var8[var11 + 1 + 1 + (var10 + 1) * (var3 + 2)];
                  int var19 = var8[var11 + 1 - 1 + (var10 + 1) * (var3 + 2)];
                  int var20 = var8[var11 + 1 + (var10 + 1 + 1) * (var3 + 2)];
                  if (var17 == BiomeGenBase.desert.biomeID
                     || var18 == BiomeGenBase.desert.biomeID
                     || var19 == BiomeGenBase.desert.biomeID
                     || var20 == BiomeGenBase.desert.biomeID
                     || var17 == BiomeGenBase.coldTaiga.biomeID
                     || var18 == BiomeGenBase.coldTaiga.biomeID
                     || var19 == BiomeGenBase.coldTaiga.biomeID
                     || var20 == BiomeGenBase.coldTaiga.biomeID
                     || var17 == BiomeGenBase.icePlains.biomeID
                     || var18 == BiomeGenBase.icePlains.biomeID
                     || var19 == BiomeGenBase.icePlains.biomeID
                     || var20 == BiomeGenBase.icePlains.biomeID) {
                     var9[var11 + var10 * var3] = BiomeGenBase.plains.biomeID;
                  } else if (var17 != BiomeGenBase.jungle.biomeID
                     && var20 != BiomeGenBase.jungle.biomeID
                     && var18 != BiomeGenBase.jungle.biomeID
                     && var19 != BiomeGenBase.jungle.biomeID) {
                     var9[var11 + var10 * var3] = var12;
                  } else {
                     var9[var11 + var10 * var3] = BiomeGenBase.jungleEdge.biomeID;
                  }
               } else {
                  var9[var11 + var10 * var3] = var12;
               }
            }
         }
      }

      return var9;
   }

   private boolean replaceBiomeEdgeIfNecessary(int[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (!biomesEqualOrMesaPlateau(var6, var7)) {
         return false;
      }

      int var12 = var1[var3 + 1 + (var4 + 1 - 1) * (var5 + 2)];
      int var13 = var1[var3 + 1 + 1 + (var4 + 1) * (var5 + 2)];
      int var14 = var1[var3 + 1 - 1 + (var4 + 1) * (var5 + 2)];
      int var15 = var1[var3 + 1 + (var4 + 1 + 1) * (var5 + 2)];
      if (this.canBiomesBeNeighbors(var12, var7)
         && this.canBiomesBeNeighbors(var13, var7)
         && this.canBiomesBeNeighbors(var14, var7)
         && this.canBiomesBeNeighbors(var15, var7)) {
         var2[var3 + var4 * var5] = var6;
      } else {
         var2[var3 + var4 * var5] = var8;
      }

      return true;
   }

   private boolean replaceBiomeEdge(int[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (var6 != var7) {
         return false;
      }

      int var12 = var1[var3 + 1 + (var4 + 1 - 1) * (var5 + 2)];
      int var13 = var1[var3 + 1 + 1 + (var4 + 1) * (var5 + 2)];
      int var14 = var1[var3 + 1 - 1 + (var4 + 1) * (var5 + 2)];
      int var15 = var1[var3 + 1 + (var4 + 1 + 1) * (var5 + 2)];
      if (biomesEqualOrMesaPlateau(var12, var7)
         && biomesEqualOrMesaPlateau(var13, var7)
         && biomesEqualOrMesaPlateau(var14, var7)
         && biomesEqualOrMesaPlateau(var15, var7)) {
         var2[var3 + var4 * var5] = var6;
      } else {
         var2[var3 + var4 * var5] = var8;
      }

      return true;
   }

   private boolean canBiomesBeNeighbors(int var1, int var2) {
      if (biomesEqualOrMesaPlateau(var1, var2)) {
         return true;
      } else {
         BiomeGenBase var6 = BiomeGenBase.getBiome(var1);
         BiomeGenBase var7 = BiomeGenBase.getBiome(var2);
         if (var6 != null && var7 != null) {
            BiomeGenBase.TempCategory var8 = var6.getTempCategory();
            BiomeGenBase.TempCategory var9 = var7.getTempCategory();
            return var8 == var9 || var8 == BiomeGenBase.TempCategory.MEDIUM || var9 == BiomeGenBase.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}
