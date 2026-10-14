package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree {
   private static final IBlockState field_181653_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
   private static final IBlockState field_181654_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private final int minTreeHeight;
   private final boolean vinesGrow;
   private final IBlockState metaWood;
   private final IBlockState metaLeaves;
   private static final long b = 82291100903143L;

   public WorldGenTrees(boolean bl) {
      this(bl, 4, field_181653_a, field_181654_b, false);
   }

   public WorldGenTrees(boolean bl, int i, IBlockState iBlockState, IBlockState iBlockState2, boolean bl2) {
      super(bl);
      this.minTreeHeight = i;
      this.metaWood = iBlockState;
      this.metaLeaves = iBlockState2;
      this.vinesGrow = bl2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + this.minTreeHeight;
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
            byte b2 = 3;
            byte b3 = 0;

            for (int m = position.getY() - b2 + i; m <= position.getY() + i; m++) {
               int n = m - (position.getY() + i);
               int i2 = b3 + 1 - n / 2;

               for (int i3 = position.getX() - i2; i3 <= position.getX() + i2; i3++) {
                  int i4 = i3 - position.getX();

                  for (int i5 = position.getZ() - i2; i5 <= position.getZ() + i2; i5++) {
                     int i6 = i5 - position.getZ();
                     if (Math.abs(i4) != i2 || Math.abs(i6) != i2 || rand.nextInt(2) != 0 && n != 0) {
                        BlockPos blockPos = new BlockPos(i3, m, i5);
                        Block block2 = worldIn.getBlockState(blockPos).getBlock();
                        if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves || block2.getMaterial() == Material.vine) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, this.metaLeaves);
                        }
                     }
                  }
               }
            }

            for (int i7 = 0; i7 < i; i7++) {
               Block block3 = worldIn.getBlockState(position.up(i7)).getBlock();
               if (block3.getMaterial() == Material.air || block3.getMaterial() == Material.leaves || block3.getMaterial() == Material.vine) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i7), this.metaWood);
                  if (this.vinesGrow && i7 > 0) {
                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, i7, 0))) {
                        this.func_181651_a(worldIn, position.add(-1, i7, 0), BlockVine.EAST);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, i7, 0))) {
                        this.func_181651_a(worldIn, position.add(1, i7, 0), BlockVine.WEST);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, i7, -1))) {
                        this.func_181651_a(worldIn, position.add(0, i7, -1), BlockVine.SOUTH);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, i7, 1))) {
                        this.func_181651_a(worldIn, position.add(0, i7, 1), BlockVine.NORTH);
                     }
                  }
               }
            }

            if (this.vinesGrow) {
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
                              this.func_181650_b(worldIn, blockPos2, BlockVine.EAST);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos3).getBlock().getMaterial() == Material.air) {
                              this.func_181650_b(worldIn, blockPos3, BlockVine.WEST);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos4).getBlock().getMaterial() == Material.air) {
                              this.func_181650_b(worldIn, blockPos4, BlockVine.SOUTH);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos5).getBlock().getMaterial() == Material.air) {
                              this.func_181650_b(worldIn, blockPos5, BlockVine.NORTH);
                           }
                        }
                     }
                  }
               }

               if (rand.nextInt(5) == 0 && i > 5) {
                  for (int i13 = 0; i13 < 2; i13++) {
                     for (Object object : EnumFacing.Plane.HORIZONTAL) {
                        if (rand.nextInt(4 - i13) == 0) {
                           EnumFacing enumFacing = ((EnumFacing)object).getOpposite();
                           this.func_181652_a(
                              worldIn, rand.nextInt(3), position.add(enumFacing.getFrontOffsetX(), i - 5 + i13, enumFacing.getFrontOffsetZ()), (EnumFacing)object
                           );
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

   private void func_181652_a(World world, int i, BlockPos blockPos, EnumFacing enumFacing) {
      this.setBlockAndNotifyAdequately(
         world, blockPos, Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.AGE, i).withProperty(BlockCocoa.FACING, enumFacing)
      );
   }

   private void func_181651_a(World world, BlockPos blockPos, PropertyBool propertyBool) {
      this.setBlockAndNotifyAdequately(world, blockPos, Blocks.vine.getDefaultState().withProperty(propertyBool, true));
   }

   private void func_181650_b(World world, BlockPos blockPos, PropertyBool propertyBool) {
      this.func_181651_a(world, blockPos, propertyBool);
      int i = 4;

      for (BlockPos blockPos2 = blockPos.down(); world.getBlockState(blockPos2).getBlock().getMaterial() == Material.air && i > 0; i--) {
         this.func_181651_a(world, blockPos2, propertyBool);
         blockPos2 = blockPos2.down();
      }
   }
}
