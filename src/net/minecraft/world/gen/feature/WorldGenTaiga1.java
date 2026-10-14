package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenAbstractTree {
   private static final IBlockState field_181636_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
   private static final IBlockState field_181637_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenTaiga1() {
      super(false);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(5) + 7;
      int j = i - rand.nextInt(2) - 3;
      int k = i - j;
      int l = 1 + rand.nextInt(k + 1);
      boolean bl = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int m = position.getY(); m <= position.getY() + 1 + i && bl; m++) {
            int n = 1;
            if (m - position.getY() < j) {
               n = 0;
            } else {
               n = l;
            }

            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int i2 = position.getX() - n; i2 <= position.getX() + n && bl; i2++) {
               for (int i3 = position.getZ() - n; i3 <= position.getZ() + n && bl; i3++) {
                  if (m < 0 || m >= 256) {
                     bl = false;
                  } else if (!this.func_150523_a(worldIn.getBlockState(mutableBlockPos.set(i2, m, i3)).getBlock())) {
                     bl = false;
                  }
               }
            }
         }

         if (!bl) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() < 256 - i - 1) {
            this.func_175921_a(worldIn, position.down());
            int i4 = 0;

            for (int i5 = position.getY() + i; i5 >= position.getY() + j; i5--) {
               for (int i6 = position.getX() - i4; i6 <= position.getX() + i4; i6++) {
                  int i7 = i6 - position.getX();

                  for (int i8 = position.getZ() - i4; i8 <= position.getZ() + i4; i8++) {
                     int i9 = i8 - position.getZ();
                     if (Math.abs(i7) != i4 || Math.abs(i9) != i4 || i4 <= 0) {
                        BlockPos blockPos = new BlockPos(i6, i5, i8);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, field_181637_b);
                        }
                     }
                  }
               }

               if (i4 >= 1 && i5 == position.getY() + j + 1) {
                  i4--;
               } else if (i4 < l) {
                  i4++;
               }
            }

            for (int i10 = 0; i10 < i - 1; i10++) {
               Block block2 = worldIn.getBlockState(position.up(i10)).getBlock();
               if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i10), field_181636_a);
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
