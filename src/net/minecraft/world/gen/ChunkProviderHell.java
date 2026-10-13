package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider {
   private final World worldObj;
   private final boolean field_177466_i;
   private final Random hellRNG;
   private double[] slowsandNoise = new double[256];
   private double[] gravelNoise = new double[256];
   private double[] netherrackExclusivityNoise = new double[256];
   private double[] noiseField;
   private final NoiseGeneratorOctaves netherNoiseGen1;
   private final NoiseGeneratorOctaves netherNoiseGen2;
   private final NoiseGeneratorOctaves netherNoiseGen3;
   private final NoiseGeneratorOctaves slowsandGravelNoiseGen;
   private final NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
   public final NoiseGeneratorOctaves netherNoiseGen6;
   public final NoiseGeneratorOctaves netherNoiseGen7;
   private final WorldGenFire field_177470_t = new WorldGenFire();
   private final WorldGenGlowStone1 field_177469_u = new WorldGenGlowStone1();
   private final WorldGenGlowStone2 field_177468_v = new WorldGenGlowStone2();
   private final WorldGenerator field_177467_w = new WorldGenMinable(Blocks.quartz_ore.getDefaultState(), 14, BlockHelper.forBlock(Blocks.netherrack));
   private final WorldGenHellLava field_177473_x = new WorldGenHellLava(Blocks.flowing_lava, true);
   private final WorldGenHellLava field_177472_y = new WorldGenHellLava(Blocks.flowing_lava, false);
   private final GeneratorBushFeature field_177471_z = new GeneratorBushFeature(Blocks.brown_mushroom);
   private final GeneratorBushFeature field_177465_A = new GeneratorBushFeature(Blocks.red_mushroom);
   private final MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
   private final MapGenBase netherCaveGenerator = new MapGenCavesHell();
   public double[] noiseData1;
   public double[] noiseData2;
   public double[] noiseData3;
   public double[] noiseData4;
   public double[] noiseData5;
   private static final long e = 95312648725581L;

   public ChunkProviderHell(World var1, boolean var2, long var3) {
      this.worldObj = var1;
      this.field_177466_i = var2;
      this.hellRNG = new Random(var3);
      this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
      this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
      this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      var1.setSeaLevel(63);
   }

   public void func_180515_a(int var1, int var2, ChunkPrimer var3) {
      byte var7 = 4;
      int var8 = this.worldObj.getSeaLevel() / 2 + 1;
      int var9 = var7 + 1;
      byte var10 = 17;
      int var11 = var7 + 1;
      this.noiseField = this.initializeNoiseField(this.noiseField, var1 * var7, 0, var2 * var7, var9, var10, var11);

      for (int var12 = 0; var12 < var7; var12++) {
         for (int var13 = 0; var13 < var7; var13++) {
            for (int var14 = 0; var14 < 16; var14++) {
               double var15 = 0.125;
               double var17 = this.noiseField[((var12 + 0) * var11 + var13 + 0) * var10 + var14 + 0];
               double var19 = this.noiseField[((var12 + 0) * var11 + var13 + 1) * var10 + var14 + 0];
               double var21 = this.noiseField[((var12 + 1) * var11 + var13 + 0) * var10 + var14 + 0];
               double var23 = this.noiseField[((var12 + 1) * var11 + var13 + 1) * var10 + var14 + 0];
               double var25 = (this.noiseField[((var12 + 0) * var11 + var13 + 0) * var10 + var14 + 1] - var17) * var15;
               double var27 = (this.noiseField[((var12 + 0) * var11 + var13 + 1) * var10 + var14 + 1] - var19) * var15;
               double var29 = (this.noiseField[((var12 + 1) * var11 + var13 + 0) * var10 + var14 + 1] - var21) * var15;
               double var31 = (this.noiseField[((var12 + 1) * var11 + var13 + 1) * var10 + var14 + 1] - var23) * var15;

               for (int var33 = 0; var33 < 8; var33++) {
                  double var34 = 0.25;
                  double var36 = var17;
                  double var38 = var19;
                  double var40 = (var21 - var17) * var34;
                  double var42 = (var23 - var19) * var34;

                  for (int var44 = 0; var44 < 4; var44++) {
                     double var45 = 0.25;
                     double var47 = var36;
                     double var49 = (var38 - var36) * var45;

                     for (int var51 = 0; var51 < 4; var51++) {
                        IBlockState var52 = null;
                        if (var14 * 8 + var33 < var8) {
                           var52 = Blocks.lava.getDefaultState();
                        }

                        if (var47 > 0.0) {
                           var52 = Blocks.netherrack.getDefaultState();
                        }

                        int var53 = var44 + var12 * 4;
                        int var54 = var33 + var14 * 8;
                        int var55 = var51 + var13 * 4;
                        var3.setBlockState(var53, var54, var55, var52);
                        var47 += var49;
                     }

                     var36 += var40;
                     var38 += var42;
                  }

                  var17 += var25;
                  var19 += var27;
                  var21 += var29;
                  var23 += var31;
               }
            }
         }
      }
   }

   public void func_180516_b(int var1, int var2, ChunkPrimer var3) {
      long var4 = e ^ 70110039423045L;
      int var7 = this.worldObj.getSeaLevel() + 1;
      double var8 = 0.03125;
      this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, var1 * 16, var2 * 16, 0, 16, 16, 1, var8, var8, 1.0);
      this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, var1 * 16, 109, var2 * 16, 16, 1, 16, var8, 1.0, var8);
      this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen
         .generateNoiseOctaves(this.netherrackExclusivityNoise, var1 * 16, var2 * 16, 0, 16, 16, 1, var8 * 2.0, var8 * 2.0, var8 * 2.0);

      for (int var10 = 0; var10 < 16; var10++) {
         for (int var11 = 0; var11 < 16; var11++) {
            boolean var12 = this.slowsandNoise[var10 + var11 * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            boolean var13 = this.gravelNoise[var10 + var11 * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            int var14 = (int)(this.netherrackExclusivityNoise[var10 + var11 * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
            int var15 = -1;
            long var10000 = 4084935087139344066L ^ var4;
            IBlockState var16 = Blocks.netherrack.getDefaultState();
            IBlockState var17 = Blocks.netherrack.getDefaultState();

            for (int var18 = 127; var18 >= 0; var18--) {
               if (var18 < 127 - this.hellRNG.nextInt(5) && var18 > this.hellRNG.nextInt(5)) {
                  IBlockState var19 = var3.getBlockState(var11, var18, var10);
                  if (var19.getBlock() == null || var19.getBlock().getMaterial() == Material.air) {
                     var15 = -1;
                  } else if (var19.getBlock() == Blocks.netherrack) {
                     if (var15 == -1) {
                        if (var14 <= 0) {
                           var16 = null;
                           var17 = Blocks.netherrack.getDefaultState();
                        } else if (var18 >= var7 - 4 && var18 <= var7 + 1) {
                           var16 = Blocks.netherrack.getDefaultState();
                           var17 = Blocks.netherrack.getDefaultState();
                           if (var13) {
                              var16 = Blocks.gravel.getDefaultState();
                              var17 = Blocks.netherrack.getDefaultState();
                           }

                           if (var12) {
                              var16 = Blocks.soul_sand.getDefaultState();
                              var17 = Blocks.soul_sand.getDefaultState();
                           }
                        }

                        if (var18 < var7 && (var16 == null || var16.getBlock().getMaterial() == Material.air)) {
                           var16 = Blocks.lava.getDefaultState();
                        }

                        var15 = var14;
                        if (var18 >= var7 - 1) {
                           var3.setBlockState(var11, var18, var10, var16);
                        } else {
                           var3.setBlockState(var11, var18, var10, var17);
                        }
                     } else if (var15 > 0) {
                        var15--;
                        var3.setBlockState(var11, var18, var10, var17);
                     }
                  }
               } else {
                  var3.setBlockState(var11, var18, var10, Blocks.bedrock.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public Chunk provideChunk(int var1, int var2) {
      this.hellRNG.setSeed(var1 * 341873128712L + var2 * 132897987541L);
      ChunkPrimer var6 = new ChunkPrimer();
      this.func_180515_a(var1, var2, var6);
      this.func_180516_b(var1, var2, var6);
      this.netherCaveGenerator.generate(this, this.worldObj, var1, var2, var6);
      if (this.field_177466_i) {
         this.genNetherBridge.generate(this, this.worldObj, var1, var2, var6);
      }

      Chunk var7 = new Chunk(this.worldObj, var6, var1, var2);
      BiomeGenBase[] var8 = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, var1 * 16, var2 * 16, 16, 16);
      byte[] var9 = var7.getBiomeArray();

      for (int var10 = 0; var10 < var9.length; var10++) {
         var9[var10] = (byte)var8[var10].biomeID;
      }

      var7.resetRelightChecks();
      return var7;
   }

   private double[] initializeNoiseField(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1 == null) {
         var1 = new double[var5 * var6 * var7];
      }

      double var11 = 684.412;
      double var13 = 2053.236;
      this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, var2, var3, var4, var5, 1, var7, 1.0, 0.0, 1.0);
      this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, var2, var3, var4, var5, 1, var7, 100.0, 0.0, 100.0);
      this.noiseData1 = this.netherNoiseGen3
         .generateNoiseOctaves(this.noiseData1, var2, var3, var4, var5, var6, var7, var11 / 80.0, var13 / 60.0, var11 / 80.0);
      this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, var2, var3, var4, var5, var6, var7, var11, var13, var11);
      this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, var2, var3, var4, var5, var6, var7, var11, var13, var11);
      int var15 = 0;
      double[] var16 = new double[var6];

      for (int var17 = 0; var17 < var6; var17++) {
         var16[var17] = Math.cos(var17 * Math.PI * 6.0 / var6) * 2.0;
         double var18 = var17;
         if (var17 > var6 / 2) {
            var18 = var6 - 1 - var17;
         }

         if (var18 < 4.0) {
            var18 = 4.0 - var18;
            var16[var17] -= var18 * var18 * var18 * 10.0;
         }
      }

      for (int var34 = 0; var34 < var5; var34++) {
         for (int var36 = 0; var36 < var7; var36++) {
            double var19 = 0.0;

            for (int var21 = 0; var21 < var6; var21++) {
               double var22 = 0.0;
               double var24 = var16[var21];
               double var26 = this.noiseData2[var15] / 512.0;
               double var28 = this.noiseData3[var15] / 512.0;
               double var30 = (this.noiseData1[var15] / 10.0 + 1.0) / 2.0;
               if (var30 < 0.0) {
                  var22 = var26;
               } else if (var30 > 1.0) {
                  var22 = var28;
               } else {
                  var22 = var26 + (var28 - var26) * var30;
               }

               var22 -= var24;
               if (var21 > var6 - 4) {
                  double var32 = (var21 - (var6 - 4)) / 3.0F;
                  var22 = var22 * (1.0 - var32) + -10.0 * var32;
               }

               if (var21 < var19) {
                  double var39 = (var19 - var21) / 4.0;
                  var39 = MathHelper.clamp_double(var39, 0.0, 1.0);
                  var22 = var22 * (1.0 - var39) + -10.0 * var39;
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
      BlockPos var7 = new BlockPos(var2 * 16, 0, var3 * 16);
      ChunkCoordIntPair var8 = new ChunkCoordIntPair(var2, var3);
      this.genNetherBridge.generateStructure(this.worldObj, this.hellRNG, var8);

      for (int var9 = 0; var9 < 8; var9++) {
         this.field_177472_y
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int var10 = 0; var10 < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1; var10++) {
         this.field_177470_t
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int var11 = 0; var11 < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1); var11++) {
         this.field_177469_u
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int var12 = 0; var12 < 10; var12++) {
         this.field_177468_v
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.field_177471_z
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.field_177465_A
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      for (int var13 = 0; var13 < 16; var13++) {
         this.field_177467_w
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

      for (int var14 = 0; var14 < 16; var14++) {
         this.field_177473_x
            .generate(this.worldObj, this.hellRNG, var7.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

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
      return "HellRandomLevelSource";
   }

   @Override
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType var1, BlockPos var2) {
      if (var1 == EnumCreatureType.MONSTER) {
         if (this.genNetherBridge.func_175795_b(var2)) {
            return this.genNetherBridge.getSpawnList();
         }

         if (this.genNetherBridge.isPositionInStructure(this.worldObj, var2) && this.worldObj.getBlockState(var2.down()).getBlock() == Blocks.nether_brick) {
            return this.genNetherBridge.getSpawnList();
         }
      }

      BiomeGenBase var6 = this.worldObj.getBiomeGenForCoords(var2);
      return var6.getSpawnableList(var1);
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
      this.genNetherBridge.generate(this, this.worldObj, var2, var3, (ChunkPrimer)null);
   }

   @Override
   public Chunk provideChunk(BlockPos var1) {
      return this.provideChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }
}
