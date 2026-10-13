package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator {
   private static final long a = 11406565195909L;
   private static final long b = -2057056964168384384L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      while (var3.getY() < (int)b) {
         if (var1.isAirBlock(var3)) {
            for (EnumFacing var10 : EnumFacing.Plane.HORIZONTAL.facings()) {
               if (Blocks.vine.canPlaceBlockOnSide(var1, var3, var10)) {
                  IBlockState var11 = Blocks.vine
                     .getDefaultState()
                     .withProperty(BlockVine.NORTH, var10 == EnumFacing.NORTH)
                     .withProperty(BlockVine.EAST, var10 == EnumFacing.EAST)
                     .withProperty(BlockVine.SOUTH, var10 == EnumFacing.SOUTH)
                     .withProperty(BlockVine.WEST, var10 == EnumFacing.WEST);
                  var1.setBlockState(var3, var11, 2);
                  break;
               }
            }
         } else {
            var3 = var3.add(var2.nextInt(4) - var2.nextInt(4), 0, var2.nextInt(4) - var2.nextInt(4));
         }

         var3 = var3.up();
      }

      return true;
   }
}
