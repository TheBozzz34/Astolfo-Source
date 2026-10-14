package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
         return false;
      }

      position = position.up(rand.nextInt(4));
      int i = rand.nextInt(4) + 7;
      int j = i / 4 + rand.nextInt(2);
      if (j > 1 && rand.nextInt(60) == 0) {
         position = position.up(10 + rand.nextInt(30));
      }

      for (int k = 0; k < i; k++) {
         float f = (1.0F - (float)k / i) * j;
         int l = MathHelper.ceiling_float_int(f);

         for (int m = -l; m <= l; m++) {
            float g = MathHelper.abs_int(m) - 0.25F;

            for (int n = -l; n <= l; n++) {
               float h = MathHelper.abs_int(n) - 0.25F;
               if ((m == 0 && n == 0 || g * g + h * h <= f * f)
                  && (m != -l && m != l && n != -l && n != l || rand.nextFloat() <= 0.75F)) {
                  Block block = worldIn.getBlockState(position.add(m, k, n)).getBlock();
                  if (block.getMaterial() == Material.air || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                     this.setBlockAndNotifyAdequately(worldIn, position.add(m, k, n), Blocks.packed_ice.getDefaultState());
                  }

                  if (k != 0 && l > 1) {
                     block = worldIn.getBlockState(position.add(m, -k, n)).getBlock();
                     if (block.getMaterial() == Material.air || block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(m, -k, n), Blocks.packed_ice.getDefaultState());
                     }
                  }
               }
            }
         }
      }

      int i2 = j - 1;
      if (i2 < 0) {
         i2 = 0;
      } else if (i2 > 1) {
         i2 = 1;
      }

      for (int i3 = -i2; i3 <= i2; i3++) {
         for (int i4 = -i2; i4 <= i2; i4++) {
            BlockPos blockPos = position.add(i3, -1, i4);
            int i5 = 50;
            if (Math.abs(i3) == 1 && Math.abs(i4) == 1) {
               i5 = rand.nextInt(5);
            }

            while (blockPos.getY() > 50) {
               Block block2 = worldIn.getBlockState(blockPos).getBlock();
               if (block2.getMaterial() != Material.air && block2 != Blocks.dirt && block2 != Blocks.snow && block2 != Blocks.ice && block2 != Blocks.packed_ice) {
                  break;
               }

               this.setBlockAndNotifyAdequately(worldIn, blockPos, Blocks.packed_ice.getDefaultState());
               blockPos = blockPos.down();
               if (--i5 <= 0) {
                  blockPos = blockPos.down(rand.nextInt(5) + 1);
                  i5 = rand.nextInt(5);
               }
            }
         }
      }

      return true;
   }
}
