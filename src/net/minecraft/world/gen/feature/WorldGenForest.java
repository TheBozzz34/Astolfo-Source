package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
   private static final IBlockState field_181629_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
   private static final IBlockState field_181630_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)
      .withProperty(BlockOldLeaf.CHECK_DECAY, false);
   private boolean useExtraRandomHeight;
   private static final long a = 93949621887721L;

   public WorldGenForest(boolean var1, boolean var2) {
      super(var1);
      this.useExtraRandomHeight = var2;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = var2.nextInt(3) + 5;
      if (this.useExtraRandomHeight) {
         var7 += var2.nextInt(7);
      }

      boolean var8 = true;
      if (var3.getY() >= 1 && var3.getY() + var7 + 1 <= 256) {
         for (int var9 = var3.getY(); var9 <= var3.getY() + 1 + var7; var9++) {
            byte var10 = 1;
            if (var9 == var3.getY()) {
               var10 = 0;
            }

            if (var9 >= var3.getY() + 1 + var7 - 2) {
               var10 = 2;
            }

            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for (int var12 = var3.getX() - var10; var12 <= var3.getX() + var10 && var8; var12++) {
               for (int var13 = var3.getZ() - var10; var13 <= var3.getZ() + var10 && var8; var13++) {
                  if (var9 < 0 || var9 >= 256) {
                     var8 = false;
                  } else if (!this.func_150523_a(var1.getBlockState(var11.set(var12, var9, var13)).getBlock())) {
                     var8 = false;
                  }
               }
            }
         }

         if (!var8) {
            return false;
         }

         Block var19 = var1.getBlockState(var3.down()).getBlock();
         if ((var19 == Blocks.grass || var19 == Blocks.dirt || var19 == Blocks.farmland) && var3.getY() < 256 - var7 - 1) {
            this.func_175921_a(var1, var3.down());

            for (int var20 = var3.getY() - 3 + var7; var20 <= var3.getY() + var7; var20++) {
               int var22 = var20 - (var3.getY() + var7);
               int var24 = 1 - var22 / 2;

               for (int var25 = var3.getX() - var24; var25 <= var3.getX() + var24; var25++) {
                  int var14 = var25 - var3.getX();

                  for (int var15 = var3.getZ() - var24; var15 <= var3.getZ() + var24; var15++) {
                     int var16 = var15 - var3.getZ();
                     if (Math.abs(var14) != var24 || Math.abs(var16) != var24 || var2.nextInt(2) != 0 && var22 != 0) {
                        BlockPos var17 = new BlockPos(var25, var20, var15);
                        Block var18 = var1.getBlockState(var17).getBlock();
                        if (var18.getMaterial() == Material.air || var18.getMaterial() == Material.leaves) {
                           this.setBlockAndNotifyAdequately(var1, var17, field_181630_b);
                        }
                     }
                  }
               }
            }

            for (int var21 = 0; var21 < var7; var21++) {
               Block var23 = var1.getBlockState(var3.up(var21)).getBlock();
               if (var23.getMaterial() == Material.air || var23.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(var1, var3.up(var21), field_181629_a);
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
