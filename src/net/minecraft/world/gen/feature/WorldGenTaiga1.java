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

public class WorldGenTaiga1 extends WorldGenAbstractTree {
   private static final IBlockState field_181636_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
   private static final IBlockState field_181637_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private static final long a = 74256985743528L;

   public WorldGenTaiga1() {
      super(false);
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = var2.nextInt(5) + 7;
      int var8 = var7 - var2.nextInt(2) - 3;
      int var9 = var7 - var8;
      int var10 = 1 + var2.nextInt(var9 + 1);
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
                  if (var12 < 0 || var12 >= 256) {
                     var11 = false;
                  } else if (!this.func_150523_a(var1.getBlockState(var14.set(var15, var12, var16)).getBlock())) {
                     var11 = false;
                  }
               }
            }
         }

         if (!var11) {
            return false;
         }

         Block var20 = var1.getBlockState(var3.down()).getBlock();
         if ((var20 == Blocks.grass || var20 == Blocks.dirt) && var3.getY() < 256 - var7 - 1) {
            this.func_175921_a(var1, var3.down());
            int var22 = 0;

            for (int var23 = var3.getY() + var7; var23 >= var3.getY() + var8; var23--) {
               for (int var25 = var3.getX() - var22; var25 <= var3.getX() + var22; var25++) {
                  int var27 = var25 - var3.getX();

                  for (int var17 = var3.getZ() - var22; var17 <= var3.getZ() + var22; var17++) {
                     int var18 = var17 - var3.getZ();
                     if (Math.abs(var27) != var22 || Math.abs(var18) != var22 || var22 <= 0) {
                        BlockPos var19 = new BlockPos(var25, var23, var17);
                        if (!var1.getBlockState(var19).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(var1, var19, field_181637_b);
                        }
                     }
                  }
               }

               if (var22 >= 1 && var23 == var3.getY() + var8 + 1) {
                  var22--;
               } else if (var22 < var10) {
                  var22++;
               }
            }

            for (int var24 = 0; var24 < var7 - 1; var24++) {
               Block var26 = var1.getBlockState(var3.up(var24)).getBlock();
               if (var26.getMaterial() == Material.air || var26.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(var1, var3.up(var24), field_181636_a);
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
