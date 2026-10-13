package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid {
   private static final long e = 125740223855698L;

   public BlockStaticLiquid(Material var1) {
      super(var1);
      this.setTickRandomly(false);
      if (var1 == Material.lava) {
         this.setTickRandomly(true);
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.checkForMixing(var1, var2, var3)) {
         this.updateLiquid(var1, var2, var3);
      }
   }

   private void updateLiquid(World var1, BlockPos var2, IBlockState var3) {
      BlockDynamicLiquid var4 = getFlowingBlock(this.blockMaterial);
      var1.setBlockState(var2, var4.getDefaultState().withProperty(LEVEL, var3.getValue(LEVEL)), 2);
      var1.scheduleUpdate(var2, var4, this.tickRate(var1));
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.blockMaterial == Material.lava && var1.getGameRules().getBoolean("doFireTick")) {
         int var8 = var4.nextInt(3);
         if (var8 > 0) {
            BlockPos var9 = var2;

            for (int var10 = 0; var10 < var8; var10++) {
               var9 = var9.add(var4.nextInt(3) - 1, 1, var4.nextInt(3) - 1);
               Block var11 = var1.getBlockState(var9).getBlock();
               if (var11.blockMaterial == Material.air) {
                  if (this.isSurroundingBlockFlammable(var1, var9)) {
                     var1.setBlockState(var9, Blocks.fire.getDefaultState());
                     return;
                  }
               } else if (var11.blockMaterial.blocksMovement()) {
                  return;
               }
            }
         } else {
            for (int var12 = 0; var12 < 3; var12++) {
               BlockPos var13 = var2.add(var4.nextInt(3) - 1, 0, var4.nextInt(3) - 1);
               if (var1.isAirBlock(var13.up()) && this.getCanBlockBurn(var1, var13)) {
                  var1.setBlockState(var13.up(), Blocks.fire.getDefaultState());
               }
            }
         }
      }
   }

   public boolean isSurroundingBlockFlammable(World var1, BlockPos var2) {
      for (EnumFacing var9 : EnumFacing.values()) {
         if (this.getCanBlockBurn(var1, var2.offset(var9))) {
            return true;
         }
      }

      return false;
   }

   private boolean getCanBlockBurn(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getBlock().getMaterial().getCanBurn();
   }
}
