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
   private static final long i = 96428548308960L;

   public BlockRedstoneDiode(boolean var1) {
      super(Material.circuits);
      this.isRepeaterPowered = var1;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return World.doesBlockHaveSolidTopSurface(var1, var2.down()) ? super.canPlaceBlockAt(var1, var2) : false;
   }

   public boolean canBlockStay(World var1, BlockPos var2) {
      return World.doesBlockHaveSolidTopSurface(var1, var2.down());
   }

   @Override
   public void randomTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!this.isLocked(var1, var2, var3)) {
         boolean var8 = this.shouldBePowered(var1, var2, var3);
         if (this.isRepeaterPowered && !var8) {
            var1.setBlockState(var2, this.getUnpoweredState(var3), 2);
         } else if (!this.isRepeaterPowered) {
            var1.setBlockState(var2, this.getPoweredState(var3), 2);
            if (!var8) {
               var1.updateBlockTick(var2, this.getPoweredState(var3).getBlock(), this.getTickDelay(var3), -1);
            }
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3.getAxis() != EnumFacing.Axis.Y;
   }

   public boolean isPowered(IBlockState var1) {
      return this.isRepeaterPowered;
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return this.getWeakPower(var1, var2, var3, var4);
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return !this.isPowered(var3) ? 0 : (var3.getValue(FACING) == var4 ? this.getActiveSignal(var1, var2, var3) : 0);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (this.canBlockStay(var1, var2)) {
         this.updateState(var1, var2, var3);
      } else {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);

         for (EnumFacing var11 : EnumFacing.values()) {
            var1.notifyNeighborsOfStateChange(var2.offset(var11), this);
         }
      }
   }

   public void updateState(World var1, BlockPos var2, IBlockState var3) {
      if (!this.isLocked(var1, var2, var3)) {
         boolean var7 = this.shouldBePowered(var1, var2, var3);
         if ((this.isRepeaterPowered && !var7 || !this.isRepeaterPowered && var7) && !var1.isBlockTickPending(var2, this)) {
            byte var8 = -1;
            if (this.isFacingTowardsRepeater(var1, var2, var3)) {
               var8 = -3;
            } else if (this.isRepeaterPowered) {
               var8 = -2;
            }

            var1.updateBlockTick(var2, this, this.getDelay(var3), var8);
         }
      }
   }

   public boolean isLocked(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      return false;
   }

   public boolean shouldBePowered(World var1, BlockPos var2, IBlockState var3) {
      return this.calculateInputStrength(var1, var2, var3) > 0;
   }

   public int calculateInputStrength(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var7 = var3.getValue(FACING);
      BlockPos var8 = var2.offset(var7);
      int var9 = var1.getRedstonePower(var8, var7);
      if (var9 >= 15) {
         return var9;
      }

      IBlockState var10 = var1.getBlockState(var8);
      return Math.max(var9, var10.getBlock() == Blocks.redstone_wire ? var10.getValue(BlockRedstoneWire.POWER) : 0);
   }

   public int getPowerOnSides(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = var3.getValue(FACING);
      EnumFacing var5 = var4.rotateY();
      EnumFacing var6 = var4.rotateYCCW();
      return Math.max(this.getPowerOnSide(var1, var2.offset(var5), var5), this.getPowerOnSide(var1, var2.offset(var6), var6));
   }

   public int getPowerOnSide(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      IBlockState var7 = var1.getBlockState(var2);
      Block var8 = var7.getBlock();
      return this.canPowerSide(var8) ? (var8 == Blocks.redstone_wire ? var7.getValue(BlockRedstoneWire.POWER) : var1.getStrongPower(var2, var3)) : 0;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (this.shouldBePowered(var1, var2, var3)) {
         var1.scheduleUpdate(var2, this, 1);
      }
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.n(var1, var2, var3);
   }

   public void n(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = var3.getValue(FACING);
      BlockPos var5 = var2.offset(var4.getOpposite());
      var1.notifyBlockOfStateChange(var5, this);
      var1.notifyNeighborsOfStateExcept(var5, this, var4);
   }

   @Override
   public void onBlockDestroyedByPlayer(World var1, BlockPos var2, IBlockState var3) {
      if (this.isRepeaterPowered) {
         for (EnumFacing var10 : EnumFacing.values()) {
            var1.notifyNeighborsOfStateChange(var2.offset(var10), this);
         }
      }

      super.onBlockDestroyedByPlayer(var1, var2, var3);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   public boolean canPowerSide(Block var1) {
      return var1.canProvidePower();
   }

   public int getActiveSignal(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      return 15;
   }

   public static boolean isRedstoneRepeaterBlockID(Block var0) {
      return Blocks.unpowered_repeater.N(var0) || Blocks.unpowered_comparator.N(var0);
   }

   public boolean N(Block var1) {
      return var1 == this.getPoweredState(this.getDefaultState()).getBlock() || var1 == this.getUnpoweredState(this.getDefaultState()).getBlock();
   }

   public boolean isFacingTowardsRepeater(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var7 = var3.getValue(FACING).getOpposite();
      BlockPos var8 = var2.offset(var7);
      return isRedstoneRepeaterBlockID(var1.getBlockState(var8).getBlock()) ? var1.getBlockState(var8).getValue(FACING) != var7 : false;
   }

   public int getTickDelay(IBlockState var1) {
      return this.getDelay(var1);
   }

   public abstract int getDelay(IBlockState var1);

   public abstract IBlockState getPoweredState(IBlockState var1);

   public abstract IBlockState getUnpoweredState(IBlockState var1);

   @Override
   public boolean isAssociatedBlock(Block var1) {
      return this.N(var1);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
