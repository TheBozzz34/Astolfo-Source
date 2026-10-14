package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {
   private static final WorldGenTaiga1 field_150639_aC = new WorldGenTaiga1();
   private static final WorldGenTaiga2 field_150640_aD = new WorldGenTaiga2(false);
   private static final WorldGenMegaPineTree field_150641_aE = new WorldGenMegaPineTree(false, false);
   private static final WorldGenMegaPineTree field_150642_aF = new WorldGenMegaPineTree(false, true);
   private static final WorldGenBlockBlob field_150643_aG = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);
   private int field_150644_aH;

   public BiomeGenTaiga(int id, int i) {
      super(id);
      this.field_150644_aH = i;
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 8, 4, 4));
      this.theBiomeDecorator.treesPerChunk = 10;
      if (i != 1 && i != 2) {
         this.theBiomeDecorator.grassPerChunk = 1;
         this.theBiomeDecorator.mushroomsPerChunk = 1;
      } else {
         this.theBiomeDecorator.grassPerChunk = 7;
         this.theBiomeDecorator.deadBushPerChunk = 1;
         this.theBiomeDecorator.mushroomsPerChunk = 3;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return (this.field_150644_aH == 1 || this.field_150644_aH == 2) && rand.nextInt(3) == 0
         ? (this.field_150644_aH != 2 && rand.nextInt(13) != 0 ? field_150641_aE : field_150642_aF)
         : (rand.nextInt(3) == 0 ? field_150639_aC : field_150640_aD);
   }

   @Override
   public WorldGenerator getRandomWorldGenForGrass(Random rand) {
      return rand.nextInt(5) > 0 ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
         int i = rand.nextInt(3);

         for (int j = 0; j < i; j++) {
            int k = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockPos = worldIn.getHeight(pos.add(k, 0, l));
            field_150643_aG.generate(worldIn, rand, blockPos);
         }
      }

      DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.FERN);

      for (int m = 0; m < 7; m++) {
         int n = rand.nextInt(16) + 8;
         int i2 = rand.nextInt(16) + 8;
         int i3 = rand.nextInt(worldIn.getHeight(pos.add(n, 0, i2)).getY() + 32);
         DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(n, i3, i2));
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
         this.topBlock = Blocks.grass.getDefaultState();
         this.fillerBlock = Blocks.dirt.getDefaultState();
         if (noiseVal > 1.75) {
            this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
         } else if (noiseVal > -0.95) {
            this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
         }
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   @Override
   public BiomeGenBase createMutatedBiome(int i) {
      return this.biomeID == BiomeGenBase.megaTaiga.biomeID
         ? new BiomeGenTaiga(i, 2)
            .func_150557_a(5858897, true)
            .setBiomeName("Mega Spruce Taiga")
            .setFillerBlockMetadata(5159473)
            .setTemperatureRainfall(0.25F, 0.8F)
            .setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight))
         : super.createMutatedBiome(i);
   }
}
