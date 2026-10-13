package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {
   private static final long a = 104167267980173L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      while (var1.isAirBlock(var3) && var3.getY() > 2) {
         var3 = var3.down();
      }

      if (var1.getBlockState(var3).getBlock() != Blocks.snow) {
         return false;
      }

      var3 = var3.up(var2.nextInt(4));
      int var7 = var2.nextInt(4) + 7;
      int var8 = var7 / 4 + var2.nextInt(2);
      if (var8 > 1 && var2.nextInt(60) == 0) {
         var3 = var3.up(10 + var2.nextInt(30));
      }

      for (int var9 = 0; var9 < var7; var9++) {
         float var10 = (1.0F - (float)var9 / var7) * var8;
         int var11 = MathHelper.ceiling_float_int(var10);

         for (int var12 = -var11; var12 <= var11; var12++) {
            float var13 = MathHelper.abs_int(var12) - 0.25F;

            for (int var14 = -var11; var14 <= var11; var14++) {
               float var15 = MathHelper.abs_int(var14) - 0.25F;
               if ((var12 == 0 && var14 == 0 || var13 * var13 + var15 * var15 <= var10 * var10)
                  && (var12 != -var11 && var12 != var11 && var14 != -var11 && var14 != var11 || var2.nextFloat() <= 0.75F)) {
                  Block var16 = var1.getBlockState(var3.add(var12, var9, var14)).getBlock();
                  if (var16.getMaterial() == Material.air || var16 == Blocks.dirt || var16 == Blocks.snow || var16 == Blocks.ice) {
                     this.setBlockAndNotifyAdequately(var1, var3.add(var12, var9, var14), Blocks.packed_ice.getDefaultState());
                  }

                  if (var9 != 0 && var11 > 1) {
                     var16 = var1.getBlockState(var3.add(var12, -var9, var14)).getBlock();
                     if (var16.getMaterial() == Material.air || var16 == Blocks.dirt || var16 == Blocks.snow || var16 == Blocks.ice) {
                        this.setBlockAndNotifyAdequately(var1, var3.add(var12, -var9, var14), Blocks.packed_ice.getDefaultState());
                     }
                  }
               }
            }
         }
      }

      int var18 = var8 - 1;
      if (var18 < 0) {
         var18 = 0;
      } else if (var18 > 1) {
         var18 = 1;
      }

      for (int var19 = -var18; var19 <= var18; var19++) {
         for (int var20 = -var18; var20 <= var18; var20++) {
            BlockPos var21 = var3.add(var19, -1, var20);
            int var22 = 50;
            if (Math.abs(var19) == 1 && Math.abs(var20) == 1) {
               var22 = var2.nextInt(5);
            }

            while (var21.getY() > 50) {
               Block var23 = var1.getBlockState(var21).getBlock();
               if (var23.getMaterial() != Material.air && var23 != Blocks.dirt && var23 != Blocks.snow && var23 != Blocks.ice && var23 != Blocks.packed_ice) {
                  break;
               }

               this.setBlockAndNotifyAdequately(var1, var21, Blocks.packed_ice.getDefaultState());
               var21 = var21.down();
               if (--var22 <= 0) {
                  var21 = var21.down(var2.nextInt(5) + 1);
                  var22 = var2.nextInt(5);
               }
            }
         }
      }

      return true;
   }
}
