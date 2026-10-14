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

   public ChunkProviderHell(World worldIn, boolean bl, long seed) {
      this.worldObj = worldIn;
      this.field_177466_i = bl;
      this.hellRNG = new Random(seed);
      this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
      this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
      this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      worldIn.setSeaLevel(63);
   }

   public void func_180515_a(int i, int j, ChunkPrimer chunkPrimer) {
      byte b = 4;
      int k = this.worldObj.getSeaLevel() / 2 + 1;
      int l = b + 1;
      byte b2 = 17;
      int m = b + 1;
      this.noiseField = this.initializeNoiseField(this.noiseField, i * b, 0, j * b, l, b2, m);

      for (int n = 0; n < b; n++) {
         for (int i2 = 0; i2 < b; i2++) {
            for (int i3 = 0; i3 < 16; i3++) {
               double d = 0.125;
               double e = this.noiseField[((n + 0) * m + i2 + 0) * b2 + i3 + 0];
               double d2 = this.noiseField[((n + 0) * m + i2 + 1) * b2 + i3 + 0];
               double d3 = this.noiseField[((n + 1) * m + i2 + 0) * b2 + i3 + 0];
               double d4 = this.noiseField[((n + 1) * m + i2 + 1) * b2 + i3 + 0];
               double d5 = (this.noiseField[((n + 0) * m + i2 + 0) * b2 + i3 + 1] - e) * d;
               double d6 = (this.noiseField[((n + 0) * m + i2 + 1) * b2 + i3 + 1] - d2) * d;
               double d7 = (this.noiseField[((n + 1) * m + i2 + 0) * b2 + i3 + 1] - d3) * d;
               double d8 = (this.noiseField[((n + 1) * m + i2 + 1) * b2 + i3 + 1] - d4) * d;

               for (int i4 = 0; i4 < 8; i4++) {
                  double d9 = 0.25;
                  double d10 = e;
                  double d11 = d2;
                  double d12 = (d3 - e) * d9;
                  double d13 = (d4 - d2) * d9;

                  for (int i5 = 0; i5 < 4; i5++) {
                     double d14 = 0.25;
                     double d15 = d10;
                     double d16 = (d11 - d10) * d14;

                     for (int i6 = 0; i6 < 4; i6++) {
                        IBlockState iBlockState = null;
                        if (i3 * 8 + i4 < k) {
                           iBlockState = Blocks.lava.getDefaultState();
                        }

                        if (d15 > 0.0) {
                           iBlockState = Blocks.netherrack.getDefaultState();
                        }

                        int i7 = i5 + n * 4;
                        int i8 = i4 + i3 * 8;
                        int i9 = i6 + i2 * 4;
                        chunkPrimer.setBlockState(i7, i8, i9, iBlockState);
                        d15 += d16;
                     }

                     d10 += d12;
                     d11 += d13;
                  }

                  e += d5;
                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
               }
            }
         }
      }
   }

   public void func_180516_b(int i, int j, ChunkPrimer chunkPrimer) {
      long l = e ^ 70110039423045L;
      int k = this.worldObj.getSeaLevel() + 1;
      double d = 0.03125;
      this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, i * 16, j * 16, 0, 16, 16, 1, d, d, 1.0);
      this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, i * 16, 109, j * 16, 16, 1, 16, d, 1.0, d);
      this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen
         .generateNoiseOctaves(this.netherrackExclusivityNoise, i * 16, j * 16, 0, 16, 16, 1, d * 2.0, d * 2.0, d * 2.0);

      for (int m = 0; m < 16; m++) {
         for (int n = 0; n < 16; n++) {
            boolean bl = this.slowsandNoise[m + n * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            boolean bl2 = this.gravelNoise[m + n * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            int i2 = (int)(this.netherrackExclusivityNoise[m + n * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
            int i3 = -1;
            long l2 = 4084935087139344066L ^ l;
            IBlockState iBlockState = Blocks.netherrack.getDefaultState();
            IBlockState iBlockState2 = Blocks.netherrack.getDefaultState();

            for (int i4 = 127; i4 >= 0; i4--) {
               if (i4 < 127 - this.hellRNG.nextInt(5) && i4 > this.hellRNG.nextInt(5)) {
                  IBlockState iBlockState3 = chunkPrimer.getBlockState(n, i4, m);
                  if (iBlockState3.getBlock() == null || iBlockState3.getBlock().getMaterial() == Material.air) {
                     i3 = -1;
                  } else if (iBlockState3.getBlock() == Blocks.netherrack) {
                     if (i3 == -1) {
                        if (i2 <= 0) {
                           iBlockState = null;
                           iBlockState2 = Blocks.netherrack.getDefaultState();
                        } else if (i4 >= k - 4 && i4 <= k + 1) {
                           iBlockState = Blocks.netherrack.getDefaultState();
                           iBlockState2 = Blocks.netherrack.getDefaultState();
                           if (bl2) {
                              iBlockState = Blocks.gravel.getDefaultState();
                              iBlockState2 = Blocks.netherrack.getDefaultState();
                           }

                           if (bl) {
                              iBlockState = Blocks.soul_sand.getDefaultState();
                              iBlockState2 = Blocks.soul_sand.getDefaultState();
                           }
                        }

                        if (i4 < k && (iBlockState == null || iBlockState.getBlock().getMaterial() == Material.air)) {
                           iBlockState = Blocks.lava.getDefaultState();
                        }

                        i3 = i2;
                        if (i4 >= k - 1) {
                           chunkPrimer.setBlockState(n, i4, m, iBlockState);
                        } else {
                           chunkPrimer.setBlockState(n, i4, m, iBlockState2);
                        }
                     } else if (i3 > 0) {
                        i3--;
                        chunkPrimer.setBlockState(n, i4, m, iBlockState2);
                     }
                  }
               } else {
                  chunkPrimer.setBlockState(n, i4, m, Blocks.bedrock.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      this.hellRNG.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer chunkPrimer = new ChunkPrimer();
      this.func_180515_a(x, z, chunkPrimer);
      this.func_180516_b(x, z, chunkPrimer);
      this.netherCaveGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      if (this.field_177466_i) {
         this.genNetherBridge.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk = new Chunk(this.worldObj, chunkPrimer, x, z);
      BiomeGenBase[] biomeGenBases = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
      byte[] bytes = chunk.getBiomeArray();

      for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte)biomeGenBases[i].biomeID;
      }

      chunk.resetRelightChecks();
      return chunk;
   }

   private double[] initializeNoiseField(double[] doubles, int i, int j, int k, int l, int m, int n) {
      if (doubles == null) {
         doubles = new double[l * m * n];
      }

      double d = 684.412;
      double e = 2053.236;
      this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, i, j, k, l, 1, n, 1.0, 0.0, 1.0);
      this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, i, j, k, l, 1, n, 100.0, 0.0, 100.0);
      this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, i, j, k, l, m, n, d / 80.0, e / 60.0, d / 80.0);
      this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, i, j, k, l, m, n, d, e, d);
      this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, i, j, k, l, m, n, d, e, d);
      int i2 = 0;
      double[] doubles2 = new double[m];

      for (int i3 = 0; i3 < m; i3++) {
         doubles2[i3] = Math.cos(i3 * Math.PI * 6.0 / m) * 2.0;
         double d2 = i3;
         if (i3 > m / 2) {
            d2 = m - 1 - i3;
         }

         if (d2 < 4.0) {
            d2 = 4.0 - d2;
            doubles2[i3] -= d2 * d2 * d2 * 10.0;
         }
      }

      for (int i4 = 0; i4 < l; i4++) {
         for (int i5 = 0; i5 < n; i5++) {
            double d3 = 0.0;

            for (int i6 = 0; i6 < m; i6++) {
               double d4 = 0.0;
               double d5 = doubles2[i6];
               double d6 = this.noiseData2[i2] / 512.0;
               double d7 = this.noiseData3[i2] / 512.0;
               double d8 = (this.noiseData1[i2] / 10.0 + 1.0) / 2.0;
               if (d8 < 0.0) {
                  d4 = d6;
               } else if (d8 > 1.0) {
                  d4 = d7;
               } else {
                  d4 = d6 + (d7 - d6) * d8;
               }

               d4 -= d5;
               if (i6 > m - 4) {
                  double d9 = (i6 - (m - 4)) / 3.0F;
                  d4 = d4 * (1.0 - d9) + -10.0 * d9;
               }

               if (i6 < d3) {
                  double d10 = (d3 - i6) / 4.0;
                  d10 = MathHelper.clamp_double(d10, 0.0, 1.0);
                  d4 = d4 * (1.0 - d10) + -10.0 * d10;
               }

               doubles[i2] = d4;
               i2++;
            }
         }
      }

      return doubles;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      BlockFalling.fallInstantly = true;
      BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);
      this.genNetherBridge.generateStructure(this.worldObj, this.hellRNG, chunkCoordIntPair);

      for (int i = 0; i < 8; i++) {
         this.field_177472_y
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int j = 0; j < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1; j++) {
         this.field_177470_t
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int k = 0; k < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1); k++) {
         this.field_177469_u
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int l = 0; l < 10; l++) {
         this.field_177468_v
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.field_177471_z
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.field_177465_A
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      for (int m = 0; m < 16; m++) {
         this.field_177467_w
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

      for (int n = 0; n < 16; n++) {
         this.field_177473_x
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

      BlockFalling.fallInstantly = false;
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
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
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      if (creatureType == EnumCreatureType.MONSTER) {
         if (this.genNetherBridge.func_175795_b(pos)) {
            return this.genNetherBridge.getSpawnList();
         }

         if (this.genNetherBridge.isPositionInStructure(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.nether_brick) {
            return this.genNetherBridge.getSpawnList();
         }
      }

      BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(pos);
      return biomeGenBase.getSpawnableList(creatureType);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      this.genNetherBridge.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
