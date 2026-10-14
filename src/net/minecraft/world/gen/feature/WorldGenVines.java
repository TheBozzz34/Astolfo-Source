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
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (position.getY() < (int)b) {
         if (worldIn.isAirBlock(position)) {
            for (EnumFacing enumFacing : EnumFacing.Plane.HORIZONTAL.facings()) {
               if (Blocks.vine.canPlaceBlockOnSide(worldIn, position, enumFacing)) {
                  IBlockState iBlockState = Blocks.vine
                     .getDefaultState()
                     .withProperty(BlockVine.NORTH, enumFacing == EnumFacing.NORTH)
                     .withProperty(BlockVine.EAST, enumFacing == EnumFacing.EAST)
                     .withProperty(BlockVine.SOUTH, enumFacing == EnumFacing.SOUTH)
                     .withProperty(BlockVine.WEST, enumFacing == EnumFacing.WEST);
                  worldIn.setBlockState(position, iBlockState, 2);
                  break;
               }
            }
         } else {
            position = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
         }

         position = position.up();
      }

      return true;
   }
}
