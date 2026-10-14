package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderGenerate implements IChunkProvider {
   private Random rand;
   private NoiseGeneratorOctaves field_147431_j;
   private NoiseGeneratorOctaves field_147432_k;
   private NoiseGeneratorOctaves field_147429_l;
   private NoiseGeneratorPerlin field_147430_m;
   public NoiseGeneratorOctaves noiseGen5;
   public NoiseGeneratorOctaves noiseGen6;
   public NoiseGeneratorOctaves mobSpawnerNoise;
   private World worldObj;
   private final boolean mapFeaturesEnabled;
   private WorldType field_177475_o;
   private final double[] field_147434_q;
   private final float[] parabolicField;
   private ChunkProviderSettings settings;
   private Block oceanBlockTmpl = Blocks.water;
   private double[] stoneNoise = new double[256];
   private MapGenBase caveGenerator = new MapGenCaves();
   private MapGenStronghold strongholdGenerator = new MapGenStronghold();
   private MapGenVillage villageGenerator = new MapGenVillage();
   private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
   private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
   private MapGenBase ravineGenerator = new MapGenRavine();
   private StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();
   private BiomeGenBase[] biomesForGeneration;
   public double[] mainNoiseArray;
   public double[] lowerLimitNoiseArray;
   public double[] upperLimitNoiseArray;
   public double[] depthNoiseArray;
   private static final long a = 2014289007904L;

   public ChunkProviderGenerate(World worldIn, long seed, boolean generateStructures, String structuresJson) {
      this.worldObj = worldIn;
      this.mapFeaturesEnabled = generateStructures;
      this.field_177475_o = worldIn.getWorldInfo().getTerrainType();
      this.rand = new Random(seed);
      this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
      this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
      this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
      this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
      this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
      this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
      this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
      this.field_147434_q = new double[825];
      this.parabolicField = new float[25];

      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            float f = 10.0F / MathHelper.sqrt_float(i * i + j * j + 0.2F);
            this.parabolicField[i + 2 + (j + 2) * 5] = f;
         }
      }

      if (structuresJson != null) {
         this.settings = ChunkProviderSettings.Factory.jsonToFactory(structuresJson).func_177864_b();
         this.oceanBlockTmpl = this.settings.useLavaOceans ? Blocks.lava : Blocks.water;
         worldIn.setSeaLevel(this.settings.seaLevel);
      }
   }

   public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
      this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
      this.func_147423_a(x * 4, 0, z * 4);

      for (int i = 0; i < 4; i++) {
         int j = i * 5;
         int k = (i + 1) * 5;

         for (int l = 0; l < 4; l++) {
            int m = (j + l) * 33;
            int n = (j + l + 1) * 33;
            int i2 = (k + l) * 33;
            int i3 = (k + l + 1) * 33;

            for (int i4 = 0; i4 < 32; i4++) {
               double d = 0.125;
               double e = this.field_147434_q[m + i4];
               double d2 = this.field_147434_q[n + i4];
               double d3 = this.field_147434_q[i2 + i4];
               double d4 = this.field_147434_q[i3 + i4];
               double d5 = (this.field_147434_q[m + i4 + 1] - e) * d;
               double d6 = (this.field_147434_q[n + i4 + 1] - d2) * d;
               double d7 = (this.field_147434_q[i2 + i4 + 1] - d3) * d;
               double d8 = (this.field_147434_q[i3 + i4 + 1] - d4) * d;

               for (int i5 = 0; i5 < 8; i5++) {
                  double d9 = 0.25;
                  double d10 = e;
                  double d11 = d2;
                  double d12 = (d3 - e) * d9;
                  double d13 = (d4 - d2) * d9;

                  for (int i6 = 0; i6 < 4; i6++) {
                     double d14 = 0.25;
                     double d15 = (d11 - d10) * d14;
                     double d16 = d10 - d15;

                     for (int i7 = 0; i7 < 4; i7++) {
                        if ((d16 += d15) > 0.0) {
                           primer.setBlockState(i * 4 + i6, i4 * 8 + i5, l * 4 + i7, Blocks.stone.getDefaultState());
                        } else if (i4 * 8 + i5 < this.settings.seaLevel) {
                           primer.setBlockState(i * 4 + i6, i4 * 8 + i5, l * 4 + i7, this.oceanBlockTmpl.getDefaultState());
                        }
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

   public void replaceBlocksForBiome(int x, int z, ChunkPrimer primer, BiomeGenBase[] biomeGens) {
      double d = 0.03125;
      this.stoneNoise = this.field_147430_m.func_151599_a(this.stoneNoise, x * 16, z * 16, 16, 16, d * 2.0, d * 2.0, 1.0);

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            BiomeGenBase biomeGenBase = biomeGens[j + i * 16];
            biomeGenBase.genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + j, this.stoneNoise[j + i * 16]);
         }
      }
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      this.rand.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer chunkPrimer = new ChunkPrimer();
      this.setBlocksInChunk(x, z, chunkPrimer);
      this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
      this.replaceBlocksForBiome(x, z, chunkPrimer, this.biomesForGeneration);
      if (this.settings.useCaves) {
         this.caveGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useRavines) {
         this.ravineGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
         this.mineshaftGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useVillages && this.mapFeaturesEnabled) {
         this.villageGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
         this.strongholdGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useTemples && this.mapFeaturesEnabled) {
         this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useMonuments && this.mapFeaturesEnabled) {
         this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk = new Chunk(this.worldObj, chunkPrimer, x, z);
      byte[] bytes = chunk.getBiomeArray();

      for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte)this.biomesForGeneration[i].biomeID;
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   private void func_147423_a(int x, int y, int z) {
      this.depthNoiseArray = this.noiseGen6
         .generateNoiseOctaves(
            this.depthNoiseArray, x, z, 5, 5, this.settings.depthNoiseScaleX, this.settings.depthNoiseScaleZ, this.settings.depthNoiseScaleExponent
         );
      float f = this.settings.coordinateScale;
      float g = this.settings.heightScale;
      this.mainNoiseArray = this.field_147429_l
         .generateNoiseOctaves(
            this.mainNoiseArray,
            x,
            y,
            z,
            5,
            33,
            5,
            f / this.settings.mainNoiseScaleX,
            g / this.settings.mainNoiseScaleY,
            f / this.settings.mainNoiseScaleZ
         );
      this.lowerLimitNoiseArray = this.field_147431_j.generateNoiseOctaves(this.lowerLimitNoiseArray, x, y, z, 5, 33, 5, f, g, f);
      this.upperLimitNoiseArray = this.field_147432_k.generateNoiseOctaves(this.upperLimitNoiseArray, x, y, z, 5, 33, 5, f, g, f);
      boolean bl = false;
      boolean bl2 = false;
      int i = 0;
      int j = 0;

      for (int k = 0; k < 5; k++) {
         for (int l = 0; l < 5; l++) {
            float h = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            byte b = 2;
            BiomeGenBase biomeGenBase = this.biomesForGeneration[k + 2 + (l + 2) * 10];

            for (int m = -b; m <= b; m++) {
               for (int n = -b; n <= b; n++) {
                  BiomeGenBase biomeGenBase2 = this.biomesForGeneration[k + m + 2 + (l + n + 2) * 10];
                  float f4 = this.settings.biomeDepthOffSet + biomeGenBase2.minHeight * this.settings.biomeDepthWeight;
                  float f5 = this.settings.biomeScaleOffset + biomeGenBase2.maxHeight * this.settings.biomeScaleWeight;
                  if (this.field_177475_o == WorldType.AMPLIFIED && f4 > 0.0F) {
                     f4 = 1.0F + f4 * 2.0F;
                     f5 = 1.0F + f5 * 4.0F;
                  }

                  float f6 = this.parabolicField[m + 2 + (n + 2) * 5] / (f4 + 2.0F);
                  if (biomeGenBase2.minHeight > biomeGenBase.minHeight) {
                     f6 /= 2.0F;
                  }

                  h += f5 * f6;
                  f2 += f4 * f6;
                  f3 += f6;
               }
            }

            h /= f3;
            f2 /= f3;
            h = h * 0.9F + 0.1F;
            f2 = (f2 * 4.0F - 1.0F) / 8.0F;
            double d = this.depthNoiseArray[j] / 8000.0;
            if (d < 0.0) {
               d = -d * 0.3;
            }

            d = d * 3.0 - 2.0;
            if (d < 0.0) {
               d /= 2.0;
               if (d < -1.0) {
                  d = -1.0;
               }

               d /= 1.4;
               d /= 2.0;
            } else {
               if (d > 1.0) {
                  d = 1.0;
               }

               d /= 8.0;
            }

            j++;
            double e = f2;
            double d2 = h;
            e += d * 0.2;
            e = e * this.settings.baseSize / 8.0;
            double d3 = this.settings.baseSize + e * 4.0;

            for (int i2 = 0; i2 < 33; i2++) {
               double d4 = (i2 - d3) * this.settings.stretchY * 128.0 / 256.0 / d2;
               if (d4 < 0.0) {
                  d4 *= 4.0;
               }

               double d5 = this.lowerLimitNoiseArray[i] / this.settings.lowerLimitScale;
               double d6 = this.upperLimitNoiseArray[i] / this.settings.upperLimitScale;
               double d7 = (this.mainNoiseArray[i] / 10.0 + 1.0) / 2.0;
               double d8 = MathHelper.denormalizeClamp(d5, d6, d7) - d4;
               if (i2 > 29) {
                  double d9 = (i2 - 29) / 3.0F;
                  d8 = d8 * (1.0 - d9) + -10.0 * d9;
               }

               this.field_147434_q[i] = d8;
               i++;
            }
         }
      }
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      BlockFalling.fallInstantly = true;
      int i = x * 16;
      int j = z * 16;
      BlockPos blockPos = new BlockPos(i, 0, j);
      BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(blockPos.add(16, 0, 16));
      this.rand.setSeed(this.worldObj.getSeed());
      long l = this.rand.nextLong() / 2L * 2L + 1L;
      long m = this.rand.nextLong() / 2L * 2L + 1L;
      this.rand.setSeed(x * l + z * m ^ this.worldObj.getSeed());
      boolean bl = false;
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);
      if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
         this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useVillages && this.mapFeaturesEnabled) {
         bl = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
         this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useTemples && this.mapFeaturesEnabled) {
         this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useMonuments && this.mapFeaturesEnabled) {
         this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (biomeGenBase != BiomeGenBase.desert
         && biomeGenBase != BiomeGenBase.desertHills
         && this.settings.useWaterLakes
         && !bl
         && this.rand.nextInt(this.settings.waterLakeChance) == 0) {
         int k = this.rand.nextInt(16) + 8;
         int n = this.rand.nextInt(256);
         int i2 = this.rand.nextInt(16) + 8;
         new WorldGenLakes(Blocks.water).generate(this.worldObj, this.rand, blockPos.add(k, n, i2));
      }

      if (!bl && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes) {
         int i3 = this.rand.nextInt(16) + 8;
         int i4 = this.rand.nextInt(this.rand.nextInt(248) + 8);
         int i5 = this.rand.nextInt(16) + 8;
         if (i4 < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
            new WorldGenLakes(Blocks.lava).generate(this.worldObj, this.rand, blockPos.add(i3, i4, i5));
         }
      }

      if (this.settings.useDungeons) {
         for (int i6 = 0; i6 < this.settings.dungeonChance; i6++) {
            int i7 = this.rand.nextInt(16) + 8;
            int i8 = this.rand.nextInt(256);
            int i9 = this.rand.nextInt(16) + 8;
            new WorldGenDungeons().generate(this.worldObj, this.rand, blockPos.add(i7, i8, i9));
         }
      }

      biomeGenBase.decorate(this.worldObj, this.rand, new BlockPos(i, 0, j));
      SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomeGenBase, i + 8, j + 8, 16, 16, this.rand);
      blockPos = blockPos.add(8, 0, 8);

      for (int i10 = 0; i10 < 16; i10++) {
         for (int i11 = 0; i11 < 16; i11++) {
            BlockPos blockPos2 = this.worldObj.getPrecipitationHeight(blockPos.add(i10, 0, i11));
            BlockPos blockPos3 = blockPos2.down();
            if (this.worldObj.canBlockFreezeWater(blockPos3)) {
               this.worldObj.setBlockState(blockPos3, Blocks.ice.getDefaultState(), 2);
            }

            if (this.worldObj.canSnowAt(blockPos2, true)) {
               this.worldObj.setBlockState(blockPos2, Blocks.snow_layer.getDefaultState(), 2);
            }
         }
      }

      BlockFalling.fallInstantly = false;
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      boolean bl = false;
      if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L) {
         bl |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(x, z));
      }

      return bl;
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
      BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(pos);
      if (this.mapFeaturesEnabled) {
         if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.func_175798_a(pos)) {
            return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
         }

         if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos)) {
            return this.oceanMonumentGenerator.getScatteredFeatureSpawnList();
         }
      }

      return biomeGenBase.getSpawnableList(creatureType);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return "Stronghold".equals(structureName) && this.strongholdGenerator != null
         ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position)
         : null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
         this.mineshaftGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useVillages && this.mapFeaturesEnabled) {
         this.villageGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
         this.strongholdGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useTemples && this.mapFeaturesEnabled) {
         this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useMonuments && this.mapFeaturesEnabled) {
         this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
