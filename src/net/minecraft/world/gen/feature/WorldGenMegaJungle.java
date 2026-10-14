package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {
   private static final long a = 51016080922317L;

   public WorldGenMegaJungle(boolean bl, int i, int k, IBlockState iBlockState, IBlockState iBlockState2) {
      super(bl, i, k, iBlockState, iBlockState2);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = this.func_150533_a(rand);
      if (!this.func_175929_a(worldIn, rand, position, i)) {
         return false;
      }

      this.func_175930_c(worldIn, position.up(i), 2);

      for (int j = position.getY() + i - 2 - rand.nextInt(4); j > position.getY() + i / 2; j -= 2 + rand.nextInt(4)) {
         float f = rand.nextFloat() * (float) Math.PI * 2.0F;
         int k = position.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
         int l = position.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

         for (int m = 0; m < 5; m++) {
            k = position.getX() + (int)(1.5F + MathHelper.cos(f) * m);
            l = position.getZ() + (int)(1.5F + MathHelper.sin(f) * m);
            this.setBlockAndNotifyAdequately(worldIn, new BlockPos(k, j - 3 + m / 2, l), this.woodMetadata);
         }

         int n = 1 + rand.nextInt(2);
         int i2 = j;

         for (int i3 = j - n; i3 <= i2; i3++) {
            int i4 = i3 - i2;
            this.e(worldIn, new BlockPos(k, i3, l), 1 - i4);
         }
      }

      for (int i5 = 0; i5 < i; i5++) {
         BlockPos blockPos = position.up(i5);
         if (this.func_150523_a(worldIn.getBlockState(blockPos).getBlock())) {
            this.setBlockAndNotifyAdequately(worldIn, blockPos, this.woodMetadata);
            if (i5 > 0) {
               this.func_181632_a(worldIn, rand, blockPos.west(), BlockVine.EAST);
               this.func_181632_a(worldIn, rand, blockPos.north(), BlockVine.SOUTH);
            }
         }

         if (i5 < i - 1) {
            BlockPos blockPos2 = blockPos.east();
            if (this.func_150523_a(worldIn.getBlockState(blockPos2).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, blockPos2, this.woodMetadata);
               if (i5 > 0) {
                  this.func_181632_a(worldIn, rand, blockPos2.east(), BlockVine.WEST);
                  this.func_181632_a(worldIn, rand, blockPos2.north(), BlockVine.SOUTH);
               }
            }

            BlockPos blockPos3 = blockPos.south().east();
            if (this.func_150523_a(worldIn.getBlockState(blockPos3).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, blockPos3, this.woodMetadata);
               if (i5 > 0) {
                  this.func_181632_a(worldIn, rand, blockPos3.east(), BlockVine.WEST);
                  this.func_181632_a(worldIn, rand, blockPos3.south(), BlockVine.NORTH);
               }
            }

            BlockPos blockPos4 = blockPos.south();
            if (this.func_150523_a(worldIn.getBlockState(blockPos4).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, blockPos4, this.woodMetadata);
               if (i5 > 0) {
                  this.func_181632_a(worldIn, rand, blockPos4.west(), BlockVine.EAST);
                  this.func_181632_a(worldIn, rand, blockPos4.south(), BlockVine.NORTH);
               }
            }
         }
      }

      return true;
   }

   private void func_181632_a(World world, Random random2, BlockPos blockPos, PropertyBool propertyBool) {
      if (random2.nextInt(3) > 0 && world.isAirBlock(blockPos)) {
         this.setBlockAndNotifyAdequately(world, blockPos, Blocks.vine.getDefaultState().withProperty(propertyBool, true));
      }
   }

   private void func_175930_c(World worldIn, BlockPos blockPos, int i) {
      byte b = 2;

      for (int k = -b; k <= 0; k++) {
         this.j(worldIn, blockPos.up(k), i + 1 - k);
      }
   }
}
