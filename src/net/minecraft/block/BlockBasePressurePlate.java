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

   public BlockBasePressurePlate(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   public BlockBasePressurePlate(Material material, MapColor mapColor) {
      super(material, mapColor);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState0(worldIn.getBlockState(pos));
   }

   public void setBlockBoundsBasedOnState0(IBlockState state) {
      boolean bl = this.getRedstoneStrength(state) > 0;
      if (bl) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
      } else {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
      }
   }

   @Override
   public int tickRate(World worldIn) {
      return (int)L;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
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
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public boolean canSpawnInBlock() {
      return true;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.canBePlacedOn(worldIn, pos.down());
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.canBePlacedOn(worldIn, pos.down())) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   private boolean canBePlacedOn(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos) || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random randomIn) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         int i = this.getRedstoneStrength(state);
         if (i > 0) {
            this.updateState(worldIn, pos, state, i);
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote) {
         int i = this.getRedstoneStrength(state);
         if (i == 0) {
            this.updateState(worldIn, pos, state, i);
         }
      }
   }

   public void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
      int i = this.computeRedstoneStrength(worldIn, pos);
      boolean bl = oldRedstoneStrength > 0;
      boolean bl2 = i > 0;
      if (oldRedstoneStrength != i) {
         state = this.setRedstoneStrength(state, i);
         worldIn.setBlockState(pos, state, 2);
         this.updateNeighbors(worldIn, pos);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (!bl2 && bl) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      } else if (bl2 && !bl) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (bl2) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   public AxisAlignedBB getSensitiveAABB(BlockPos pos) {
      return new AxisAlignedBB(pos.getX() + 0.125F, pos.getY(), pos.getZ() + 0.125F, pos.getX() + 1 - 0.125F, pos.getY() + 0.25, pos.getZ() + 1 - 0.125F);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (this.getRedstoneStrength(state) > 0) {
         this.updateNeighbors(worldIn, pos);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public void updateNeighbors(World worldIn, BlockPos pos) {
      worldIn.notifyNeighborsOfStateChange(pos, this);
      worldIn.notifyNeighborsOfStateChange(pos.down(), this);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.getRedstoneStrength(state);
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return side == EnumFacing.UP ? this.getRedstoneStrength(state) : 0;
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

   public abstract int computeRedstoneStrength(World worldIn, BlockPos pos);

   public abstract int getRedstoneStrength(IBlockState state);

   public abstract IBlockState setRedstoneStrength(IBlockState state, int strength);
}
