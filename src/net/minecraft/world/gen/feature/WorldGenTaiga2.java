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

public class WorldGenTaiga2 extends WorldGenAbstractTree {
   private static final IBlockState field_181645_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
   private static final IBlockState field_181646_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenTaiga2(boolean bl) {
      super(bl);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(4) + 6;
      int j = 1 + rand.nextInt(2);
      int k = i - j;
      int l = 2 + rand.nextInt(2);
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
                  if (m >= 0 && m < 256) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(i2, m, i3)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        bl = false;
                     }
                  } else {
                     bl = false;
                  }
               }
            }
         }

         if (!bl) {
            return false;
         }

         Block block2 = worldIn.getBlockState(position.down()).getBlock();
         if ((block2 == Blocks.grass || block2 == Blocks.dirt || block2 == Blocks.farmland) && position.getY() < 256 - i - 1) {
            this.func_175921_a(worldIn, position.down());
            int i4 = rand.nextInt(2);
            int i5 = 1;
            byte b = 0;

            for (int i6 = 0; i6 <= k; i6++) {
               int i7 = position.getY() + i - i6;

               for (int i8 = position.getX() - i4; i8 <= position.getX() + i4; i8++) {
                  int i9 = i8 - position.getX();

                  for (int i10 = position.getZ() - i4; i10 <= position.getZ() + i4; i10++) {
                     int i11 = i10 - position.getZ();
                     if (Math.abs(i9) != i4 || Math.abs(i11) != i4 || i4 <= 0) {
                        BlockPos blockPos = new BlockPos(i8, i7, i10);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, field_181646_b);
                        }
                     }
                  }
               }

               if (i4 >= i5) {
                  i4 = b;
                  b = 1;
                  if (++i5 > l) {
                     i5 = l;
                  }
               } else {
                  i4++;
               }
            }

            int i12 = rand.nextInt(3);

            for (int i13 = 0; i13 < i - i12; i13++) {
               Block block3 = worldIn.getBlockState(position.up(i13)).getBlock();
               if (block3.getMaterial() == Material.air || block3.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i13), field_181645_a);
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
