package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator {
   private static final long a = 18831106842090L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      for (int var7 = 0; var7 < 10; var7++) {
         int var8 = var3.getX() + var2.nextInt(8) - var2.nextInt(8);
         int var9 = var3.getY() + var2.nextInt(4) - var2.nextInt(4);
         int var10 = var3.getZ() + var2.nextInt(8) - var2.nextInt(8);
         if (var1.isAirBlock(new BlockPos(var8, var9, var10)) && Blocks.waterlily.canPlaceBlockAt(var1, new BlockPos(var8, var9, var10))) {
            var1.setBlockState(new BlockPos(var8, var9, var10), Blocks.waterlily.getDefaultState(), 2);
         }
      }

      return true;
   }
}
