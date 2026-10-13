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
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = var2.nextInt(4) + 5;

      while (var1.getBlockState(var3.down()).getBlock().getMaterial() == Material.water) {
         var3 = var3.down();
      }

      boolean var8 = true;
      if (var3.getY() >= 1 && var3.getY() + var7 + 1 <= 256) {
         for (int var9 = var3.getY(); var9 <= var3.getY() + 1 + var7; var9++) {
            byte var10 = 1;
            if (var9 == var3.getY()) {
               var10 = 0;
            }

            if (var9 >= var3.getY() + 1 + var7 - 2) {
               var10 = 3;
            }

            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for (int var12 = var3.getX() - var10; var12 <= var3.getX() + var10 && var8; var12++) {
               for (int var13 = var3.getZ() - var10; var13 <= var3.getZ() + var10 && var8; var13++) {
                  if (var9 >= 0 && var9 < 256) {
                     Block var14 = var1.getBlockState(var11.set(var12, var9, var13)).getBlock();
                     if (var14.getMaterial() != Material.air && var14.getMaterial() != Material.leaves) {
                        if (var14 != Blocks.water && var14 != Blocks.flowing_water) {
                           var8 = false;
                        } else if (var9 > var3.getY()) {
                           var8 = false;
                        }
                     }
                  } else {
                     var8 = false;
                  }
               }
            }
         }

         if (!var8) {
            return false;
         }

         Block var20 = var1.getBlockState(var3.down()).getBlock();
         if ((var20 == Blocks.grass || var20 == Blocks.dirt) && var3.getY() < 256 - var7 - 1) {
            this.func_175921_a(var1, var3.down());

            for (int var21 = var3.getY() - 3 + var7; var21 <= var3.getY() + var7; var21++) {
               int var24 = var21 - (var3.getY() + var7);
               int var27 = 2 - var24 / 2;

               for (int var29 = var3.getX() - var27; var29 <= var3.getX() + var27; var29++) {
                  int var31 = var29 - var3.getX();

                  for (int var15 = var3.getZ() - var27; var15 <= var3.getZ() + var27; var15++) {
                     int var16 = var15 - var3.getZ();
                     if (Math.abs(var31) != var27 || Math.abs(var16) != var27 || var2.nextInt(2) != 0 && var24 != 0) {
                        BlockPos var17 = new BlockPos(var29, var21, var15);
                        if (!var1.getBlockState(var17).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(var1, var17, field_181649_b);
                        }
                     }
                  }
               }
            }

            for (int var22 = 0; var22 < var7; var22++) {
               Block var25 = var1.getBlockState(var3.up(var22)).getBlock();
               if (var25.getMaterial() == Material.air || var25.getMaterial() == Material.leaves || var25 == Blocks.flowing_water || var25 == Blocks.water) {
                  this.setBlockAndNotifyAdequately(var1, var3.up(var22), field_181648_a);
               }
            }

            for (int var23 = var3.getY() - 3 + var7; var23 <= var3.getY() + var7; var23++) {
               int var26 = var23 - (var3.getY() + var7);
               int var28 = 2 - var26 / 2;
               BlockPos.MutableBlockPos var30 = new BlockPos.MutableBlockPos();

               for (int var32 = var3.getX() - var28; var32 <= var3.getX() + var28; var32++) {
                  for (int var33 = var3.getZ() - var28; var33 <= var3.getZ() + var28; var33++) {
                     var30.set(var32, var23, var33);
                     if (var1.getBlockState(var30).getBlock().getMaterial() == Material.leaves) {
                        BlockPos var34 = var30.west();
                        BlockPos var35 = var30.east();
                        BlockPos var18 = var30.north();
                        BlockPos var19 = var30.south();
                        if (var2.nextInt(4) == 0 && var1.getBlockState(var34).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(var1, var34, BlockVine.EAST);
                        }

                        if (var2.nextInt(4) == 0 && var1.getBlockState(var35).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(var1, var35, BlockVine.WEST);
                        }

                        if (var2.nextInt(4) == 0 && var1.getBlockState(var18).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(var1, var18, BlockVine.SOUTH);
                        }

                        if (var2.nextInt(4) == 0 && var1.getBlockState(var19).getBlock().getMaterial() == Material.air) {
                           this.func_181647_a(var1, var19, BlockVine.NORTH);
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

   private void func_181647_a(World var1, BlockPos var2, PropertyBool var3) {
      IBlockState var7 = Blocks.vine.getDefaultState().withProperty(var3, true);
      this.setBlockAndNotifyAdequately(var1, var2, var7);
      int var8 = 4;

      for (BlockPos var9 = var2.down(); var1.getBlockState(var9).getBlock().getMaterial() == Material.air && var8 > 0; var8--) {
         this.setBlockAndNotifyAdequately(var1, var9, var7);
         var9 = var9.down();
      }
   }
}
