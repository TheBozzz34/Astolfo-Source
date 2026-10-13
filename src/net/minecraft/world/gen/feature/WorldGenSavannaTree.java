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

public class WorldGenSavannaTree extends WorldGenAbstractTree {
   private static final IBlockState field_181643_a = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA);
   private static final IBlockState field_181644_b = Blocks.leaves2
      .getDefaultState()
      .withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private static final long a = 68662523949693L;

   public WorldGenSavannaTree(boolean var1) {
      super(var1);
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = var2.nextInt(3) + var2.nextInt(3) + 5;
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

         Block var24 = var1.getBlockState(var3.down()).getBlock();
         if ((var24 == Blocks.grass || var24 == Blocks.dirt) && var3.getY() < 256 - var7 - 1) {
            this.func_175921_a(var1, var3.down());
            EnumFacing var25 = EnumFacing.Plane.HORIZONTAL.random(var2);
            int var26 = var7 - var2.nextInt(4) - 1;
            int var27 = 3 - var2.nextInt(3);
            int var28 = var3.getX();
            int var14 = var3.getZ();
            int var15 = 0;

            for (int var16 = 0; var16 < var7; var16++) {
               int var17 = var3.getY() + var16;
               if (var16 >= var26 && var27 > 0) {
                  var28 += var25.getFrontOffsetX();
                  var14 += var25.getFrontOffsetZ();
                  var27--;
               }

               BlockPos var18 = new BlockPos(var28, var17, var14);
               Material var19 = var1.getBlockState(var18).getBlock().getMaterial();
               if (var19 == Material.air || var19 == Material.leaves) {
                  this.func_181642_b(var1, var18);
                  var15 = var17;
               }
            }

            BlockPos var32 = new BlockPos(var28, var15, var14);

            for (int var34 = -3; var34 <= 3; var34++) {
               for (int var37 = -3; var37 <= 3; var37++) {
                  if (Math.abs(var34) != 3 || Math.abs(var37) != 3) {
                     this.func_175924_b(var1, var32.add(var34, 0, var37));
                  }
               }
            }

            var32 = var32.up();

            for (int var35 = -1; var35 <= 1; var35++) {
               for (int var38 = -1; var38 <= 1; var38++) {
                  this.func_175924_b(var1, var32.add(var35, 0, var38));
               }
            }

            this.func_175924_b(var1, var32.east(2));
            this.func_175924_b(var1, var32.west(2));
            this.func_175924_b(var1, var32.south(2));
            this.func_175924_b(var1, var32.north(2));
            var28 = var3.getX();
            var14 = var3.getZ();
            EnumFacing var36 = EnumFacing.Plane.HORIZONTAL.random(var2);
            if (var36 != var25) {
               int var39 = var26 - var2.nextInt(2) - 1;
               int var40 = 1 + var2.nextInt(3);
               var15 = 0;

               for (int var20 = var39; var20 < var7 && var40 > 0; var40--) {
                  if (var20 >= 1) {
                     int var21 = var3.getY() + var20;
                     var28 += var36.getFrontOffsetX();
                     var14 += var36.getFrontOffsetZ();
                     BlockPos var22 = new BlockPos(var28, var21, var14);
                     Material var23 = var1.getBlockState(var22).getBlock().getMaterial();
                     if (var23 == Material.air || var23 == Material.leaves) {
                        this.func_181642_b(var1, var22);
                        var15 = var21;
                     }
                  }

                  var20++;
               }

               if (var15 > 0) {
                  BlockPos var41 = new BlockPos(var28, var15, var14);

                  for (int var43 = -2; var43 <= 2; var43++) {
                     for (int var45 = -2; var45 <= 2; var45++) {
                        if (Math.abs(var43) != 2 || Math.abs(var45) != 2) {
                           this.func_175924_b(var1, var41.add(var43, 0, var45));
                        }
                     }
                  }

                  var41 = var41.up();

                  for (int var44 = -1; var44 <= 1; var44++) {
                     for (int var46 = -1; var46 <= 1; var46++) {
                        this.func_175924_b(var1, var41.add(var44, 0, var46));
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

   private void func_181642_b(World var1, BlockPos var2) {
      this.setBlockAndNotifyAdequately(var1, var2, field_181643_a);
   }

   private void func_175924_b(World var1, BlockPos var2) {
      Material var6 = var1.getBlockState(var2).getBlock().getMaterial();
      if (var6 == Material.air || var6 == Material.leaves) {
         this.setBlockAndNotifyAdequately(var1, var2, field_181644_b);
      }
   }
}
