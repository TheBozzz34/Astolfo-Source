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

   public BiomeGenMesa(int id, boolean bl, boolean bl2) {
      super(id);
      this.field_150626_aH = bl;
      this.field_150620_aI = bl2;
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
      if (bl2) {
         this.theBiomeDecorator.treesPerChunk = 5;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.worldGeneratorTrees;
   }

   @Override
   public int getFoliageColorAtPos(BlockPos pos) {
      return 10387789;
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      return 9470285;
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (this.field_150621_aC == null || this.field_150622_aD != worldIn.getSeed()) {
         this.func_150619_a(worldIn.getSeed());
      }

      if (this.field_150623_aE == null || this.field_150624_aF == null || this.field_150622_aD != worldIn.getSeed()) {
         Random random = new Random(this.field_150622_aD);
         this.field_150623_aE = new NoiseGeneratorPerlin(random, 4);
         this.field_150624_aF = new NoiseGeneratorPerlin(random, 1);
      }

      this.field_150622_aD = worldIn.getSeed();
      double d = 0.0;
      if (this.field_150626_aH) {
         int i = (x & -16) + (z & 15);
         int j = (z & -16) + (x & 15);
         double e = Math.min(Math.abs(noiseVal), this.field_150623_aE.func_151601_a(i * 0.25, j * 0.25));
         if (e > 0.0) {
            double d2 = 0.001953125;
            double d3 = Math.abs(this.field_150624_aF.func_151601_a(i * d2, j * d2));
            d = e * e * 2.5;
            double d4 = Math.ceil(d3 * 50.0) + 14.0;
            if (d > d4) {
               d = d4;
            }

            d += 64.0;
         }
      }

      int k = x & 15;
      int l = z & 15;
      int m = worldIn.getSeaLevel();
      IBlockState iBlockState = Blocks.stained_hardened_clay.getDefaultState();
      IBlockState iBlockState2 = this.fillerBlock;
      int n = (int)(noiseVal / 3.0 + 3.0 + rand.nextDouble() * 0.25);
      boolean bl = Math.cos(noiseVal / 3.0 * Math.PI) > 0.0;
      int i2 = -1;
      boolean bl2 = false;

      for (int i3 = 255; i3 >= 0; i3--) {
         if (chunkPrimerIn.getBlockState(l, i3, k).getBlock().getMaterial() == Material.air && i3 < (int)d) {
            chunkPrimerIn.setBlockState(l, i3, k, Blocks.stone.getDefaultState());
         }

         if (i3 <= rand.nextInt(5)) {
            chunkPrimerIn.setBlockState(l, i3, k, Blocks.bedrock.getDefaultState());
         } else {
            IBlockState iBlockState3 = chunkPrimerIn.getBlockState(l, i3, k);
            if (iBlockState3.getBlock().getMaterial() == Material.air) {
               i2 = -1;
            } else if (iBlockState3.getBlock() == Blocks.stone) {
               if (i2 == -1) {
                  bl2 = false;
                  if (n <= 0) {
                     iBlockState = null;
                     iBlockState2 = Blocks.stone.getDefaultState();
                  } else if (i3 >= m - 4 && i3 <= m + 1) {
                     iBlockState = Blocks.stained_hardened_clay.getDefaultState();
                     iBlockState2 = this.fillerBlock;
                  }

                  if (i3 < m && (iBlockState == null || iBlockState.getBlock().getMaterial() == Material.air)) {
                     iBlockState = Blocks.water.getDefaultState();
                  }

                  i2 = n + Math.max(0, i3 - m);
                  if (i3 < m - 1) {
                     chunkPrimerIn.setBlockState(l, i3, k, iBlockState2);
                     if (iBlockState2.getBlock() == Blocks.stained_hardened_clay) {
                        chunkPrimerIn.setBlockState(
                           l, i3, k, iBlockState2.getBlock().getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE)
                        );
                     }
                  } else if (!this.field_150620_aI || i3 <= 86 + n * 2) {
                     if (i3 <= m + 3 + n) {
                        chunkPrimerIn.setBlockState(l, i3, k, this.topBlock);
                        bl2 = true;
                     } else {
                        IBlockState iBlockState4;
                        if (i3 < 64 || i3 > 127) {
                           iBlockState4 = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
                        } else if (bl) {
                           iBlockState4 = Blocks.hardened_clay.getDefaultState();
                        } else {
                           iBlockState4 = this.func_180629_a(x, i3, z);
                        }

                        chunkPrimerIn.setBlockState(l, i3, k, iBlockState4);
                     }
                  } else if (bl) {
                     chunkPrimerIn.setBlockState(
                        l, i3, k, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT)
                     );
                  } else {
                     chunkPrimerIn.setBlockState(l, i3, k, Blocks.grass.getDefaultState());
                  }
               } else if (i2 > 0) {
                  i2--;
                  if (bl2) {
                     chunkPrimerIn.setBlockState(
                        l, i3, k, Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE)
                     );
                  } else {
                     IBlockState iBlockState5 = this.func_180629_a(x, i3, z);
                     chunkPrimerIn.setBlockState(l, i3, k, iBlockState5);
                  }
               }
            }
         }
      }
   }

   private void func_150619_a(long l) {
      this.field_150621_aC = new IBlockState[64];
      Arrays.fill(this.field_150621_aC, Blocks.hardened_clay.getDefaultState());
      Random random = new Random(l);
      this.field_150625_aG = new NoiseGeneratorPerlin(random, 1);

      for (int i = 0; i < 64; i++) {
         i += random.nextInt(5) + 1;
         if (i < 64) {
            this.field_150621_aC[i] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
         }
      }

      int j = random.nextInt(4) + 2;

      for (int k = 0; k < j; k++) {
         int m = random.nextInt(3) + 1;
         int n = random.nextInt(64);

         for (int i2 = 0; n + i2 < 64 && i2 < m; i2++) {
            this.field_150621_aC[n + i2] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
         }
      }

      int i3 = random.nextInt(4) + 2;

      for (int i4 = 0; i4 < i3; i4++) {
         int i5 = random.nextInt(3) + 2;
         int i6 = random.nextInt(64);

         for (int i7 = 0; i6 + i7 < 64 && i7 < i5; i7++) {
            this.field_150621_aC[i6 + i7] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
         }
      }

      int i8 = random.nextInt(4) + 2;

      for (int i9 = 0; i9 < i8; i9++) {
         int i10 = random.nextInt(3) + 1;
         int i11 = random.nextInt(64);

         for (int i12 = 0; i11 + i12 < 64 && i12 < i10; i12++) {
            this.field_150621_aC[i11 + i12] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
         }
      }

      int i13 = random.nextInt(3) + 3;
      int i14 = 0;

      for (int i15 = 0; i15 < i13; i15++) {
         byte b = 1;
         i14 += random.nextInt(16) + 4;

         for (int i16 = 0; i14 + i16 < 64 && i16 < b; i16++) {
            this.field_150621_aC[i14 + i16] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
            if (i14 + i16 > 1 && random.nextBoolean()) {
               this.field_150621_aC[i14 + i16 - 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }

            if (i14 + i16 < 63 && random.nextBoolean()) {
               this.field_150621_aC[i14 + i16 + 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }
         }
      }
   }

   private IBlockState func_180629_a(int i, int j, int k) {
      int l = (int)Math.round(this.field_150625_aG.func_151601_a(i * 1.0 / 512.0, i * 1.0 / 512.0) * 2.0);
      return this.field_150621_aC[(j + l + 64) % 64];
   }

   @Override
   public BiomeGenBase createMutatedBiome(int i) {
      boolean bl = this.biomeID == BiomeGenBase.mesa.biomeID;
      BiomeGenMesa biomeGenMesa = new BiomeGenMesa(i, bl, this.field_150620_aI);
      if (!bl) {
         biomeGenMesa.setHeight(height_LowHills);
         biomeGenMesa.setBiomeName(this.biomeName + " M");
      } else {
         biomeGenMesa.setBiomeName(this.biomeName + " (Bryce)");
      }

      biomeGenMesa.func_150557_a(this.color, true);
      return biomeGenMesa;
   }
}
