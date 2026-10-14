package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenCactus extends WorldGenerator {
   private static final long a = 30572182166201L;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < 10; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos)) {
            int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

            for (int k = 0; k < j; k++) {
               if (Blocks.cactus.canBlockStay(worldIn, blockPos)) {
                  worldIn.setBlockState(blockPos.up(k), Blocks.cactus.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
