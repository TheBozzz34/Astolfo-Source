package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree {
   private static final IBlockState field_181643_a = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA);
   private static final IBlockState field_181644_b = Blocks.leaves2
      .getDefaultState()
      .withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private static final long a = 68662523949693L;

   public WorldGenSavannaTree(boolean bl) {
      super(bl);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + rand.nextInt(3) + 5;
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
         if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() < 256 - i - 1) {
            this.func_175921_a(worldIn, position.down());
            EnumFacing enumFacing = EnumFacing.Plane.HORIZONTAL.random(rand);
            int m = i - rand.nextInt(4) - 1;
            int n = 3 - rand.nextInt(3);
            int i2 = position.getX();
            int i3 = position.getZ();
            int i4 = 0;

            for (int i5 = 0; i5 < i; i5++) {
               int i6 = position.getY() + i5;
               if (i5 >= m && n > 0) {
                  i2 += enumFacing.getFrontOffsetX();
                  i3 += enumFacing.getFrontOffsetZ();
                  n--;
               }

               BlockPos blockPos = new BlockPos(i2, i6, i3);
               Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.func_181642_b(worldIn, blockPos);
                  i4 = i6;
               }
            }

            BlockPos blockPos2 = new BlockPos(i2, i4, i3);

            for (int i7 = -3; i7 <= 3; i7++) {
               for (int i8 = -3; i8 <= 3; i8++) {
                  if (Math.abs(i7) != 3 || Math.abs(i8) != 3) {
                     this.func_175924_b(worldIn, blockPos2.add(i7, 0, i8));
                  }
               }
            }

            blockPos2 = blockPos2.up();

            for (int i9 = -1; i9 <= 1; i9++) {
               for (int i10 = -1; i10 <= 1; i10++) {
                  this.func_175924_b(worldIn, blockPos2.add(i9, 0, i10));
               }
            }

            this.func_175924_b(worldIn, blockPos2.east(2));
            this.func_175924_b(worldIn, blockPos2.west(2));
            this.func_175924_b(worldIn, blockPos2.south(2));
            this.func_175924_b(worldIn, blockPos2.north(2));
            i2 = position.getX();
            i3 = position.getZ();
            EnumFacing enumFacing2 = EnumFacing.Plane.HORIZONTAL.random(rand);
            if (enumFacing2 != enumFacing) {
               int i11 = m - rand.nextInt(2) - 1;
               int i12 = 1 + rand.nextInt(3);
               i4 = 0;

               for (int i13 = i11; i13 < i && i12 > 0; i12--) {
                  if (i13 >= 1) {
                     int i14 = position.getY() + i13;
                     i2 += enumFacing2.getFrontOffsetX();
                     i3 += enumFacing2.getFrontOffsetZ();
                     BlockPos blockPos3 = new BlockPos(i2, i14, i3);
                     Material material2 = worldIn.getBlockState(blockPos3).getBlock().getMaterial();
                     if (material2 == Material.air || material2 == Material.leaves) {
                        this.func_181642_b(worldIn, blockPos3);
                        i4 = i14;
                     }
                  }

                  i13++;
               }

               if (i4 > 0) {
                  BlockPos blockPos4 = new BlockPos(i2, i4, i3);

                  for (int i15 = -2; i15 <= 2; i15++) {
                     for (int i16 = -2; i16 <= 2; i16++) {
                        if (Math.abs(i15) != 2 || Math.abs(i16) != 2) {
                           this.func_175924_b(worldIn, blockPos4.add(i15, 0, i16));
                        }
                     }
                  }

                  blockPos4 = blockPos4.up();

                  for (int i17 = -1; i17 <= 1; i17++) {
                     for (int i18 = -1; i18 <= 1; i18++) {
                        this.func_175924_b(worldIn, blockPos4.add(i17, 0, i18));
                     }
                  }
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

   private void func_181642_b(World world, BlockPos blockPos) {
      this.setBlockAndNotifyAdequately(world, blockPos, field_181643_a);
   }

   private void func_175924_b(World worldIn, BlockPos blockPos) {
      Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
      if (material == Material.air || material == Material.leaves) {
         this.setBlockAndNotifyAdequately(worldIn, blockPos, field_181644_b);
      }
   }
}
