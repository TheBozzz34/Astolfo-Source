package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> NORTH = PropertyEnum.create("north", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> EAST = PropertyEnum.create("east", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> SOUTH = PropertyEnum.create("south", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> WEST = PropertyEnum.create("west", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
   private boolean canProvidePower = true;
   private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

   public BlockRedstoneWire() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(NORTH, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(EAST, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(SOUTH, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(WEST, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(POWER, 0)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
      state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
      state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
      return state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
   }

   private BlockRedstoneWire.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
      BlockPos blockPos = pos.offset(direction);
      Block block = worldIn.getBlockState(pos.offset(direction)).getBlock();
      if (!canConnectTo(worldIn.getBlockState(blockPos), direction) && (block.isBlockNormalCube() || !canConnectUpwardsTo(worldIn.getBlockState(blockPos.down())))) {
         Block block2 = worldIn.getBlockState(pos.up()).getBlock();
         return !block2.isBlockNormalCube() && block.isBlockNormalCube() && canConnectUpwardsTo(worldIn.getBlockState(blockPos.up()))
            ? BlockRedstoneWire.EnumAttachPosition.UP
            : BlockRedstoneWire.EnumAttachPosition.NONE;
      } else {
         return BlockRedstoneWire.EnumAttachPosition.SIDE;
      }
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
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      return iBlockState.getBlock() != this ? super.colorMultiplier(worldIn, pos, renderPass) : this.colorMultiplier(iBlockState.getValue(POWER));
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || worldIn.getBlockState(pos.down()).getBlock() == Blocks.glowstone;
   }

   private IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state) {
      state = this.calculateCurrentChanges(worldIn, pos, pos, state);
      ArrayList arrayList = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();

      for (Object object : arrayList) {
         BlockPos blockPos = (BlockPos)object;
         worldIn.notifyNeighborsOfStateChange(blockPos, this);
      }

      return state;
   }

   private IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state) {
      IBlockState iBlockState = state;
      int i = state.getValue(POWER);
      int j = 0;
      j = this.getMaxCurrentStrength(worldIn, pos2, j);
      this.canProvidePower = false;
      int k = worldIn.isBlockIndirectlyGettingPowered(pos1);
      this.canProvidePower = true;
      if (k > 0 && k > j - 1) {
         j = k;
      }

      int l = 0;

      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos = pos1.offset((EnumFacing)object);
         boolean bl = blockPos.getX() != pos2.getX() || blockPos.getZ() != pos2.getZ();
         if (bl) {
            l = this.getMaxCurrentStrength(worldIn, blockPos, l);
         }

         if (worldIn.getBlockState(blockPos).getBlock().isNormalCube() && !worldIn.getBlockState(pos1.up()).getBlock().isNormalCube()) {
            if (bl && pos1.getY() >= pos2.getY()) {
               l = this.getMaxCurrentStrength(worldIn, blockPos.up(), l);
            }
         } else if (!worldIn.getBlockState(blockPos).getBlock().isNormalCube() && bl && pos1.getY() <= pos2.getY()) {
            l = this.getMaxCurrentStrength(worldIn, blockPos.down(), l);
         }
      }

      if (l > j) {
         j = l - 1;
      } else if (j > 0) {
         j--;
      } else {
         j = 0;
      }

      if (k > j - 1) {
         j = k;
      }

      if (i != j) {
         state = state.withProperty(POWER, j);
         if (worldIn.getBlockState(pos1) == iBlockState) {
            worldIn.setBlockState(pos1, state, 2);
         }

         this.blocksNeedingUpdate.add(pos1);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            this.blocksNeedingUpdate.add(pos1.offset(enumFacing));
         }
      }

      return state;
   }

   private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() == this) {
         worldIn.notifyNeighborsOfStateChange(pos, this);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         this.updateSurroundingRedstone(worldIn, pos, state);

         for (Object object : EnumFacing.Plane.VERTICAL) {
            worldIn.notifyNeighborsOfStateChange(pos.offset((EnumFacing)object), this);
         }

         for (Object object2 : EnumFacing.Plane.HORIZONTAL) {
            EnumFacing enumFacing = (EnumFacing)object2;
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumFacing));
         }

         for (Object object3 : EnumFacing.Plane.HORIZONTAL) {
            EnumFacing enumFacing2 = (EnumFacing)object3;
            BlockPos blockPos = pos.offset(enumFacing2);
            if (worldIn.getBlockState(blockPos).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.down());
            }
         }
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      if (!worldIn.isRemote) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }

         this.updateSurroundingRedstone(worldIn, pos, state);

         for (Object object : EnumFacing.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset((EnumFacing)object));
         }

         for (Object object2 : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockPos = pos.offset((EnumFacing)object2);
            if (worldIn.getBlockState(blockPos).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.down());
            }
         }
      }
   }

   private int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength) {
      if (worldIn.getBlockState(pos).getBlock() != this) {
         return strength;
      }

      int i = worldIn.getBlockState(pos).getValue(POWER);
      return i > strength ? i : strength;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         if (this.canPlaceBlockAt(worldIn, pos)) {
            this.updateSurroundingRedstone(worldIn, pos, state);
         } else {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.redstone;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !this.canProvidePower ? 0 : this.getWeakPower(worldIn, pos, state, side);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      if (!this.canProvidePower) {
         return 0;
      }

      int i = state.getValue(POWER);
      if (i == 0) {
         return 0;
      }

      if (side == EnumFacing.UP) {
         return i;
      }

      EnumSet enumSet = EnumSet.noneOf(EnumFacing.class);

      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         if (this.func_176339_d(worldIn, pos, (EnumFacing)object)) {
            enumSet.add((EnumFacing)object);
         }
      }

      if (side.getAxis().isHorizontal() && enumSet.isEmpty()) {
         return i;
      } else {
         return enumSet.contains(side) && !enumSet.contains(side.rotateYCCW()) && !enumSet.contains(side.rotateY()) ? i : 0;
      }
   }

   private boolean func_176339_d(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      BlockPos blockPos = pos.offset(side);
      IBlockState iBlockState = worldIn.getBlockState(blockPos);
      Block block = iBlockState.getBlock();
      boolean bl = block.isNormalCube();
      boolean bl2 = worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
      return !bl2 && bl && canConnectUpwardsTo(worldIn, blockPos.up())
         ? true
         : (
            canConnectTo(iBlockState, side)
               ? true
               : (
                  block == Blocks.powered_repeater && iBlockState.getValue(BlockRedstoneDiode.FACING) == side
                     ? true
                     : !bl && canConnectUpwardsTo(worldIn, blockPos.down())
               )
         );
   }

   public static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
      return canConnectUpwardsTo(worldIn.getBlockState(pos));
   }

   public static boolean canConnectUpwardsTo(IBlockState state) {
      return canConnectTo(state, (EnumFacing)null);
   }

   public static boolean canConnectTo(IBlockState blockState, EnumFacing side) {
      Block block = blockState.getBlock();
      if (block == Blocks.redstone_wire) {
         return true;
      } else if (Blocks.unpowered_repeater.isAssociated(block)) {
         EnumFacing enumFacing = blockState.getValue(BlockRedstoneRepeater.FACING);
         return enumFacing == side || enumFacing.getOpposite() == side;
      } else {
         return block.canProvidePower() && side != null;
      }
   }

   @Override
   public boolean canProvidePower() {
      return this.canProvidePower;
   }

   private int colorMultiplier(int powerLevel) {
      float f = powerLevel / 15.0F;
      float g = f * 0.6F + 0.4F;
      if (powerLevel == 0) {
         g = 0.3F;
      }

      float h = f * f * 0.7F - 0.5F;
      float f2 = f * f * 0.6F - 0.7F;
      if (h < 0.0F) {
         h = 0.0F;
      }

      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      int i = MathHelper.clamp_int((int)(g * 255.0F), 0, 255);
      int j = MathHelper.clamp_int((int)(h * 255.0F), 0, 255);
      int k = MathHelper.clamp_int((int)(f2 * 255.0F), 0, 255);
      return 0xFF000000 | i << 16 | j << 8 | k;
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int i = state.getValue(POWER);
      if (i != 0) {
         double d = pos.getX() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         double e = pos.getY() + 0.0625F;
         double d2 = pos.getZ() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         float f = i / 15.0F;
         float g = f * 0.6F + 0.4F;
         float h = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float f2 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d, e, d2, g, h, f2);
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.redstone;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWER, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWER);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, NORTH, EAST, SOUTH, WEST, POWER);
   }

   public enum EnumAttachPosition implements IStringSerializable {
      UP("up"),
      SIDE("side"),
      NONE("none");

      private final String name;

      EnumAttachPosition(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
