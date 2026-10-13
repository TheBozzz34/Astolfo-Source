package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa extends BiomeGenBase {
   private IBlockState[] field_150621_aC;
   private long field_150622_aD;
   private NoiseGeneratorPerlin field_150623_aE;
   private NoiseGeneratorPerlin field_150624_aF;
   private NoiseGeneratorPerlin field_150625_aG;
   private boolean field_150626_aH;
   private boolean field_150620_aI;
   private static final long bb = 128206132025083L;

   public BiomeGenMesa(int var1, boolean var2, boolean var3) {
      super(var1);
      this.field_150626_aH = var2;
      this.field_150620_aI = var3;
      this.setDisableRain();
      this.setTemperatureRainfall(2.0F, 0.0F);
      this.spawnableCreatureList.clear();
      this.topBlock = Blocks.sand.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND);
      this.fillerBlock = Blocks.stained_hardened_clay.getDefaultState();
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.deadBushPerChunk = 20;
      this.theBiomeDecorator.reedsPerChunk = 3;
      this.theBiomeDecorator.cactiPerChunk = 5;
      this.theBiomeDecorator.flowersPerChunk = 0;
      this.spawnableCreatureList.clear();
      if (var3) {
         this.theBiomeDecorator.treesPerChunk = 5;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random var1) {
      return this.worldGeneratorTrees;
   }

   @Override
   public int getFoliageColorAtPos(BlockPos var1) {
      return 10387789;
   }

   @Override
   public int getGrassColorAtPos(BlockPos var1) {
      return 9470285;
   }

   @Override
   public void decorate(World var1, Random var2, BlockPos var3) {
      super.decorate(var1, var2, var3);
   }

   @Override
   public void genTerrainBlocks(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      long var8 = bb ^ 98401691192015L;
      if (this.field_150621_aC == null || this.field_150622_aD != var1.getSeed()) {
         this.func_150619_a(var1.getSeed());
      }

      if (this.field_150623_aE == null || this.field_150624_aF == null || this.field_150622_aD != var1.getSeed()) {
         Random var11 = new Random(this.field_150622_aD);
         this.field_150623_aE = new NoiseGeneratorPerlin(var11, 4);
         this.field_150624_aF = new NoiseGeneratorPerlin(var11, 1);
      }

      this.field_150622_aD = var1.getSeed();
      double var25 = 0.0;
      if (this.field_150626_aH) {
         int var13 = (var4 & -16) + (var5 & 15);
         int var14 = (var5 & -16) + (var4 & 15);
         double var15 = Math.min(Math.abs(var6), this.field_150623_aE.func_151601_a(var13 * 0.25, var14 * 0.25));
         if (var15 > 0.0) {
            double var17 = 0.001953125;
            double var19 = Math.abs(this.field_150624_aF.func_151601_a(var13 * var17, var14 * var17));
            var25 = var15 * var15 * 2.5;
            double var21 = Math.ceil(var19 * 50.0) + 14.0;
            if (var25 > var21) {
               var25 = var21;
            }

            var25 += 64.0;
         }
      }

      int var27 = var4 & 15;
      int var28 = var5 & 15;
      int var29 = var1.getSeaLevel();
      IBlockState var16 = Blocks.stained_hardened_clay.getDefaultState();
      IBlockState var30 = this.fillerBlock;
      int var18 = (int)(var6 / 3.0 + 3.0 + var2.nextDouble() * 0.25);
      boolean var31 = Math.cos(var6 / 3.0 * Math.PI) > 0.0;
      int var20 = -1;
      long var10000 = 936363570082422977L ^ var8;
      boolean var32 = false;

      for (int var22 = 255; var22 >= 0; var22--) {
         if (var3.getBlockState(var28, var22, var27).getBlock().getMaterial() == Material.air && var22 < (int)var25) {
            var3.setBlockState(var28, var22, var27, Blocks.stone.getDefaultState());
         }

         if (var22 <= var2.nextInt(5)) {
            var3.setBlockState(var28, var22, var27, Blocks.bedrock.getDefaultState());
         } else {
            IBlockState var23 = var3.getBlockState(var28, var22, var27);
            if (var23.getBlock().getMaterial() == Material.air) {
               var20 = -1;
            } else if (var23.getBlock() == Blocks.stone) {
               if (var20 == -1) {
                  var32 = false;
                  if (var18 <= 0) {
                     var16 = null;
                     var30 = Blocks.stone.getDefaultState();
                  } else if (var22 >= var29 - 4 && var22 <= var29 + 1) {
                     var16 = Blocks.stained_hardened_clay.getDefaultState();
                     var30 = this.fillerBlock;
                  }

                  if (var22 < var29 && (var16 == null || var16.getBlock().getMaterial() == Material.air)) {
                     var16 = Blocks.water.getDefaultState();
                  }

                  var20 = var18 + Math.max(0, var22 - var29);
                  if (var22 < var29 - 1) {
                     var3.setBlockState(var28, var22, var27, var30);
                     if (var30.getBlock() == Blocks.stained_hardened_clay) {
                        var3.setBlockState(var28, var22, var27, var30.getBlock().getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE));
                     }
                  } else if (!this.field_150620_aI || var22 <= 86 + var18 * 2) {
                     if (var22 <= var29 + 3 + var18) {
                        var3.setBlockState(var28, var22, var27, this.topBlock);
                        var32 = true;
                     } else {
                        IBlockState var24;
                        if (var22 < 64 || var22 > 127) {
                           var24 = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
                        } else if (var31) {
                           var24 = Blocks.hardened_clay.getDefaultState();
                        } else {
                           var24 = this.func_180629_a(var4, var22, var5);
                        }

                        var3.setBlockState(var28, var22, var27, var24);
                     }
                  } else if (var31) {
                     var3.setBlockState(var28, var22, var27, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
                  } else {
                     var3.setBlockState(var28, var22, var27, Blocks.grass.getDefaultState());
                  }
               } else if (var20 > 0) {
                  var20--;
                  if (var32) {
                     var3.setBlockState(
                        var28, var22, var27, Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE)
                     );
                  } else {
                     IBlockState var33 = this.func_180629_a(var4, var22, var5);
                     var3.setBlockState(var28, var22, var27, var33);
                  }
               }
            }
         }
      }
   }

   private void func_150619_a(long var1) {
      this.field_150621_aC = new IBlockState[64];
      Arrays.fill(this.field_150621_aC, Blocks.hardened_clay.getDefaultState());
      Random var6 = new Random(var1);
      this.field_150625_aG = new NoiseGeneratorPerlin(var6, 1);

      for (int var15 = 0; var15 < 64; var15++) {
         var15 += var6.nextInt(5) + 1;
         if (var15 < 64) {
            this.field_150621_aC[var15] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
         }
      }

      int var16 = var6.nextInt(4) + 2;

      for (int var8 = 0; var8 < var16; var8++) {
         int var9 = var6.nextInt(3) + 1;
         int var10 = var6.nextInt(64);

         for (int var11 = 0; var10 + var11 < 64 && var11 < var9; var11++) {
            this.field_150621_aC[var10 + var11] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
         }
      }

      int var17 = var6.nextInt(4) + 2;

      for (int var18 = 0; var18 < var17; var18++) {
         int var20 = var6.nextInt(3) + 2;
         int var23 = var6.nextInt(64);

         for (int var12 = 0; var23 + var12 < 64 && var12 < var20; var12++) {
            this.field_150621_aC[var23 + var12] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
         }
      }

      int var19 = var6.nextInt(4) + 2;

      for (int var21 = 0; var21 < var19; var21++) {
         int var24 = var6.nextInt(3) + 1;
         int var26 = var6.nextInt(64);

         for (int var13 = 0; var26 + var13 < 64 && var13 < var24; var13++) {
            this.field_150621_aC[var26 + var13] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
         }
      }

      int var22 = var6.nextInt(3) + 3;
      int var25 = 0;

      for (int var27 = 0; var27 < var22; var27++) {
         byte var28 = 1;
         var25 += var6.nextInt(16) + 4;

         for (int var14 = 0; var25 + var14 < 64 && var14 < var28; var14++) {
            this.field_150621_aC[var25 + var14] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
            if (var25 + var14 > 1 && var6.nextBoolean()) {
               this.field_150621_aC[var25 + var14 - 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }

            if (var25 + var14 < 63 && var6.nextBoolean()) {
               this.field_150621_aC[var25 + var14 + 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }
         }
      }
   }

   private IBlockState func_180629_a(int var1, int var2, int var3) {
      int var6 = (int)Math.round(this.field_150625_aG.func_151601_a(var1 * 1.0 / 512.0, var1 * 1.0 / 512.0) * 2.0);
      return this.field_150621_aC[(var2 + var6 + 64) % 64];
   }

   @Override
   public BiomeGenBase createMutatedBiome(int var1) {
      boolean var5 = this.biomeID == BiomeGenBase.mesa.biomeID;
      BiomeGenMesa var6 = new BiomeGenMesa(var1, var5, this.field_150620_aI);
      if (!var5) {
         var6.setHeight(height_LowHills);
         var6.setBiomeName(this.biomeName + " M");
      } else {
         var6.setBiomeName(this.biomeName + " (Bryce)");
      }

      var6.func_150557_a(this.color, true);
      return var6;
   }
}
