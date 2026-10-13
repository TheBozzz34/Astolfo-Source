package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenMelon extends WorldGenerator {
   private static final long a = 15799793815060L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      for (int var7 = 0; var7 < 64; var7++) {
         BlockPos var8 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (Blocks.melon_block.canPlaceBlockAt(var1, var8) && var1.getBlockState(var8.down()).getBlock() == Blocks.grass) {
            var1.setBlockState(var8, Blocks.melon_block.getDefaultState(), 2);
         }
      }

      return true;
   }
}
