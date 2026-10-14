package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree {
   private static final IBlockState field_181648_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
   private static final IBlockState field_181649_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
      .withProperty(BlockOldLeaf.CHECK_DECAY, false);
   private static final long a = 85386633255729L;

   public WorldGenSwamp() {
      super(false);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(4) + 5;

      while (worldIn.getBlockState(position.down()).getBlock().getMaterial() == Material.water) {
         position = position.down();
      }

      boolean bl = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int j = position.getY(); j <= position.getY() + 1 + i; j++) {
            byte b = 1;
            if (j == position.getY()) {
               b = 0;
            }

            if (j >= position.getY() + 1 + i - 2) {
               b = 3;
            }

            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int k = position.getX() - b; k <= position.getX() + b && bl; k++) {
               for (int l = position.getZ() - b; l <= position.getZ() + b && bl; l++) {
                  if (j >= 0 && j < 256) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(k, j, l)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        if (block != Blocks.water && block != Blocks.flowing_water) {
                           bl = false;
                        } else if (j > position.getY()) {
                           bl = false;
                        }
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
         if ((block2 == Blocks.grass || block2 == Blocks.dirt) && position.getY() < 256 - i - 1) {
            this.func_175921_a(worldIn, position.down());

            for (int m = position.getY() - 3 + i; m <= position.getY() + i; m++) {
               int n = m - (position.getY() + i);
               int i2 = 2 - n / 2;

               for (int i3 = position.getX() - i2; i3 <= position.getX() + i2; i3++) {
                  int i4 = i3 - position.getX();

                  for (int i5 = position.getZ() - i2; i5 <= position.getZ() + i2; i5++) {
                     int i6 = i5 - position.getZ();
                     if (Math.abs(i4) != i2 || Math.abs(i6) != i2 || rand.nextInt(2) != 0 && n != 0) {
                        BlockPos blockPos = new BlockPos(i3, m, i5);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, field_181649_b);
                        }
                     }
                  }
               }
            }

            for (int i7 = 0; i7 < i; i7++) {
               Block block3 = worldIn.getBlockState(position.up(i7)).getBlock();
               if (block3.getMaterial() == Material.air || block3.getMaterial() == Material.leaves || block3 == Blocks.flowing_water || block3 == Blocks.water) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i7), field_181648_a);
               }
            }

            for (int i8 = position.getY() - 3 + i; i8 <= position.getY() + i; i8++) {
               int i9 = i8 - (position.getY() + i);
               int i10 = 2 - i9 / 2;
               BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();

               for (int i11 = position.getX() - i10; i11 <= position.getX() + i10; i11++) {
                  for (int i12 = position.getZ() - i10; i12 <= position.getZ() + i10; i12++) {
                     mutableBlockPos2.set(i11, i8, i12);
                     if (worldIn.getBlockState(mutableBlockPos2).getBlock().getMaterial() == Material.leaves) {
                        BlockPos blockPos2 = mutableBlockPos2.west();
                        BlockPos blockPos3 = mutableBlockPos2.east();
                        BlockPos blockPos4 = mutableBlockPos2.north();
                        BlockPos blockPos5 = mutableBlockPos2.south();
                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos2).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(worldIn, blockPos2, BlockVine.EAST);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos3).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(worldIn, blockPos3, BlockVine.WEST);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos4).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(worldIn, blockPos4, BlockVine.SOUTH);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos5).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(worldIn, blockPos5, BlockVine.NORTH);
                        }
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

   private void func_181647_a(World world, BlockPos blockPos, PropertyBool propertyBool) {
      IBlockState iBlockState = Blocks.vine.getDefaultState().withProperty(propertyBool, true);
      this.setBlockAndNotifyAdequately(world, blockPos, iBlockState);
      int i = 4;

      for (BlockPos blockPos2 = blockPos.down(); world.getBlockState(blockPos2).getBlock().getMaterial() == Material.air && i > 0; i--) {
         this.setBlockAndNotifyAdequately(world, blockPos2, iBlockState);
         blockPos2 = blockPos2.down();
      }
   }
}
