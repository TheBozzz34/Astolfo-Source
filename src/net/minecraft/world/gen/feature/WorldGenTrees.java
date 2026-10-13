package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree {
   private static final IBlockState field_181653_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
   private static final IBlockState field_181654_b = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private final int minTreeHeight;
   private final boolean vinesGrow;
   private final IBlockState metaWood;
   private final IBlockState metaLeaves;
   private static final long b = 82291100903143L;

   public WorldGenTrees(boolean var1) {
      this(var1, 4, field_181653_a, field_181654_b, false);
   }

   public WorldGenTrees(boolean var1, int var2, IBlockState var3, IBlockState var4, boolean var5) {
      super(var1);
      this.minTreeHeight = var2;
      this.metaWood = var3;
      this.metaLeaves = var4;
      this.vinesGrow = var5;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = var2.nextInt(3) + this.minTreeHeight;
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

         Block var22 = var1.getBlockState(var3.down()).getBlock();
         if ((var22 == Blocks.grass || var22 == Blocks.dirt || var22 == Blocks.farmland) && var3.getY() < 256 - var7 - 1) {
            this.func_175921_a(var1, var3.down());
            byte var23 = 3;
            byte var24 = 0;

            for (int var25 = var3.getY() - var23 + var7; var25 <= var3.getY() + var7; var25++) {
               int var29 = var25 - (var3.getY() + var7);
               int var14 = var24 + 1 - var29 / 2;

               for (int var15 = var3.getX() - var14; var15 <= var3.getX() + var14; var15++) {
                  int var16 = var15 - var3.getX();

                  for (int var17 = var3.getZ() - var14; var17 <= var3.getZ() + var14; var17++) {
                     int var18 = var17 - var3.getZ();
                     if (Math.abs(var16) != var14 || Math.abs(var18) != var14 || var2.nextInt(2) != 0 && var29 != 0) {
                        BlockPos var19 = new BlockPos(var15, var25, var17);
                        Block var20 = var1.getBlockState(var19).getBlock();
                        if (var20.getMaterial() == Material.air || var20.getMaterial() == Material.leaves || var20.getMaterial() == Material.vine) {
                           this.setBlockAndNotifyAdequately(var1, var19, this.metaLeaves);
                        }
                     }
                  }
               }
            }

            for (int var26 = 0; var26 < var7; var26++) {
               Block var30 = var1.getBlockState(var3.up(var26)).getBlock();
               if (var30.getMaterial() == Material.air || var30.getMaterial() == Material.leaves || var30.getMaterial() == Material.vine) {
                  this.setBlockAndNotifyAdequately(var1, var3.up(var26), this.metaWood);
                  if (this.vinesGrow && var26 > 0) {
                     if (var2.nextInt(3) > 0 && var1.isAirBlock(var3.add(-1, var26, 0))) {
                        this.func_181651_a(var1, var3.add(-1, var26, 0), BlockVine.EAST);
                     }

                     if (var2.nextInt(3) > 0 && var1.isAirBlock(var3.add(1, var26, 0))) {
                        this.func_181651_a(var1, var3.add(1, var26, 0), BlockVine.WEST);
                     }

                     if (var2.nextInt(3) > 0 && var1.isAirBlock(var3.add(0, var26, -1))) {
                        this.func_181651_a(var1, var3.add(0, var26, -1), BlockVine.SOUTH);
                     }

                     if (var2.nextInt(3) > 0 && var1.isAirBlock(var3.add(0, var26, 1))) {
                        this.func_181651_a(var1, var3.add(0, var26, 1), BlockVine.NORTH);
                     }
                  }
               }
            }

            if (this.vinesGrow) {
               for (int var27 = var3.getY() - 3 + var7; var27 <= var3.getY() + var7; var27++) {
                  int var31 = var27 - (var3.getY() + var7);
                  int var33 = 2 - var31 / 2;
                  BlockPos.MutableBlockPos var35 = new BlockPos.MutableBlockPos();

                  for (int var37 = var3.getX() - var33; var37 <= var3.getX() + var33; var37++) {
                     for (int var38 = var3.getZ() - var33; var38 <= var3.getZ() + var33; var38++) {
                        var35.set(var37, var27, var38);
                        if (var1.getBlockState(var35).getBlock().getMaterial() == Material.leaves) {
                           BlockPos var39 = var35.west();
                           BlockPos var40 = var35.east();
                           BlockPos var41 = var35.north();
                           BlockPos var21 = var35.south();
                           if (var2.nextInt(4) == 0 && var1.getBlockState(var39).getBlock().getMaterial() == Material.air) {
                              this.func_181650_b(var1, var39, BlockVine.EAST);
                           }

                           if (var2.nextInt(4) == 0 && var1.getBlockState(var40).getBlock().getMaterial() == Material.air) {
                              this.func_181650_b(var1, var40, BlockVine.WEST);
                           }

                           if (var2.nextInt(4) == 0 && var1.getBlockState(var41).getBlock().getMaterial() == Material.air) {
                              this.func_181650_b(var1, var41, BlockVine.SOUTH);
                           }

                           if (var2.nextInt(4) == 0 && var1.getBlockState(var21).getBlock().getMaterial() == Material.air) {
                              this.func_181650_b(var1, var21, BlockVine.NORTH);
                           }
                        }
                     }
                  }
               }

               if (var2.nextInt(5) == 0 && var7 > 5) {
                  for (int var28 = 0; var28 < 2; var28++) {
                     for (Object var34 : EnumFacing.Plane.HORIZONTAL) {
                        if (var2.nextInt(4 - var28) == 0) {
                           EnumFacing var36 = ((EnumFacing)var34).getOpposite();
                           this.func_181652_a(
                              var1, var2.nextInt(3), var3.add(var36.getFrontOffsetX(), var7 - 5 + var28, var36.getFrontOffsetZ()), (EnumFacing)var34
                           );
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

   private void func_181652_a(World var1, int var2, BlockPos var3, EnumFacing var4) {
      this.setBlockAndNotifyAdequately(var1, var3, Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.AGE, var2).withProperty(BlockCocoa.FACING, var4));
   }

   private void func_181651_a(World var1, BlockPos var2, PropertyBool var3) {
      this.setBlockAndNotifyAdequately(var1, var2, Blocks.vine.getDefaultState().withProperty(var3, true));
   }

   private void func_181650_b(World var1, BlockPos var2, PropertyBool var3) {
      this.func_181651_a(var1, var2, var3);
      int var7 = 4;

      for (BlockPos var8 = var2.down(); var1.getBlockState(var8).getBlock().getMaterial() == Material.air && var7 > 0; var7--) {
         this.func_181651_a(var1, var8, var3);
         var8 = var8.down();
      }
   }
}
