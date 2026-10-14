package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase {
   private static final long bb = 96306984257120L;

   public BiomeGenDesert(int id) {
      super(id);
      this.spawnableCreatureList.clear();
      this.topBlock = Blocks.sand.getDefaultState();
      this.fillerBlock = Blocks.sand.getDefaultState();
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.deadBushPerChunk = 2;
      this.theBiomeDecorator.reedsPerChunk = 50;
      this.theBiomeDecorator.cactiPerChunk = 10;
      this.spawnableCreatureList.clear();
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      if (rand.nextInt(1000) == 0) {
         int i = rand.nextInt(16) + 8;
         int j = rand.nextInt(16) + 8;
         BlockPos blockPos = worldIn.getHeight(pos.add(i, 0, j)).up();
         new WorldGenDesertWells().generate(worldIn, rand, blockPos);
      }
   }
}
