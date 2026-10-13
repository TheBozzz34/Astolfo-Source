package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase {
   private static final long bb = 96306984257120L;

   public BiomeGenDesert(int var1) {
      super(var1);
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
   public void decorate(World var1, Random var2, BlockPos var3) {
      super.decorate(var1, var2, var3);
      if (var2.nextInt(1000) == 0) {
         int var7 = var2.nextInt(16) + 8;
         int var8 = var2.nextInt(16) + 8;
         BlockPos var9 = var1.getHeight(var3.add(var7, 0, var8)).up();
         new WorldGenDesertWells().generate(var1, var2, var9);
      }
   }
}
