package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {
   public static final PropertyBool LOCKED = PropertyBool.create("locked");
   public static final PropertyInteger DELAY = PropertyInteger.create("delay", 1, 4);

   public BlockRedstoneRepeater(boolean powered) {
      super(powered);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DELAY, 1).withProperty(LOCKED, false));
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.diode.name");
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(LOCKED, this.isLocked(worldIn, pos, state));
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!playerIn.capabilities.allowEdit) {
         return false;
      }

      worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
      return true;
   }

   @Override
   public int getDelay(IBlockState state) {
      return state.getValue(DELAY) * 2;
   }

   @Override
   public IBlockState getPoweredState(IBlockState unpoweredState) {
      Integer integer = unpoweredState.getValue(DELAY);
      Boolean boolean2 = unpoweredState.getValue(LOCKED);
      EnumFacing enumFacing = unpoweredState.getValue(FACING);
      return Blocks.powered_repeater.getDefaultState().withProperty(FACING, enumFacing).withProperty(DELAY, integer).withProperty(LOCKED, boolean2);
   }

   @Override
   public IBlockState getUnpoweredState(IBlockState poweredState) {
      Integer integer = poweredState.getValue(DELAY);
      Boolean boolean2 = poweredState.getValue(LOCKED);
      EnumFacing enumFacing = poweredState.getValue(FACING);
      return Blocks.unpowered_repeater.getDefaultState().withProperty(FACING, enumFacing).withProperty(DELAY, integer).withProperty(LOCKED, boolean2);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.repeater;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.repeater;
   }

   @Override
   public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      return this.getPowerOnSides(worldIn, pos, state) > 0;
   }

   @Override
   public boolean canPowerSide(Block blockIn) {
      return isRedstoneRepeaterBlockID(blockIn);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.isRepeaterPowered) {
         EnumFacing enumFacing = state.getValue(FACING);
         double d = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2;
         double e = pos.getY() + 0.4F + (rand.nextFloat() - 0.5F) * 0.2;
         double d2 = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2;
         float f = -5.0F;
         if (rand.nextBoolean()) {
            f = state.getValue(DELAY) * 2 - 1;
         }

         f /= 16.0F;
         double d3 = f * enumFacing.getFrontOffsetX();
         double d4 = f * enumFacing.getFrontOffsetZ();
         worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d + d3, e, d2 + d4, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      this.notifyNeighbors(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(LOCKED, false).withProperty(DELAY, 1 + (meta >> 2));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(FACING).getHorizontalIndex();
      return i | state.getValue(DELAY) - 1 << 2;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, DELAY, LOCKED);
   }
}
