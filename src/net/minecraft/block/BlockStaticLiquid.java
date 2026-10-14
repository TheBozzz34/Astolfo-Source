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

   public BlockStaticLiquid(Material materialIn) {
      super(materialIn);
      this.setTickRandomly(false);
      if (materialIn == Material.lava) {
         this.setTickRandomly(true);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.checkForMixing(worldIn, pos, state)) {
         this.updateLiquid(worldIn, pos, state);
      }
   }

   private void updateLiquid(World worldIn, BlockPos pos, IBlockState state) {
      BlockDynamicLiquid blockDynamicLiquid = getFlowingBlock(this.blockMaterial);
      worldIn.setBlockState(pos, blockDynamicLiquid.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
      worldIn.scheduleUpdate(pos, blockDynamicLiquid, this.tickRate(worldIn));
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.blockMaterial == Material.lava && worldIn.getGameRules().getBoolean("doFireTick")) {
         int i = rand.nextInt(3);
         if (i > 0) {
            BlockPos blockPos = pos;

            for (int j = 0; j < i; j++) {
               blockPos = blockPos.add(rand.nextInt(3) - 1, 1, rand.nextInt(3) - 1);
               Block block = worldIn.getBlockState(blockPos).getBlock();
               if (block.blockMaterial == Material.air) {
                  if (this.isSurroundingBlockFlammable(worldIn, blockPos)) {
                     worldIn.setBlockState(blockPos, Blocks.fire.getDefaultState());
                     return;
                  }
               } else if (block.blockMaterial.blocksMovement()) {
                  return;
               }
            }
         } else {
            for (int k = 0; k < 3; k++) {
               BlockPos blockPos2 = pos.add(rand.nextInt(3) - 1, 0, rand.nextInt(3) - 1);
               if (worldIn.isAirBlock(blockPos2.up()) && this.getCanBlockBurn(worldIn, blockPos2)) {
                  worldIn.setBlockState(blockPos2.up(), Blocks.fire.getDefaultState());
               }
            }
         }
      }
   }

   public boolean isSurroundingBlockFlammable(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (this.getCanBlockBurn(worldIn, pos.offset(enumFacing))) {
            return true;
         }
      }

      return false;
   }

   private boolean getCanBlockBurn(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().getMaterial().getCanBurn();
   }
}
