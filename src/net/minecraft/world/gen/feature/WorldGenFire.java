package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenFire extends WorldGenerator {
   private static final long a = 17783718171040L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      for (int var7 = 0; var7 < 64; var7++) {
         BlockPos var8 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isAirBlock(var8) && var1.getBlockState(var8.down()).getBlock() == Blocks.netherrack) {
            var1.setBlockState(var8, Blocks.fire.getDefaultState(), 2);
         }
      }

      return true;
   }
}
