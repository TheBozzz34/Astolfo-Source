package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {
   public int adjacentSourceBlocks;
   private static final long e = 127452808295627L;

   public BlockDynamicLiquid(Material var1) {
      super(var1);
   }

   private void placeStaticBlock(World var1, BlockPos var2, IBlockState var3) {
      var1.setBlockState(var2, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, var3.getValue(LEVEL)), 2);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      int var8 = var3.getValue(LEVEL);
      byte var9 = 1;
      if (this.blockMaterial == Material.lava && !var1.provider.doesWaterVaporize()) {
         var9 = 2;
      }

      int var10 = this.tickRate(var1);
      if (var8 > 0) {
         int var11 = -100;
         this.adjacentSourceBlocks = 0;

         for (Object var13 : EnumFacing.Plane.HORIZONTAL) {
            var11 = this.checkAdjacentBlock(var1, var2.offset((EnumFacing)var13), var11);
         }

         int var18 = var11 + var9;
         if (var18 >= 8 || var11 < 0) {
            var18 = -1;
         }

         if (this.getLevel(var1, var2.up()) >= 0) {
            int var20 = this.getLevel(var1, var2.up());
            if (var20 >= 8) {
               var18 = var20;
            } else {
               var18 = var20 + 8;
            }
         }

         if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water) {
            IBlockState var21 = var1.getBlockState(var2.down());
            if (var21.getBlock().getMaterial().isSolid()) {
               var18 = 0;
            } else if (var21.getBlock().getMaterial() == this.blockMaterial && var21.getValue(LEVEL) == 0) {
               var18 = 0;
            }
         }

         if (this.blockMaterial == Material.lava && var8 < 8 && var18 < 8 && var18 > var8 && var4.nextInt(4) != 0) {
            var10 *= 4;
         }

         if (var18 == var8) {
            this.placeStaticBlock(var1, var2, var3);
         } else {
            var8 = var18;
            if (var18 < 0) {
               var1.setBlockToAir(var2);
            } else {
               var3 = var3.withProperty(LEVEL, var18);
               var1.setBlockState(var2, var3, 2);
               var1.scheduleUpdate(var2, this, var10);
               var1.notifyNeighborsOfStateChange(var2, this);
            }
         }
      } else {
         this.placeStaticBlock(var1, var2, var3);
      }

      IBlockState var17 = var1.getBlockState(var2.down());
      if (this.canFlowInto(var1, var2.down(), var17)) {
         if (this.blockMaterial == Material.lava && var1.getBlockState(var2.down()).getBlock().getMaterial() == Material.water) {
            var1.setBlockState(var2.down(), Blocks.stone.getDefaultState());
            this.triggerMixEffects(var1, var2.down());
            return;
         }

         if (var8 >= 8) {
            this.tryFlowInto(var1, var2.down(), var17, var8);
         } else {
            this.tryFlowInto(var1, var2.down(), var17, var8 + 8);
         }
      } else if (var8 >= 0 && (var8 == 0 || this.isBlocked(var1, var2.down(), var17))) {
         Set var19 = this.getPossibleFlowDirections(var1, var2);
         int var22 = var8 + var9;
         if (var8 >= 8) {
            var22 = 1;
         }

         if (var22 >= 8) {
            return;
         }

         for (Object var15$raw : var19) {
            EnumFacing var15 = (EnumFacing)var15$raw;
            this.tryFlowInto(var1, var2.offset(var15), var1.getBlockState(var2.offset(var15)), var22);
         }
      }
   }

   private void tryFlowInto(World var1, BlockPos var2, IBlockState var3, int var4) {
      if (this.canFlowInto(var1, var2, var3)) {
         if (var3.getBlock() != Blocks.air) {
            if (this.blockMaterial == Material.lava) {
               this.triggerMixEffects(var1, var2);
            } else {
               var3.getBlock().dropBlockAsItem(var1, var2, var3, 0);
            }
         }

         var1.setBlockState(var2, this.getDefaultState().withProperty(LEVEL, var4), 3);
      }
   }

   private int func_176374_a(World var1, BlockPos var2, int var3, EnumFacing var4) {
      int var8 = 1000;

      for (Object var10 : EnumFacing.Plane.HORIZONTAL) {
         if (var10 != var4) {
            BlockPos var11 = var2.offset((EnumFacing)var10);
            IBlockState var12 = var1.getBlockState(var11);
            if (!this.isBlocked(var1, var11, var12) && (var12.getBlock().getMaterial() != this.blockMaterial || var12.getValue(LEVEL) > 0)) {
               if (!this.isBlocked(var1, var11.down(), var12)) {
                  return var3;
               }

               if (var3 < 4) {
                  int var13 = this.func_176374_a(var1, var11, var3 + 1, ((EnumFacing)var10).getOpposite());
                  if (var13 < var8) {
                     var8 = var13;
                  }
               }
            }
         }
      }

      return var8;
   }

   private Set<EnumFacing> getPossibleFlowDirections(World var1, BlockPos var2) {
      int var6 = 1000;
      EnumSet var7 = EnumSet.noneOf(EnumFacing.class);

      for (Object var9 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos var10 = var2.offset((EnumFacing)var9);
         IBlockState var11 = var1.getBlockState(var10);
         if (!this.isBlocked(var1, var10, var11) && (var11.getBlock().getMaterial() != this.blockMaterial || var11.getValue(LEVEL) > 0)) {
            int var12;
            if (this.isBlocked(var1, var10.down(), var1.getBlockState(var10.down()))) {
               var12 = this.func_176374_a(var1, var10, 1, ((EnumFacing)var9).getOpposite());
            } else {
               var12 = 0;
            }

            if (var12 < var6) {
               var7.clear();
            }

            if (var12 <= var6) {
               var7.add((EnumFacing)var9);
               var6 = var12;
            }
         }
      }

      return var7;
   }

   private boolean isBlocked(World var1, BlockPos var2, IBlockState var3) {
      Block var7 = var1.getBlockState(var2).getBlock();
      return !(var7 instanceof BlockDoor) && var7 != Blocks.standing_sign && var7 != Blocks.ladder && var7 != Blocks.reeds
         ? (var7.blockMaterial == Material.portal ? true : var7.blockMaterial.blocksMovement())
         : true;
   }

   public int checkAdjacentBlock(World var1, BlockPos var2, int var3) {
      int var7 = this.getLevel(var1, var2);
      if (var7 < 0) {
         return var3;
      }

      if (var7 == 0) {
         this.adjacentSourceBlocks++;
      }

      if (var7 >= 8) {
         var7 = 0;
      }

      return var3 >= 0 && var7 >= var3 ? var3 : var7;
   }

   private boolean canFlowInto(World var1, BlockPos var2, IBlockState var3) {
      Material var7 = var3.getBlock().getMaterial();
      return var7 != this.blockMaterial && var7 != Material.lava && !this.isBlocked(var1, var2, var3);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      if (!this.checkForMixing(var1, var2, var3)) {
         var1.scheduleUpdate(var2, this, this.tickRate(var1));
      }
   }
}
