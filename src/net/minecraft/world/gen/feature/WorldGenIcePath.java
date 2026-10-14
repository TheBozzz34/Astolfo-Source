package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenIcePath extends WorldGenerator {
   private Block block = Blocks.packed_ice;
   private int basePathWidth;

   public WorldGenIcePath(int i) {
      this.basePathWidth = i;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
         return false;
      }

      int i = rand.nextInt(this.basePathWidth - 2) + 2;
      byte b = 1;

      for (int j = position.getX() - i; j <= position.getX() + i; j++) {
         for (int k = position.getZ() - i; k <= position.getZ() + i; k++) {
            int l = j - position.getX();
            int m = k - position.getZ();
            if (l * l + m * m <= i * i) {
               for (int n = position.getY() - b; n <= position.getY() + b; n++) {
                  BlockPos blockPos = new BlockPos(j, n, k);
                  Block block2 = worldIn.getBlockState(blockPos).getBlock();
                  if (block2 == Blocks.dirt || block2 == Blocks.snow || block2 == Blocks.ice) {
                     worldIn.setBlockState(blockPos, this.block.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
