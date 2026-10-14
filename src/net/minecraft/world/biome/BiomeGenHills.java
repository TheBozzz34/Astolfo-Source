package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
   private WorldGenerator theWorldGenerator = new WorldGenMinable(
      Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONE), 9
   );
   private WorldGenTaiga2 field_150634_aD = new WorldGenTaiga2(false);
   private int field_150635_aE = 0;
   private int field_150636_aF = 1;
   private int field_150637_aG = 2;
   private int field_150638_aH = this.field_150635_aE;

   public BiomeGenHills(int id, boolean bl) {
      super(id);
      if (bl) {
         this.theBiomeDecorator.treesPerChunk = 3;
         this.field_150638_aH = this.field_150636_aF;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return rand.nextInt(3) > 0 ? this.field_150634_aD : super.genBigTreeChance(rand);
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      int i = 3 + rand.nextInt(6);

      for (int j = 0; j < i; j++) {
         int k = rand.nextInt(16);
         int l = rand.nextInt(28) + 4;
         int m = rand.nextInt(16);
         BlockPos blockPos = pos.add(k, l, m);
         if (worldIn.getBlockState(blockPos).getBlock() == Blocks.stone) {
            worldIn.setBlockState(blockPos, Blocks.emerald_ore.getDefaultState(), 2);
         }
      }

      for (int n = 0; n < 7; n++) {
         int i2 = rand.nextInt(16);
         int i3 = rand.nextInt(64);
         int i4 = rand.nextInt(16);
         this.theWorldGenerator.generate(worldIn, rand, pos.add(i2, i3, i4));
      }
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      this.topBlock = Blocks.grass.getDefaultState();
      this.fillerBlock = Blocks.dirt.getDefaultState();
      if ((noiseVal < -1.0 || noiseVal > 2.0) && this.field_150638_aH == this.field_150637_aG) {
         this.topBlock = Blocks.gravel.getDefaultState();
         this.fillerBlock = Blocks.gravel.getDefaultState();
      } else if (noiseVal > 1.0 && this.field_150638_aH != this.field_150636_aF) {
         this.topBlock = Blocks.stone.getDefaultState();
         this.fillerBlock = Blocks.stone.getDefaultState();
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   private BiomeGenHills mutateHills(BiomeGenBase biomeGenBase) {
      this.field_150638_aH = this.field_150637_aG;
      this.func_150557_a(biomeGenBase.color, true);
      this.setBiomeName(biomeGenBase.biomeName + " M");
      this.setHeight(new BiomeGenBase.Height(biomeGenBase.minHeight, biomeGenBase.maxHeight));
      this.setTemperatureRainfall(biomeGenBase.temperature, biomeGenBase.rainfall);
      return this;
   }

   @Override
   public BiomeGenBase createMutatedBiome(int i) {
      return new BiomeGenHills(i, false).mutateHills(this);
   }
}
