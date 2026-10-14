package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees {
   private final IBlockState leavesMetadata;
   private final IBlockState woodMetadata;
   private static final long a = 39567591091198L;

   public WorldGenShrub(IBlockState iBlockState, IBlockState iBlockState2) {
      super(false);
      this.woodMetadata = iBlockState;
      this.leavesMetadata = iBlockState2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      Block block;
      while (
         ((block = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && position.getY() > 0
      ) {
         position = position.down();
      }

      Block block2 = worldIn.getBlockState(position).getBlock();
      if (block2 == Blocks.dirt || block2 == Blocks.grass) {
         position = position.up();
         this.setBlockAndNotifyAdequately(worldIn, position, this.woodMetadata);

         for (int i = position.getY(); i <= position.getY() + 2; i++) {
            int j = i - position.getY();
            int k = 2 - j;

            for (int l = position.getX() - k; l <= position.getX() + k; l++) {
               int m = l - position.getX();

               for (int n = position.getZ() - k; n <= position.getZ() + k; n++) {
                  int i2 = n - position.getZ();
                  if (Math.abs(m) != k || Math.abs(i2) != k || rand.nextInt(2) != 0) {
                     BlockPos blockPos = new BlockPos(l, i, n);
                     if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                        this.setBlockAndNotifyAdequately(worldIn, blockPos, this.leavesMetadata);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
