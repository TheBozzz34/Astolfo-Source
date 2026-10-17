package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {
   public final boolean isRepeaterPowered;

   public BlockRedstoneDiode(boolean powered) {
      super(Material.circuits);
      this.isRepeaterPowered = powered;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) ? super.canPlaceBlockAt(worldIn, pos) : false;
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random randomIn) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean bl = this.shouldBePowered(worldIn, pos, state);
         if (this.isRepeaterPowered && !bl) {
            worldIn.setBlockState(pos, this.getUnpoweredState(state), 2);
         } else if (!this.isRepeaterPowered) {
            worldIn.setBlockState(pos, this.getPoweredState(state), 2);
            if (!bl) {
               worldIn.updateBlockTick(pos, this.getPoweredState(state).getBlock(), this.getTickDelay(state), -1);
            }
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return side.getAxis() != EnumFacing.Axis.Y;
   }

   public boolean isPowered(IBlockState state) {
      return this.isRepeaterPowered;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.getWeakPower(worldIn, pos, state, side);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !this.isPowered(state) ? 0 : (state.getValue(FACING) == side ? this.getActiveSignal(worldIn, pos, state) : 0);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (this.canBlockStay(worldIn, pos)) {
         this.updateState(worldIn, pos, state);
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   public void updateState(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean bl = this.shouldBePowered(worldIn, pos, state);
         if ((this.isRepeaterPowered && !bl || !this.isRepeaterPowered && bl) && !worldIn.isBlockTickPending(pos, this)) {
            byte b = -1;
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
               b = -3;
            } else if (this.isRepeaterPowered) {
               b = -2;
            }

            worldIn.updateBlockTick(pos, this, this.getDelay(state), b);
         }
      }
   }

   public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      return false;
   }

   public boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
      return this.calculateInputStrength(worldIn, pos, state) > 0;
   }

   public int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumFacing = state.getValue(FACING);
      BlockPos blockPos = pos.offset(enumFacing);
      int i = worldIn.getRedstonePower(blockPos, enumFacing);
      if (i >= 15) {
         return i;
      }

      IBlockState iBlockState = worldIn.getBlockState(blockPos);
      return Math.max(i, iBlockState.getBlock() == Blocks.redstone_wire ? iBlockState.getValue(BlockRedstoneWire.POWER) : 0);
   }

   public int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumFacing = state.getValue(FACING);
      EnumFacing enumFacing2 = enumFacing.rotateY();
      EnumFacing enumFacing3 = enumFacing.rotateYCCW();
      return Math.max(this.getPowerOnSide(worldIn, pos.offset(enumFacing2), enumFacing2), this.getPowerOnSide(worldIn, pos.offset(enumFacing3), enumFacing3));
   }

   public int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      Block block = iBlockState.getBlock();
      return this.canPowerSide(block) ? (block == Blocks.redstone_wire ? iBlockState.getValue(BlockRedstoneWire.POWER) : worldIn.getStrongPower(pos, side)) : 0;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.shouldBePowered(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, 1);
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.notifyNeighbors(worldIn, pos, state);
   }

   public void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumFacing = state.getValue(FACING);
      BlockPos blockPos = pos.offset(enumFacing.getOpposite());
      worldIn.notifyBlockOfStateChange(blockPos, this);
      worldIn.notifyNeighborsOfStateExcept(blockPos, this, enumFacing);
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      if (this.isRepeaterPowered) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }

      super.onBlockDestroyedByPlayer(worldIn, pos, state);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   public boolean canPowerSide(Block blockIn) {
      return blockIn.canProvidePower();
   }

   public int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      return 15;
   }

   public static boolean isRedstoneRepeaterBlockID(Block blockIn) {
      return Blocks.unpowered_repeater.isAssociated(blockIn) || Blocks.unpowered_comparator.isAssociated(blockIn);
   }

   public boolean isAssociated(Block other) {
      return other == this.getPoweredState(this.getDefaultState()).getBlock() || other == this.getUnpoweredState(this.getDefaultState()).getBlock();
   }

   public boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumFacing = state.getValue(FACING).getOpposite();
      BlockPos blockPos = pos.offset(enumFacing);
      return isRedstoneRepeaterBlockID(worldIn.getBlockState(blockPos).getBlock()) ? worldIn.getBlockState(blockPos).getValue(FACING) != enumFacing : false;
   }

   public int getTickDelay(IBlockState state) {
      return this.getDelay(state);
   }

   public abstract int getDelay(IBlockState state);

   public abstract IBlockState getPoweredState(IBlockState unpoweredState);

   public abstract IBlockState getUnpoweredState(IBlockState poweredState);

   @Override
   public boolean isAssociatedBlock(Block other) {
      return this.isAssociated(other);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
