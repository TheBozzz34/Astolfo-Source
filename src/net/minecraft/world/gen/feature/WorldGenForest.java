package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
   private static final IBlockState field_181629_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
   private static final IBlockState field_181630_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
      .withProperty(BlockOldLeaf.CHECK_DECAY, false);
   private boolean useExtraRandomHeight;
   private static final long a = 93949621887721L;

   public WorldGenForest(boolean bl, boolean bl2) {
      super(bl);
      this.useExtraRandomHeight = bl2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + 5;
      if (this.useExtraRandomHeight) {
         i += rand.nextInt(7);
      }

      boolean bl = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int j = position.getY(); j <= position.getY() + 1 + i; j++) {
            byte b = 1;
            if (j == position.getY()) {
               b = 0;
            }

            if (j >= position.getY() + 1 + i - 2) {
               b = 2;
            }

            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int k = position.getX() - b; k <= position.getX() + b && bl; k++) {
               for (int l = position.getZ() - b; l <= position.getZ() + b && bl; l++) {
                  if (j < 0 || j >= 256) {
                     bl = false;
                  } else if (!this.func_150523_a(worldIn.getBlockState(mutableBlockPos.set(k, j, l)).getBlock())) {
                     bl = false;
                  }
               }
            }
         }

         if (!bl) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if ((block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland) && position.getY() < 256 - i - 1) {
            this.func_175921_a(worldIn, position.down());

            for (int m = position.getY() - 3 + i; m <= position.getY() + i; m++) {
               int n = m - (position.getY() + i);
               int i2 = 1 - n / 2;

               for (int i3 = position.getX() - i2; i3 <= position.getX() + i2; i3++) {
                  int i4 = i3 - position.getX();

                  for (int i5 = position.getZ() - i2; i5 <= position.getZ() + i2; i5++) {
                     int i6 = i5 - position.getZ();
                     if (Math.abs(i4) != i2 || Math.abs(i6) != i2 || rand.nextInt(2) != 0 && n != 0) {
                        BlockPos blockPos = new BlockPos(i3, m, i5);
                        Block block2 = worldIn.getBlockState(blockPos).getBlock();
                        if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, field_181630_b);
                        }
                     }
                  }
               }
            }

            for (int i7 = 0; i7 < i; i7++) {
               Block block3 = worldIn.getBlockState(position.up(i7)).getBlock();
               if (block3.getMaterial() == Material.air || block3.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i7), field_181629_a);
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
