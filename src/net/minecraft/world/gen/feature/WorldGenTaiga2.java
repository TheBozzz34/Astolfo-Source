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
   private static final long a = 121062372297628L;

   public WorldGenTaiga2(boolean var1) {
      super(var1);
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = var2.nextInt(4) + 6;
      int var8 = 1 + var2.nextInt(2);
      int var9 = var7 - var8;
      int var10 = 2 + var2.nextInt(2);
      boolean var11 = true;
      if (var3.getY() >= 1 && var3.getY() + var7 + 1 <= 256) {
         for (int var12 = var3.getY(); var12 <= var3.getY() + 1 + var7 && var11; var12++) {
            int var13 = 1;
            if (var12 - var3.getY() < var8) {
               var13 = 0;
            } else {
               var13 = var10;
            }

            BlockPos.MutableBlockPos var14 = new BlockPos.MutableBlockPos();

            for (int var15 = var3.getX() - var13; var15 <= var3.getX() + var13 && var11; var15++) {
               for (int var16 = var3.getZ() - var13; var16 <= var3.getZ() + var13 && var11; var16++) {
                  if (var12 >= 0 && var12 < 256) {
                     Block var17 = var1.getBlockState(var14.set(var15, var12, var16)).getBlock();
                     if (var17.getMaterial() != Material.air && var17.getMaterial() != Material.leaves) {
                        var11 = false;
                     }
                  } else {
                     var11 = false;
                  }
               }
            }
         }

         if (!var11) {
            return false;
         }

         Block var23 = var1.getBlockState(var3.down()).getBlock();
         if ((var23 == Blocks.grass || var23 == Blocks.dirt || var23 == Blocks.farmland) && var3.getY() < 256 - var7 - 1) {
            this.func_175921_a(var1, var3.down());
            int var25 = var2.nextInt(2);
            int var26 = 1;
            byte var27 = 0;

            for (int var28 = 0; var28 <= var9; var28++) {
               int var30 = var3.getY() + var7 - var28;

               for (int var18 = var3.getX() - var25; var18 <= var3.getX() + var25; var18++) {
                  int var19 = var18 - var3.getX();

                  for (int var20 = var3.getZ() - var25; var20 <= var3.getZ() + var25; var20++) {
                     int var21 = var20 - var3.getZ();
                     if (Math.abs(var19) != var25 || Math.abs(var21) != var25 || var25 <= 0) {
                        BlockPos var22 = new BlockPos(var18, var30, var20);
                        if (!var1.getBlockState(var22).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(var1, var22, field_181646_b);
                        }
                     }
                  }
               }

               if (var25 >= var26) {
                  var25 = var27;
                  var27 = 1;
                  if (++var26 > var10) {
                     var26 = var10;
                  }
               } else {
                  var25++;
               }
            }

            int var29 = var2.nextInt(3);

            for (int var31 = 0; var31 < var7 - var29; var31++) {
               Block var32 = var1.getBlockState(var3.up(var31)).getBlock();
               if (var32.getMaterial() == Material.air || var32.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(var1, var3.up(var31), field_181645_a);
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
