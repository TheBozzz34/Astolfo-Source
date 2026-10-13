package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDeadBush extends WorldGenerator {
   private static final long a = 37256308154712L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      Block var7;
      while (((var7 = var1.getBlockState(var3).getBlock()).getMaterial() == Material.air || var7.getMaterial() == Material.leaves) && var3.getY() > 0) {
         var3 = var3.down();
      }

      for (int var8 = 0; var8 < 4; var8++) {
         BlockPos var9 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isAirBlock(var9) && Blocks.deadbush.canBlockStay(var1, var9, Blocks.deadbush.getDefaultState())) {
            var1.setBlockState(var9, Blocks.deadbush.getDefaultState(), 2);
         }
      }

      return true;
   }
}
