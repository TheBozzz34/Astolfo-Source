package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   private static final long e = 18717455217648L;
   private static final long L = 8260034292818640916L;

   public BlockBasePressurePlate(Material var1) {
      this(var1, var1.getMaterialMapColor());
   }

   public BlockBasePressurePlate(Material var1, MapColor var2) {
      super(var1, var2);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.setBlockBoundsBasedOnState0(var1.getBlockState(var2));
   }

   public void setBlockBoundsBasedOnState0(IBlockState var1) {
      boolean var5 = this.getRedstoneStrength(var1) > 0;
      if (var5) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
      } else {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
      }
   }

   @Override
   public int tickRate(World var1) {
      return (int)L;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return true;
   }

   @Override
   public boolean canSpawnInBlock() {
      return true;
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return this.canBePlacedOn(var1, var2.down());
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.canBePlacedOn(var1, var2.down())) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }
   }

   private boolean canBePlacedOn(World var1, BlockPos var2) {
      return World.doesBlockHaveSolidTopSurface(var1, var2) || var1.getBlockState(var2).getBlock() instanceof BlockFence;
   }

   @Override
   public void randomTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote) {
         int var8 = this.getRedstoneStrength(var3);
         if (var8 > 0) {
            this.updateState(var1, var2, var3, var8);
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.isRemote) {
         int var8 = this.getRedstoneStrength(var3);
         if (var8 == 0) {
            this.updateState(var1, var2, var3, var8);
         }
      }
   }

   public void updateState(World var1, BlockPos var2, IBlockState var3, int var4) {
      int var8 = this.computeRedstoneStrength(var1, var2);
      boolean var9 = var4 > 0;
      boolean var10 = var8 > 0;
      if (var4 != var8) {
         var3 = this.setRedstoneStrength(var3, var8);
         var1.setBlockState(var2, var3, 2);
         this.updateNeighbors(var1, var2);
         var1.markBlockRangeForRenderUpdate(var2, var2);
      }

      if (!var10 && var9) {
         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.1, var2.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      } else if (var10 && !var9) {
         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.1, var2.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (var10) {
         var1.scheduleUpdate(var2, this, this.tickRate(var1));
      }
   }

   public AxisAlignedBB getSensitiveAABB(BlockPos var1) {
      return new AxisAlignedBB(var1.getX() + 0.125F, var1.getY(), var1.getZ() + 0.125F, var1.getX() + 1 - 0.125F, var1.getY() + 0.25, var1.getZ() + 1 - 0.125F);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      if (this.getRedstoneStrength(var3) > 0) {
         this.updateNeighbors(var1, var2);
      }

      super.breakBlock(var1, var2, var3);
   }

   public void updateNeighbors(World var1, BlockPos var2) {
      var1.notifyNeighborsOfStateChange(var2, this);
      var1.notifyNeighborsOfStateChange(var2.down(), this);
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return this.getRedstoneStrength(var3);
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var4 == EnumFacing.UP ? this.getRedstoneStrength(var3) : 0;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   public abstract int computeRedstoneStrength(World var1, BlockPos var2);

   public abstract int getRedstoneStrength(IBlockState var1);

   public abstract IBlockState setRedstoneStrength(IBlockState var1, int var2);
}
