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

public class WorldGenCanopyTree extends WorldGenAbstractTree {
   private static final IBlockState field_181640_a = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
   private static final IBlockState field_181641_b = Blocks.leaves2
      .getDefaultState()
      .withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenCanopyTree(boolean bl) {
      super(bl);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + rand.nextInt(2) + 6;
      int j = position.getX();
      int k = position.getY();
      int l = position.getZ();
      if (k >= 1 && k + i + 1 < 256) {
         BlockPos blockPos = position.down();
         Block block = worldIn.getBlockState(blockPos).getBlock();
         if (block != Blocks.grass && block != Blocks.dirt) {
            return false;
         }

         if (!this.func_181638_a(worldIn, position, i)) {
            return false;
         }

         this.func_175921_a(worldIn, blockPos);
         this.func_175921_a(worldIn, blockPos.east());
         this.func_175921_a(worldIn, blockPos.south());
         this.func_175921_a(worldIn, blockPos.south().east());
         EnumFacing enumFacing = EnumFacing.Plane.HORIZONTAL.random(rand);
         int m = i - rand.nextInt(4);
         int n = 2 - rand.nextInt(3);
         int i2 = j;
         int i3 = l;
         int i4 = k + i - 1;

         for (int i5 = 0; i5 < i; i5++) {
            if (i5 >= m && n > 0) {
               i2 += enumFacing.getFrontOffsetX();
               i3 += enumFacing.getFrontOffsetZ();
               n--;
            }

            int i6 = k + i5;
            BlockPos blockPos2 = new BlockPos(i2, i6, i3);
            Material material = worldIn.getBlockState(blockPos2).getBlock().getMaterial();
            if (material == Material.air || material == Material.leaves) {
               this.func_181639_b(worldIn, blockPos2);
               this.func_181639_b(worldIn, blockPos2.east());
               this.func_181639_b(worldIn, blockPos2.south());
               this.func_181639_b(worldIn, blockPos2.east().south());
            }
         }

         for (int i7 = -2; i7 <= 0; i7++) {
            for (int i8 = -2; i8 <= 0; i8++) {
               byte b = -1;
               this.func_150526_a(worldIn, i2 + i7, i4 + b, i3 + i8);
               this.func_150526_a(worldIn, 1 + i2 - i7, i4 + b, i3 + i8);
               this.func_150526_a(worldIn, i2 + i7, i4 + b, 1 + i3 - i8);
               this.func_150526_a(worldIn, 1 + i2 - i7, i4 + b, 1 + i3 - i8);
               if ((i7 > -2 || i8 > -1) && (i7 != -1 || i8 != -2)) {
                  b = 1;
                  this.func_150526_a(worldIn, i2 + i7, i4 + b, i3 + i8);
                  this.func_150526_a(worldIn, 1 + i2 - i7, i4 + b, i3 + i8);
                  this.func_150526_a(worldIn, i2 + i7, i4 + b, 1 + i3 - i8);
                  this.func_150526_a(worldIn, 1 + i2 - i7, i4 + b, 1 + i3 - i8);
               }
            }
         }

         if (rand.nextBoolean()) {
            this.func_150526_a(worldIn, i2, i4 + 2, i3);
            this.func_150526_a(worldIn, i2 + 1, i4 + 2, i3);
            this.func_150526_a(worldIn, i2 + 1, i4 + 2, i3 + 1);
            this.func_150526_a(worldIn, i2, i4 + 2, i3 + 1);
         }

         for (int i9 = -3; i9 <= 4; i9++) {
            for (int i10 = -3; i10 <= 4; i10++) {
               if ((i9 != -3 || i10 != -3)
                  && (i9 != -3 || i10 != 4)
                  && (i9 != 4 || i10 != -3)
                  && (i9 != 4 || i10 != 4)
                  && (Math.abs(i9) < 3 || Math.abs(i10) < 3)) {
                  this.func_150526_a(worldIn, i2 + i9, i4, i3 + i10);
               }
            }
         }

         for (int i11 = -1; i11 <= 2; i11++) {
            for (int i12 = -1; i12 <= 2; i12++) {
               if ((i11 < 0 || i11 > 1 || i12 < 0 || i12 > 1) && rand.nextInt(3) <= 0) {
                  int i13 = rand.nextInt(3) + 2;

                  for (int i14 = 0; i14 < i13; i14++) {
                     this.func_181639_b(worldIn, new BlockPos(j + i11, i4 - i14 - 1, l + i12));
                  }

                  for (int i15 = -1; i15 <= 1; i15++) {
                     for (int i16 = -1; i16 <= 1; i16++) {
                        this.func_150526_a(worldIn, i2 + i11 + i15, i4, i3 + i12 + i16);
                     }
                  }

                  for (int i17 = -2; i17 <= 2; i17++) {
                     for (int i18 = -2; i18 <= 2; i18++) {
                        if (Math.abs(i17) != 2 || Math.abs(i18) != 2) {
                           this.func_150526_a(worldIn, i2 + i11 + i17, i4 - 1, i3 + i12 + i18);
                        }
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean func_181638_a(World world, BlockPos blockPos, int i) {
      int j = blockPos.getX();
      int k = blockPos.getY();
      int l = blockPos.getZ();
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int m = 0; m <= i + 1; m++) {
         byte b = 1;
         if (m == 0) {
            b = 0;
         }

         if (m >= i - 1) {
            b = 2;
         }

         for (int n = -b; n <= b; n++) {
            for (int i2 = -b; i2 <= b; i2++) {
               if (!this.func_150523_a(world.getBlockState(mutableBlockPos.set(j + n, k + m, l + i2)).getBlock())) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void func_181639_b(World world, BlockPos blockPos) {
      if (this.func_150523_a(world.getBlockState(blockPos).getBlock())) {
         this.setBlockAndNotifyAdequately(world, blockPos, field_181640_a);
      }
   }

   private void func_150526_a(World worldIn, int i, int j, int k) {
      BlockPos blockPos = new BlockPos(i, j, k);
      Block block = worldIn.getBlockState(blockPos).getBlock();
      if (block.getMaterial() == Material.air) {
         this.setBlockAndNotifyAdequately(worldIn, blockPos, field_181641_b);
      }
   }
}
