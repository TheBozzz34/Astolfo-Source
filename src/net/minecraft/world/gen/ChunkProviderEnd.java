package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEnd implements IChunkProvider {
   private Random endRNG;
   private NoiseGeneratorOctaves noiseGen1;
   private NoiseGeneratorOctaves noiseGen2;
   private NoiseGeneratorOctaves noiseGen3;
   public NoiseGeneratorOctaves noiseGen4;
   public NoiseGeneratorOctaves noiseGen5;
   private World endWorld;
   private double[] densities;
   private BiomeGenBase[] biomesForGeneration;
   public double[] noiseData1;
   public double[] noiseData2;
   public double[] noiseData3;
   public double[] noiseData4;
   public double[] noiseData5;
   private static final long a = 17716181717184L;

   public ChunkProviderEnd(World var1, long var2) {
      this.endWorld = var1;
      this.endRNG = new Random(var2);
      this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
      this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
      this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
      this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
      this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);
   }

   public void func_180520_a(int var1, int var2, ChunkPrimer var3) {
      byte var7 = 2;
      int var8 = var7 + 1;
      byte var9 = 33;
      int var10 = var7 + 1;
      this.densities = this.initializeNoiseField(this.densities, var1 * var7, 0, var2 * var7, var8, var9, var10);

      for (int var11 = 0; var11 < var7; var11++) {
         for (int var12 = 0; var12 < var7; var12++) {
            for (int var13 = 0; var13 < 32; var13++) {
               double var14 = 0.25;
               double var16 = this.densities[((var11 + 0) * var10 + var12 + 0) * var9 + var13 + 0];
               double var18 = this.densities[((var11 + 0) * var10 + var12 + 1) * var9 + var13 + 0];
               double var20 = this.densities[((var11 + 1) * var10 + var12 + 0) * var9 + var13 + 0];
               double var22 = this.densities[((var11 + 1) * var10 + var12 + 1) * var9 + var13 + 0];
               double var24 = (this.densities[((var11 + 0) * var10 + var12 + 0) * var9 + var13 + 1] - var16) * var14;
               double var26 = (this.densities[((var11 + 0) * var10 + var12 + 1) * var9 + var13 + 1] - var18) * var14;
               double var28 = (this.densities[((var11 + 1) * var10 + var12 + 0) * var9 + var13 + 1] - var20) * var14;
               double var30 = (this.densities[((var11 + 1) * var10 + var12 + 1) * var9 + var13 + 1] - var22) * var14;

               for (int var32 = 0; var32 < 4; var32++) {
                  double var33 = 0.125;
                  double var35 = var16;
                  double var37 = var18;
                  double var39 = (var20 - var16) * var33;
                  double var41 = (var22 - var18) * var33;

                  for (int var43 = 0; var43 < 8; var43++) {
                     double var44 = 0.125;
                     double var46 = var35;
                     double var48 = (var37 - var35) * var44;

                     for (int var50 = 0; var50 < 8; var50++) {
                        IBlockState var51 = null;
                        if (var46 > 0.0) {
                           var51 = Blocks.end_stone.getDefaultState();
                        }

                        int var52 = var43 + var11 * 8;
                        int var53 = var32 + var13 * 4;
                        int var54 = var50 + var12 * 8;
                        var3.setBlockState(var52, var53, var54, var51);
                        var46 += var48;
                     }

                     var35 += var39;
                     var37 += var41;
                  }

                  var16 += var24;
                  var18 += var26;
                  var20 += var28;
                  var22 += var30;
               }
            }
         }
      }
   }

   public void func_180519_a(ChunkPrimer var1) {
      for (int var5 = 0; var5 < 16; var5++) {
         for (int var6 = 0; var6 < 16; var6++) {
            byte var7 = 1;
            int var8 = -1;
            IBlockState var9 = Blocks.end_stone.getDefaultState();
            IBlockState var10 = Blocks.end_stone.getDefaultState();

            for (int var11 = 127; var11 >= 0; var11--) {
               IBlockState var12 = var1.getBlockState(var5, var11, var6);
               if (var12.getBlock().getMaterial() == Material.air) {
                  var8 = -1;
               } else if (var12.getBlock() == Blocks.stone) {
                  if (var8 == -1) {
                     if (var7 <= 0) {
                        var9 = Blocks.air.getDefaultState();
                        var10 = Blocks.end_stone.getDefaultState();
                     }

                     var8 = var7;
                     if (var11 >= 0) {
                        var1.setBlockState(var5, var11, var6, var9);
                     } else {
                        var1.setBlockState(var5, var11, var6, var10);
                     }
                  } else if (var8 > 0) {
                     var8--;
                     var1.setBlockState(var5, var11, var6, var10);
                  }
               }
            }
         }
      }
   }

   @Override
   public Chunk provideChunk(int var1, int var2) {
      this.endRNG.setSeed(var1 * 341873128712L + var2 * 132897987541L);
      ChunkPrimer var6 = new ChunkPrimer();
      this.biomesForGeneration = this.endWorld.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, var1 * 16, var2 * 16, 16, 16);
      this.func_180520_a(var1, var2, var6);
      this.func_180519_a(var6);
      Chunk var7 = new Chunk(this.endWorld, var6, var1, var2);
      byte[] var8 = var7.getBiomeArray();

      for (int var9 = 0; var9 < var8.length; var9++) {
         var8[var9] = (byte)this.biomesForGeneration[var9].biomeID;
      }

      var7.generateSkylightMap();
      return var7;
   }

   private double[] initializeNoiseField(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1 == null) {
         var1 = new double[var5 * var6 * var7];
      }

      double var11 = 684.412;
      double var13 = 684.412;
      this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, var2, var4, var5, var7, 1.121, 1.121, 0.5);
      this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, var2, var4, var5, var7, 200.0, 200.0, 0.5);
      var11 *= 2.0;
      this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, var2, var3, var4, var5, var6, var7, var11 / 80.0, var13 / 160.0, var11 / 80.0);
      this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, var2, var3, var4, var5, var6, var7, var11, var13, var11);
      this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, var2, var3, var4, var5, var6, var7, var11, var13, var11);
      int var15 = 0;

      for (int var16 = 0; var16 < var5; var16++) {
         for (int var17 = 0; var17 < var7; var17++) {
            float var18 = (var16 + var2) / 1.0F;
            float var19 = (var17 + var4) / 1.0F;
            float var20 = 100.0F - MathHelper.sqrt_float(var18 * var18 + var19 * var19) * 8.0F;
            if (var20 > 80.0F) {
               var20 = 80.0F;
            }

            if (var20 < -100.0F) {
               var20 = -100.0F;
            }

            for (int var21 = 0; var21 < var6; var21++) {
               double var22 = 0.0;
               double var24 = this.noiseData2[var15] / 512.0;
               double var26 = this.noiseData3[var15] / 512.0;
               double var28 = (this.noiseData1[var15] / 10.0 + 1.0) / 2.0;
               if (var28 < 0.0) {
                  var22 = var24;
               } else if (var28 > 1.0) {
                  var22 = var26;
               } else {
                  var22 = var24 + (var26 - var24) * var28;
               }

               var22 -= 8.0;
               var22 += var20;
               byte var30 = 2;
               if (var21 > var6 / 2 - var30) {
                  double var31 = (var21 - (var6 / 2 - var30)) / 64.0F;
                  var31 = MathHelper.clamp_double(var31, 0.0, 1.0);
                  var22 = var22 * (1.0 - var31) + -3000.0 * var31;
               }

               var30 = 8;
               if (var21 < var30) {
                  double var39 = (var30 - var21) / (var30 - 1.0F);
                  var22 = var22 * (1.0 - var39) + -30.0 * var39;
               }

               var1[var15] = var22;
               var15++;
            }
         }
      }

      return var1;
   }

   @Override
   public boolean chunkExists(int var1, int var2) {
      return true;
   }

   @Override
   public void populate(IChunkProvider var1, int var2, int var3) {
      BlockFalling.fallInstantly = true;
      BlockPos var6 = new BlockPos(var2 * 16, 0, var3 * 16);
      this.endWorld.getBiomeGenForCoords(var6.add(16, 0, 16)).decorate(this.endWorld, this.endWorld.rand, var6);
      BlockFalling.fallInstantly = false;
   }

   @Override
   public boolean populateChunk(IChunkProvider var1, Chunk var2, int var3, int var4) {
      return false;
   }

   @Override
   public boolean saveChunks(boolean var1, IProgressUpdate var2) {
      return true;
   }

   @Override
   public void saveExtraData() {
   }

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public String makeString() {
      return "RandomLevelSource";
   }

   @Override
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType var1, BlockPos var2) {
      return this.endWorld.getBiomeGenForCoords(var2).getSpawnableList(var1);
   }

   @Override
   public BlockPos getStrongholdGen(World var1, String var2, BlockPos var3) {
      return null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk var1, int var2, int var3) {
   }

   @Override
   public Chunk provideChunk(BlockPos var1) {
      return this.provideChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }
}
