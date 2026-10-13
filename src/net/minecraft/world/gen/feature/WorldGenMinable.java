package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMinable extends WorldGenerator {
   private final IBlockState oreBlock;
   private final int numberOfBlocks;
   private final Predicate<IBlockState> predicate;
   private static final long a = 92200702009520L;

   public WorldGenMinable(IBlockState var1, int var2) {
      this(var1, var2, BlockHelper.forBlock(Blocks.stone));
   }

   public WorldGenMinable(IBlockState var1, int var2, Predicate<IBlockState> var3) {
      this.oreBlock = var1;
      this.numberOfBlocks = var2;
      this.predicate = var3;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      float var8 = var2.nextFloat() * (float) Math.PI;
      double var9 = var3.getX() + 8 + MathHelper.sin(var8) * this.numberOfBlocks / 8.0F;
      double var11 = var3.getX() + 8 - MathHelper.sin(var8) * this.numberOfBlocks / 8.0F;
      double var13 = var3.getZ() + 8 + MathHelper.cos(var8) * this.numberOfBlocks / 8.0F;
      double var15 = var3.getZ() + 8 - MathHelper.cos(var8) * this.numberOfBlocks / 8.0F;
      double var17 = var3.getY() + var2.nextInt(3) - 2;
      double var19 = var3.getY() + var2.nextInt(3) - 2;

      for (int var21 = 0; var21 < this.numberOfBlocks; var21++) {
         float var22 = (float)var21 / this.numberOfBlocks;
         double var23 = var9 + (var11 - var9) * var22;
         double var25 = var17 + (var19 - var17) * var22;
         double var27 = var13 + (var15 - var13) * var22;
         double var29 = var2.nextDouble() * this.numberOfBlocks / 16.0;
         double var31 = (MathHelper.sin((float) Math.PI * var22) + 1.0F) * var29 + 1.0;
         double var33 = (MathHelper.sin((float) Math.PI * var22) + 1.0F) * var29 + 1.0;
         int var35 = MathHelper.floor_double(var23 - var31 / 2.0);
         int var36 = MathHelper.floor_double(var25 - var33 / 2.0);
         int var37 = MathHelper.floor_double(var27 - var31 / 2.0);
         int var38 = MathHelper.floor_double(var23 + var31 / 2.0);
         int var39 = MathHelper.floor_double(var25 + var33 / 2.0);
         int var40 = MathHelper.floor_double(var27 + var31 / 2.0);

         for (int var41 = var35; var41 <= var38; var41++) {
            double var42 = (var41 + 0.5 - var23) / (var31 / 2.0);
            if (var42 * var42 < 1.0) {
               for (int var44 = var36; var44 <= var39; var44++) {
                  double var45 = (var44 + 0.5 - var25) / (var33 / 2.0);
                  if (var42 * var42 + var45 * var45 < 1.0) {
                     for (int var47 = var37; var47 <= var40; var47++) {
                        double var48 = (var47 + 0.5 - var27) / (var31 / 2.0);
                        if (var42 * var42 + var45 * var45 + var48 * var48 < 1.0) {
                           BlockPos var50 = new BlockPos(var41, var44, var47);
                           if (this.predicate.apply(var1.getBlockState(var50))) {
                              var1.setBlockState(var50, this.oreBlock, 2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
