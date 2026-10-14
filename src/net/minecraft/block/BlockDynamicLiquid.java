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

   public BlockDynamicLiquid(Material materialIn) {
      super(materialIn);
   }

   private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState) {
      worldIn.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, currentState.getValue(LEVEL)), 2);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int i = state.getValue(LEVEL);
      byte b = 1;
      if (this.blockMaterial == Material.lava && !worldIn.provider.doesWaterVaporize()) {
         b = 2;
      }

      int j = this.tickRate(worldIn);
      if (i > 0) {
         int k = -100;
         this.adjacentSourceBlocks = 0;

         for (Object object : EnumFacing.Plane.HORIZONTAL) {
            k = this.checkAdjacentBlock(worldIn, pos.offset((EnumFacing)object), k);
         }

         int l = k + b;
         if (l >= 8 || k < 0) {
            l = -1;
         }

         if (this.getLevel(worldIn, pos.up()) >= 0) {
            int m = this.getLevel(worldIn, pos.up());
            if (m >= 8) {
               l = m;
            } else {
               l = m + 8;
            }
         }

         if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water) {
            IBlockState iBlockState = worldIn.getBlockState(pos.down());
            if (iBlockState.getBlock().getMaterial().isSolid()) {
               l = 0;
            } else if (iBlockState.getBlock().getMaterial() == this.blockMaterial && iBlockState.getValue(LEVEL) == 0) {
               l = 0;
            }
         }

         if (this.blockMaterial == Material.lava && i < 8 && l < 8 && l > i && rand.nextInt(4) != 0) {
            j *= 4;
         }

         if (l == i) {
            this.placeStaticBlock(worldIn, pos, state);
         } else {
            i = l;
            if (l < 0) {
               worldIn.setBlockToAir(pos);
            } else {
               state = state.withProperty(LEVEL, l);
               worldIn.setBlockState(pos, state, 2);
               worldIn.scheduleUpdate(pos, this, j);
               worldIn.notifyNeighborsOfStateChange(pos, this);
            }
         }
      } else {
         this.placeStaticBlock(worldIn, pos, state);
      }

      IBlockState iBlockState2 = worldIn.getBlockState(pos.down());
      if (this.canFlowInto(worldIn, pos.down(), iBlockState2)) {
         if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.down()).getBlock().getMaterial() == Material.water) {
            worldIn.setBlockState(pos.down(), Blocks.stone.getDefaultState());
            this.triggerMixEffects(worldIn, pos.down());
            return;
         }

         if (i >= 8) {
            this.tryFlowInto(worldIn, pos.down(), iBlockState2, i);
         } else {
            this.tryFlowInto(worldIn, pos.down(), iBlockState2, i + 8);
         }
      } else if (i >= 0 && (i == 0 || this.isBlocked(worldIn, pos.down(), iBlockState2))) {
         Set set = this.getPossibleFlowDirections(worldIn, pos);
         int n = i + b;
         if (i >= 8) {
            n = 1;
         }

         if (n >= 8) {
            return;
         }

         for (Object object2 : set) {
            EnumFacing enumFacing = (EnumFacing)object2;
            this.tryFlowInto(worldIn, pos.offset(enumFacing), worldIn.getBlockState(pos.offset(enumFacing)), n);
         }
      }
   }

   private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
      if (this.canFlowInto(worldIn, pos, state)) {
         if (state.getBlock() != Blocks.air) {
            if (this.blockMaterial == Material.lava) {
               this.triggerMixEffects(worldIn, pos);
            } else {
               state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
            }
         }

         worldIn.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, level), 3);
      }
   }

   private int func_176374_a(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost) {
      int i = 1000;

      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         if (object != calculateFlowCost) {
            BlockPos blockPos = pos.offset((EnumFacing)object);
            IBlockState iBlockState = worldIn.getBlockState(blockPos);
            if (!this.isBlocked(worldIn, blockPos, iBlockState) && (iBlockState.getBlock().getMaterial() != this.blockMaterial || iBlockState.getValue(LEVEL) > 0)) {
               if (!this.isBlocked(worldIn, blockPos.down(), iBlockState)) {
                  return distance;
               }

               if (distance < 4) {
                  int j = this.func_176374_a(worldIn, blockPos, distance + 1, ((EnumFacing)object).getOpposite());
                  if (j < i) {
                     i = j;
                  }
               }
            }
         }
      }

      return i;
   }

   private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {
      int i = 1000;
      EnumSet enumSet = EnumSet.noneOf(EnumFacing.class);

      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos = pos.offset((EnumFacing)object);
         IBlockState iBlockState = worldIn.getBlockState(blockPos);
         if (!this.isBlocked(worldIn, blockPos, iBlockState) && (iBlockState.getBlock().getMaterial() != this.blockMaterial || iBlockState.getValue(LEVEL) > 0)) {
            int j;
            if (this.isBlocked(worldIn, blockPos.down(), worldIn.getBlockState(blockPos.down()))) {
               j = this.func_176374_a(worldIn, blockPos, 1, ((EnumFacing)object).getOpposite());
            } else {
               j = 0;
            }

            if (j < i) {
               enumSet.clear();
            }

            if (j <= i) {
               enumSet.add((EnumFacing)object);
               i = j;
            }
         }
      }

      return enumSet;
   }

   private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state) {
      Block block = worldIn.getBlockState(pos).getBlock();
      return !(block instanceof BlockDoor) && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds
         ? (block.blockMaterial == Material.portal ? true : block.blockMaterial.blocksMovement())
         : true;
   }

   public int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel) {
      int i = this.getLevel(worldIn, pos);
      if (i < 0) {
         return currentMinLevel;
      }

      if (i == 0) {
         this.adjacentSourceBlocks++;
      }

      if (i >= 8) {
         i = 0;
      }

      return currentMinLevel >= 0 && i >= currentMinLevel ? currentMinLevel : i;
   }

   private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {
      Material material = state.getBlock().getMaterial();
      return material != this.blockMaterial && material != Material.lava && !this.isBlocked(worldIn, pos, state);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.checkForMixing(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }
}
