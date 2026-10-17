package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {
   public final int baseHeight;
   public final IBlockState woodMetadata;
   public final IBlockState leavesMetadata;
   public int extraRandomHeight;

   public WorldGenHugeTrees(boolean bl, int i, int j, IBlockState iBlockState, IBlockState iBlockState2) {
      super(bl);
      this.baseHeight = i;
      this.extraRandomHeight = j;
      this.woodMetadata = iBlockState;
      this.leavesMetadata = iBlockState2;
   }

   public int func_150533_a(Random random2) {
      int i = random2.nextInt(3) + this.baseHeight;
      if (this.extraRandomHeight > 1) {
         i += random2.nextInt(this.extraRandomHeight);
      }

      return i;
   }

   private boolean func_175926_c(World worldIn, BlockPos blockPos, int i) {
      boolean bl = true;
      if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
         for (int j = 0; j <= 1 + i; j++) {
            byte b = 2;
            if (j == 0) {
               b = 1;
            } else if (j >= 1 + i - 2) {
               b = 2;
            }

            for (int k = -b; k <= b && bl; k++) {
               for (int l = -b; l <= b && bl; l++) {
                  if (blockPos.getY() + j < 0
                     || blockPos.getY() + j >= 256
                     || !this.func_150523_a(worldIn.getBlockState(blockPos.add(k, j, l)).getBlock())) {
                     bl = false;
                  }
               }
            }
         }

         return bl;
      } else {
         return false;
      }
   }

   private boolean func_175927_a(BlockPos blockPos, World worldIn) {
      BlockPos blockPos2 = blockPos.down();
      Block block = worldIn.getBlockState(blockPos2).getBlock();
      if ((block == Blocks.grass || block == Blocks.dirt) && blockPos.getY() >= 2) {
         this.func_175921_a(worldIn, blockPos2);
         this.func_175921_a(worldIn, blockPos2.east());
         this.func_175921_a(worldIn, blockPos2.south());
         this.func_175921_a(worldIn, blockPos2.south().east());
         return true;
      } else {
         return false;
      }
   }

   public boolean func_175929_a(World worldIn, Random random2, BlockPos blockPos, int i) {
      return this.func_175926_c(worldIn, blockPos, i) && this.func_175927_a(blockPos, worldIn);
   }

   public void func_175925_a(World worldIn, BlockPos blockPos, int i) {
      int j = i * i;

      for (int k = -i; k <= i + 1; k++) {
         for (int l = -i; l <= i + 1; l++) {
            int m = k - 1;
            int n = l - 1;
            if (k * k + l * l <= j
               || m * m + n * n <= j
               || k * k + n * n <= j
               || m * m + l * l <= j) {
               BlockPos blockPos2 = blockPos.add(k, 0, l);
               Material material = worldIn.getBlockState(blockPos2).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, blockPos2, this.leavesMetadata);
               }
            }
         }
      }
   }

   public void func_175928_b(World worldIn, BlockPos blockPos, int i) {
      int j = i * i;

      for (int k = -i; k <= i; k++) {
         for (int l = -i; l <= i; l++) {
            if (k * k + l * l <= j) {
               BlockPos blockPos2 = blockPos.add(k, 0, l);
               Material material = worldIn.getBlockState(blockPos2).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, blockPos2, this.leavesMetadata);
               }
            }
         }
      }
   }
}
