package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {
   private static final Logger logger = LogManager.getLogger();
   private GenLayer field_151628_d;
   private static final long c = 72549392538923L;

   public GenLayerHills(long var1, GenLayer var3, GenLayer var4) {
      super(var1);
      this.parent = var3;
      this.field_151628_d = var4;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int[] var8 = this.parent.getInts(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var9 = this.field_151628_d.getInts(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var10 = IntCache.getIntCache(var3 * var4);

      for (int var11 = 0; var11 < var4; var11++) {
         for (int var12 = 0; var12 < var3; var12++) {
            this.initChunkSeed(var12 + var1, var11 + var2);
            int var13 = var8[var12 + 1 + (var11 + 1) * (var3 + 2)];
            int var14 = var9[var12 + 1 + (var11 + 1) * (var3 + 2)];
            boolean var15 = (var14 - 2) % 29 == 0;
            if (var13 > 255) {
               logger.debug("old! " + var13);
            }

            if (var13 != 0 && var14 >= 2 && (var14 - 2) % 29 == 1 && var13 < 128) {
               if (BiomeGenBase.getBiome(var13 + 128) != null) {
                  var10[var12 + var11 * var3] = var13 + 128;
               } else {
                  var10[var12 + var11 * var3] = var13;
               }
            } else if (this.nextInt(3) != 0 && !var15) {
               var10[var12 + var11 * var3] = var13;
            } else {
               int var16 = var13;
               if (var13 == BiomeGenBase.desert.biomeID) {
                  var16 = BiomeGenBase.desertHills.biomeID;
               } else if (var13 == BiomeGenBase.forest.biomeID) {
                  var16 = BiomeGenBase.forestHills.biomeID;
               } else if (var13 == BiomeGenBase.birchForest.biomeID) {
                  var16 = BiomeGenBase.birchForestHills.biomeID;
               } else if (var13 == BiomeGenBase.roofedForest.biomeID) {
                  var16 = BiomeGenBase.plains.biomeID;
               } else if (var13 == BiomeGenBase.taiga.biomeID) {
                  var16 = BiomeGenBase.taigaHills.biomeID;
               } else if (var13 == BiomeGenBase.megaTaiga.biomeID) {
                  var16 = BiomeGenBase.megaTaigaHills.biomeID;
               } else if (var13 == BiomeGenBase.coldTaiga.biomeID) {
                  var16 = BiomeGenBase.coldTaigaHills.biomeID;
               } else if (var13 == BiomeGenBase.plains.biomeID) {
                  if (this.nextInt(3) == 0) {
                     var16 = BiomeGenBase.forestHills.biomeID;
                  } else {
                     var16 = BiomeGenBase.forest.biomeID;
                  }
               } else if (var13 == BiomeGenBase.icePlains.biomeID) {
                  var16 = BiomeGenBase.iceMountains.biomeID;
               } else if (var13 == BiomeGenBase.jungle.biomeID) {
                  var16 = BiomeGenBase.jungleHills.biomeID;
               } else if (var13 == BiomeGenBase.ocean.biomeID) {
                  var16 = BiomeGenBase.deepOcean.biomeID;
               } else if (var13 == BiomeGenBase.extremeHills.biomeID) {
                  var16 = BiomeGenBase.extremeHillsPlus.biomeID;
               } else if (var13 == BiomeGenBase.savanna.biomeID) {
                  var16 = BiomeGenBase.savannaPlateau.biomeID;
               } else if (biomesEqualOrMesaPlateau(var13, BiomeGenBase.mesaPlateau_F.biomeID)) {
                  var16 = BiomeGenBase.mesa.biomeID;
               } else if (var13 == BiomeGenBase.deepOcean.biomeID && this.nextInt(3) == 0) {
                  int var17 = this.nextInt(2);
                  if (var17 == 0) {
                     var16 = BiomeGenBase.plains.biomeID;
                  } else {
                     var16 = BiomeGenBase.forest.biomeID;
                  }
               }

               if (var15 && var16 != var13) {
                  if (BiomeGenBase.getBiome(var16 + 128) != null) {
                     var16 += 128;
                  } else {
                     var16 = var13;
                  }
               }

               if (var16 == var13) {
                  var10[var12 + var11 * var3] = var13;
               } else {
                  int var22 = var8[var12 + 1 + (var11 + 1 - 1) * (var3 + 2)];
                  int var18 = var8[var12 + 1 + 1 + (var11 + 1) * (var3 + 2)];
                  int var19 = var8[var12 + 1 - 1 + (var11 + 1) * (var3 + 2)];
                  int var20 = var8[var12 + 1 + (var11 + 1 + 1) * (var3 + 2)];
                  int var21 = 0;
                  if (biomesEqualOrMesaPlateau(var22, var13)) {
                     var21++;
                  }

                  if (biomesEqualOrMesaPlateau(var18, var13)) {
                     var21++;
                  }

                  if (biomesEqualOrMesaPlateau(var19, var13)) {
                     var21++;
                  }

                  if (biomesEqualOrMesaPlateau(var20, var13)) {
                     var21++;
                  }

                  if (var21 >= 3) {
                     var10[var12 + var11 * var3] = var16;
                  } else {
                     var10[var12 + var11 * var3] = var13;
                  }
               }
            }
         }
      }

      return var10;
   }
}
