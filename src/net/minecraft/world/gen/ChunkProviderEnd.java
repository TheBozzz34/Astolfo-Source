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

   public ChunkProviderEnd(World worldIn, long seed) {
      this.endWorld = worldIn;
      this.endRNG = new Random(seed);
      this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
      this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
      this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
      this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
      this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);
   }

   public void func_180520_a(int i, int j, ChunkPrimer chunkPrimer) {
      byte b = 2;
      int k = b + 1;
      byte b2 = 33;
      int l = b + 1;
      this.densities = this.initializeNoiseField(this.densities, i * b, 0, j * b, k, b2, l);

      for (int m = 0; m < b; m++) {
         for (int n = 0; n < b; n++) {
            for (int i2 = 0; i2 < 32; i2++) {
               double d = 0.25;
               double e = this.densities[((m + 0) * l + n + 0) * b2 + i2 + 0];
               double d2 = this.densities[((m + 0) * l + n + 1) * b2 + i2 + 0];
               double d3 = this.densities[((m + 1) * l + n + 0) * b2 + i2 + 0];
               double d4 = this.densities[((m + 1) * l + n + 1) * b2 + i2 + 0];
               double d5 = (this.densities[((m + 0) * l + n + 0) * b2 + i2 + 1] - e) * d;
               double d6 = (this.densities[((m + 0) * l + n + 1) * b2 + i2 + 1] - d2) * d;
               double d7 = (this.densities[((m + 1) * l + n + 0) * b2 + i2 + 1] - d3) * d;
               double d8 = (this.densities[((m + 1) * l + n + 1) * b2 + i2 + 1] - d4) * d;

               for (int i3 = 0; i3 < 4; i3++) {
                  double d9 = 0.125;
                  double d10 = e;
                  double d11 = d2;
                  double d12 = (d3 - e) * d9;
                  double d13 = (d4 - d2) * d9;

                  for (int i4 = 0; i4 < 8; i4++) {
                     double d14 = 0.125;
                     double d15 = d10;
                     double d16 = (d11 - d10) * d14;

                     for (int i5 = 0; i5 < 8; i5++) {
                        IBlockState iBlockState = null;
                        if (d15 > 0.0) {
                           iBlockState = Blocks.end_stone.getDefaultState();
                        }

                        int i6 = i4 + m * 8;
                        int i7 = i3 + i2 * 4;
                        int i8 = i5 + n * 8;
                        chunkPrimer.setBlockState(i6, i7, i8, iBlockState);
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

   public void func_180519_a(ChunkPrimer chunkPrimer) {
      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            byte b = 1;
            int k = -1;
            IBlockState iBlockState = Blocks.end_stone.getDefaultState();
            IBlockState iBlockState2 = Blocks.end_stone.getDefaultState();

            for (int l = 127; l >= 0; l--) {
               IBlockState iBlockState3 = chunkPrimer.getBlockState(i, l, j);
               if (iBlockState3.getBlock().getMaterial() == Material.air) {
                  k = -1;
               } else if (iBlockState3.getBlock() == Blocks.stone) {
                  if (k == -1) {
                     if (b <= 0) {
                        iBlockState = Blocks.air.getDefaultState();
                        iBlockState2 = Blocks.end_stone.getDefaultState();
                     }

                     k = b;
                     if (l >= 0) {
                        chunkPrimer.setBlockState(i, l, j, iBlockState);
                     } else {
                        chunkPrimer.setBlockState(i, l, j, iBlockState2);
                     }
                  } else if (k > 0) {
                     k--;
                     chunkPrimer.setBlockState(i, l, j, iBlockState2);
                  }
               }
            }
         }
      }
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      this.endRNG.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer chunkPrimer = new ChunkPrimer();
      this.biomesForGeneration = this.endWorld.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
      this.func_180520_a(x, z, chunkPrimer);
      this.func_180519_a(chunkPrimer);
      Chunk chunk = new Chunk(this.endWorld, chunkPrimer, x, z);
      byte[] bytes = chunk.getBiomeArray();

      for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte)this.biomesForGeneration[i].biomeID;
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   private double[] initializeNoiseField(double[] doubles, int i, int j, int k, int l, int m, int n) {
      if (doubles == null) {
         doubles = new double[l * m * n];
      }

      double d = 684.412;
      double e = 684.412;
      this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, i, k, l, n, 1.121, 1.121, 0.5);
      this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, i, k, l, n, 200.0, 200.0, 0.5);
      d *= 2.0;
      this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, i, j, k, l, m, n, d / 80.0, e / 160.0, d / 80.0);
      this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, i, j, k, l, m, n, d, e, d);
      this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, i, j, k, l, m, n, d, e, d);
      int i2 = 0;

      for (int i3 = 0; i3 < l; i3++) {
         for (int i4 = 0; i4 < n; i4++) {
            float f = (i3 + i) / 1.0F;
            float g = (i4 + k) / 1.0F;
            float h = 100.0F - MathHelper.sqrt_float(f * f + g * g) * 8.0F;
            if (h > 80.0F) {
               h = 80.0F;
            }

            if (h < -100.0F) {
               h = -100.0F;
            }

            for (int i5 = 0; i5 < m; i5++) {
               double d2 = 0.0;
               double d3 = this.noiseData2[i2] / 512.0;
               double d4 = this.noiseData3[i2] / 512.0;
               double d5 = (this.noiseData1[i2] / 10.0 + 1.0) / 2.0;
               if (d5 < 0.0) {
                  d2 = d3;
               } else if (d5 > 1.0) {
                  d2 = d4;
               } else {
                  d2 = d3 + (d4 - d3) * d5;
               }

               d2 -= 8.0;
               d2 += h;
               byte b = 2;
               if (i5 > m / 2 - b) {
                  double d6 = (i5 - (m / 2 - b)) / 64.0F;
                  d6 = MathHelper.clamp_double(d6, 0.0, 1.0);
                  d2 = d2 * (1.0 - d6) + -3000.0 * d6;
               }

               b = 8;
               if (i5 < b) {
                  double d7 = (b - i5) / (b - 1.0F);
                  d2 = d2 * (1.0 - d7) + -30.0 * d7;
               }

               doubles[i2] = d2;
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
      this.endWorld.getBiomeGenForCoords(blockPos.add(16, 0, 16)).decorate(this.endWorld, this.endWorld.rand, blockPos);
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
      return "RandomLevelSource";
   }

   @Override
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.endWorld.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
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
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
