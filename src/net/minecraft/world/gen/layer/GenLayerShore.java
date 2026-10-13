package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;

public class GenLayerShore extends GenLayer {
   private static final long c = 23285428648181L;

   public GenLayerShore(long var1, GenLayer var3) {
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
            BiomeGenBase var13 = BiomeGenBase.getBiome(var12);
            if (var12 == BiomeGenBase.mushroomIsland.biomeID) {
               int var14 = var8[var11 + 1 + (var10 + 1 - 1) * (var3 + 2)];
               int var15 = var8[var11 + 1 + 1 + (var10 + 1) * (var3 + 2)];
               int var16 = var8[var11 + 1 - 1 + (var10 + 1) * (var3 + 2)];
               int var17 = var8[var11 + 1 + (var10 + 1 + 1) * (var3 + 2)];
               if (var14 != BiomeGenBase.ocean.biomeID
                  && var15 != BiomeGenBase.ocean.biomeID
                  && var16 != BiomeGenBase.ocean.biomeID
                  && var17 != BiomeGenBase.ocean.biomeID) {
                  var9[var11 + var10 * var3] = var12;
               } else {
                  var9[var11 + var10 * var3] = BiomeGenBase.mushroomIslandShore.biomeID;
               }
            } else if (var13 != null && var13.getBiomeClass() == BiomeGenJungle.class) {
               int var20 = var8[var11 + 1 + (var10 + 1 - 1) * (var3 + 2)];
               int var23 = var8[var11 + 1 + 1 + (var10 + 1) * (var3 + 2)];
               int var26 = var8[var11 + 1 - 1 + (var10 + 1) * (var3 + 2)];
               int var29 = var8[var11 + 1 + (var10 + 1 + 1) * (var3 + 2)];
               if (!this.I(var20) || !this.I(var23) || !this.I(var26) || !this.I(var29)) {
                  var9[var11 + var10 * var3] = BiomeGenBase.jungleEdge.biomeID;
               } else if (!isBiomeOceanic(var20) && !isBiomeOceanic(var23) && !isBiomeOceanic(var26) && !isBiomeOceanic(var29)) {
                  var9[var11 + var10 * var3] = var12;
               } else {
                  var9[var11 + var10 * var3] = BiomeGenBase.beach.biomeID;
               }
            } else if (var12 == BiomeGenBase.extremeHills.biomeID
               || var12 == BiomeGenBase.extremeHillsPlus.biomeID
               || var12 == BiomeGenBase.extremeHillsEdge.biomeID) {
               this.func_151632_a(var8, var9, var11, var10, var3, var12, BiomeGenBase.stoneBeach.biomeID);
            } else if (var13 != null && var13.isSnowyBiome()) {
               this.func_151632_a(var8, var9, var11, var10, var3, var12, BiomeGenBase.coldBeach.biomeID);
            } else if (var12 == BiomeGenBase.mesa.biomeID || var12 == BiomeGenBase.mesaPlateau_F.biomeID) {
               int var19 = var8[var11 + 1 + (var10 + 1 - 1) * (var3 + 2)];
               int var22 = var8[var11 + 1 + 1 + (var10 + 1) * (var3 + 2)];
               int var25 = var8[var11 + 1 - 1 + (var10 + 1) * (var3 + 2)];
               int var28 = var8[var11 + 1 + (var10 + 1 + 1) * (var3 + 2)];
               if (isBiomeOceanic(var19) || isBiomeOceanic(var22) || isBiomeOceanic(var25) || isBiomeOceanic(var28)) {
                  var9[var11 + var10 * var3] = var12;
               } else if (this.p(var19) && this.p(var22) && this.p(var25) && this.p(var28)) {
                  var9[var11 + var10 * var3] = var12;
               } else {
                  var9[var11 + var10 * var3] = BiomeGenBase.desert.biomeID;
               }
            } else if (var12 != BiomeGenBase.ocean.biomeID
               && var12 != BiomeGenBase.deepOcean.biomeID
               && var12 != BiomeGenBase.river.biomeID
               && var12 != BiomeGenBase.swampland.biomeID) {
               int var18 = var8[var11 + 1 + (var10 + 1 - 1) * (var3 + 2)];
               int var21 = var8[var11 + 1 + 1 + (var10 + 1) * (var3 + 2)];
               int var24 = var8[var11 + 1 - 1 + (var10 + 1) * (var3 + 2)];
               int var27 = var8[var11 + 1 + (var10 + 1 + 1) * (var3 + 2)];
               if (!isBiomeOceanic(var18) && !isBiomeOceanic(var21) && !isBiomeOceanic(var24) && !isBiomeOceanic(var27)) {
                  var9[var11 + var10 * var3] = var12;
               } else {
                  var9[var11 + var10 * var3] = BiomeGenBase.beach.biomeID;
               }
            } else {
               var9[var11 + var10 * var3] = var12;
            }
         }
      }

      return var9;
   }

   private void func_151632_a(int[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7) {
      if (isBiomeOceanic(var6)) {
         var2[var3 + var4 * var5] = var6;
      } else {
         int var11 = var1[var3 + 1 + (var4 + 1 - 1) * (var5 + 2)];
         int var12 = var1[var3 + 1 + 1 + (var4 + 1) * (var5 + 2)];
         int var13 = var1[var3 + 1 - 1 + (var4 + 1) * (var5 + 2)];
         int var14 = var1[var3 + 1 + (var4 + 1 + 1) * (var5 + 2)];
         if (!isBiomeOceanic(var11) && !isBiomeOceanic(var12) && !isBiomeOceanic(var13) && !isBiomeOceanic(var14)) {
            var2[var3 + var4 * var5] = var6;
         } else {
            var2[var3 + var4 * var5] = var7;
         }
      }
   }

   private boolean I(int var1) {
      return BiomeGenBase.getBiome(var1) != null && BiomeGenBase.getBiome(var1).getBiomeClass() == BiomeGenJungle.class
         ? true
         : var1 == BiomeGenBase.jungleEdge.biomeID
            || var1 == BiomeGenBase.jungle.biomeID
            || var1 == BiomeGenBase.jungleHills.biomeID
            || var1 == BiomeGenBase.forest.biomeID
            || var1 == BiomeGenBase.taiga.biomeID
            || isBiomeOceanic(var1);
   }

   private boolean p(int var1) {
      return BiomeGenBase.getBiome(var1) instanceof BiomeGenMesa;
   }
}
