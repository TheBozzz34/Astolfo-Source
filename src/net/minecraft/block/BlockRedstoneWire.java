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
   private static final long e = 19469652593125L;

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
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      var1 = var1.withProperty(WEST, this.getAttachPosition(var2, var3, EnumFacing.WEST));
      var1 = var1.withProperty(EAST, this.getAttachPosition(var2, var3, EnumFacing.EAST));
      var1 = var1.withProperty(NORTH, this.getAttachPosition(var2, var3, EnumFacing.NORTH));
      return var1.withProperty(SOUTH, this.getAttachPosition(var2, var3, EnumFacing.SOUTH));
   }

   private BlockRedstoneWire.EnumAttachPosition getAttachPosition(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      BlockPos var7 = var2.offset(var3);
      Block var8 = var1.getBlockState(var2.offset(var3)).getBlock();
      if (!canConnectTo(var1.getBlockState(var7), var3) && (var8.isBlockNormalCube() || !canConnectUpwardsTo(var1.getBlockState(var7.down())))) {
         Block var9 = var1.getBlockState(var2.up()).getBlock();
         return !var9.isBlockNormalCube() && var8.isBlockNormalCube() && canConnectUpwardsTo(var1.getBlockState(var7.up()))
            ? BlockRedstoneWire.EnumAttachPosition.UP
            : BlockRedstoneWire.EnumAttachPosition.NONE;
      } else {
         return BlockRedstoneWire.EnumAttachPosition.SIDE;
      }
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
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      IBlockState var6 = var1.getBlockState(var2);
      return var6.getBlock() != this ? super.colorMultiplier(var1, var2, var3) : this.colorMultiplier(var6.getValue(POWER));
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return World.doesBlockHaveSolidTopSurface(var1, var2.down()) || var1.getBlockState(var2.down()).getBlock() == Blocks.glowstone;
   }

   private IBlockState updateSurroundingRedstone(World var1, BlockPos var2, IBlockState var3) {
      var3 = this.calculateCurrentChanges(var1, var2, var2, var3);
      ArrayList var7 = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();

      for (Object var9$raw : var7) {
         BlockPos var9 = (BlockPos)var9$raw;
         var1.notifyNeighborsOfStateChange(var9, this);
      }

      return var3;
   }

   private IBlockState calculateCurrentChanges(World var1, BlockPos var2, BlockPos var3, IBlockState var4) {
      IBlockState var8 = var4;
      int var9 = var4.getValue(POWER);
      int var10 = 0;
      var10 = this.getMaxCurrentStrength(var1, var3, var10);
      this.canProvidePower = false;
      int var11 = var1.isBlockIndirectlyGettingPowered(var2);
      this.canProvidePower = true;
      if (var11 > 0 && var11 > var10 - 1) {
         var10 = var11;
      }

      int var12 = 0;

      for (Object var14 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos var15 = var2.offset((EnumFacing)var14);
         boolean var16 = var15.getX() != var3.getX() || var15.getZ() != var3.getZ();
         if (var16) {
            var12 = this.getMaxCurrentStrength(var1, var15, var12);
         }

         if (var1.getBlockState(var15).getBlock().isNormalCube() && !var1.getBlockState(var2.up()).getBlock().isNormalCube()) {
            if (var16 && var2.getY() >= var3.getY()) {
               var12 = this.getMaxCurrentStrength(var1, var15.up(), var12);
            }
         } else if (!var1.getBlockState(var15).getBlock().isNormalCube() && var16 && var2.getY() <= var3.getY()) {
            var12 = this.getMaxCurrentStrength(var1, var15.down(), var12);
         }
      }

      if (var12 > var10) {
         var10 = var12 - 1;
      } else if (var10 > 0) {
         var10--;
      } else {
         var10 = 0;
      }

      if (var11 > var10 - 1) {
         var10 = var11;
      }

      if (var9 != var10) {
         var4 = var4.withProperty(POWER, var10);
         if (var1.getBlockState(var2) == var8) {
            var1.setBlockState(var2, var4, 2);
         }

         this.blocksNeedingUpdate.add(var2);

         for (EnumFacing var22 : EnumFacing.values()) {
            this.blocksNeedingUpdate.add(var2.offset(var22));
         }
      }

      return var4;
   }

   private void notifyWireNeighborsOfStateChange(World var1, BlockPos var2) {
      if (var1.getBlockState(var2).getBlock() == this) {
         var1.notifyNeighborsOfStateChange(var2, this);

         for (EnumFacing var9 : EnumFacing.values()) {
            var1.notifyNeighborsOfStateChange(var2.offset(var9), this);
         }
      }
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isRemote) {
         this.updateSurroundingRedstone(var1, var2, var3);

         for (Object var8 : EnumFacing.Plane.VERTICAL) {
            var1.notifyNeighborsOfStateChange(var2.offset((EnumFacing)var8), this);
         }

         for (Object var13 : EnumFacing.Plane.HORIZONTAL) {
            EnumFacing var9 = (EnumFacing)var13;
            this.notifyWireNeighborsOfStateChange(var1, var2.offset(var9));
         }

         for (Object var14 : EnumFacing.Plane.HORIZONTAL) {
            EnumFacing var15 = (EnumFacing)var14;
            BlockPos var10 = var2.offset(var15);
            if (var1.getBlockState(var10).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(var1, var10.up());
            } else {
               this.notifyWireNeighborsOfStateChange(var1, var10.down());
            }
         }
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      super.breakBlock(var1, var2, var3);
      if (!var1.isRemote) {
         for (EnumFacing var10 : EnumFacing.values()) {
            var1.notifyNeighborsOfStateChange(var2.offset(var10), this);
         }

         this.updateSurroundingRedstone(var1, var2, var3);

         for (Object var13 : EnumFacing.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(var1, var2.offset((EnumFacing)var13));
         }

         for (Object var14 : EnumFacing.Plane.HORIZONTAL) {
            BlockPos var15 = var2.offset((EnumFacing)var14);
            if (var1.getBlockState(var15).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(var1, var15.up());
            } else {
               this.notifyWireNeighborsOfStateChange(var1, var15.down());
            }
         }
      }
   }

   private int getMaxCurrentStrength(World var1, BlockPos var2, int var3) {
      if (var1.getBlockState(var2).getBlock() != this) {
         return var3;
      }

      int var7 = var1.getBlockState(var2).getValue(POWER);
      return var7 > var3 ? var7 : var3;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.isRemote) {
         if (this.canPlaceBlockAt(var1, var2)) {
            this.updateSurroundingRedstone(var1, var2, var3);
         } else {
            this.dropBlockAsItem(var1, var2, var3, 0);
            var1.setBlockToAir(var2);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.redstone;
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return !this.canProvidePower ? 0 : this.getWeakPower(var1, var2, var3, var4);
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!this.canProvidePower) {
         return 0;
      }

      int var8 = var3.getValue(POWER);
      if (var8 == 0) {
         return 0;
      }

      if (var4 == EnumFacing.UP) {
         return var8;
      }

      EnumSet var9 = EnumSet.noneOf(EnumFacing.class);

      for (Object var11 : EnumFacing.Plane.HORIZONTAL) {
         if (this.func_176339_d(var1, var2, (EnumFacing)var11)) {
            var9.add((EnumFacing)var11);
         }
      }

      if (var4.getAxis().isHorizontal() && var9.isEmpty()) {
         return var8;
      } else {
         return var9.contains(var4) && !var9.contains(var4.rotateYCCW()) && !var9.contains(var4.rotateY()) ? var8 : 0;
      }
   }

   private boolean func_176339_d(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      BlockPos var7 = var2.offset(var3);
      IBlockState var8 = var1.getBlockState(var7);
      Block var9 = var8.getBlock();
      boolean var10 = var9.isNormalCube();
      boolean var11 = var1.getBlockState(var2.up()).getBlock().isNormalCube();
      return !var11 && var10 && canConnectUpwardsTo(var1, var7.up())
         ? true
         : (
            canConnectTo(var8, var3)
               ? true
               : (var9 == Blocks.powered_repeater && var8.getValue(BlockRedstoneDiode.FACING) == var3 ? true : !var10 && canConnectUpwardsTo(var1, var7.down()))
         );
   }

   public static boolean canConnectUpwardsTo(IBlockAccess var0, BlockPos var1) {
      return canConnectUpwardsTo(var0.getBlockState(var1));
   }

   public static boolean canConnectUpwardsTo(IBlockState var0) {
      return canConnectTo(var0, (EnumFacing)null);
   }

   public static boolean canConnectTo(IBlockState var0, EnumFacing var1) {
      Block var5 = var0.getBlock();
      if (var5 == Blocks.redstone_wire) {
         return true;
      } else if (Blocks.unpowered_repeater.N(var5)) {
         EnumFacing var6 = var0.getValue(BlockRedstoneRepeater.FACING);
         return var6 == var1 || var6.getOpposite() == var1;
      } else {
         return var5.canProvidePower() && var1 != null;
      }
   }

   @Override
   public boolean canProvidePower() {
      return this.canProvidePower;
   }

   private int colorMultiplier(int var1) {
      float var5 = var1 / 15.0F;
      float var6 = var5 * 0.6F + 0.4F;
      if (var1 == 0) {
         var6 = 0.3F;
      }

      float var7 = var5 * var5 * 0.7F - 0.5F;
      float var8 = var5 * var5 * 0.6F - 0.7F;
      if (var7 < 0.0F) {
         var7 = 0.0F;
      }

      if (var8 < 0.0F) {
         var8 = 0.0F;
      }

      int var9 = MathHelper.clamp_int((int)(var6 * 255.0F), 0, 255);
      int var10 = MathHelper.clamp_int((int)(var7 * 255.0F), 0, 255);
      int var11 = MathHelper.clamp_int((int)(var8 * 255.0F), 0, 255);
      return 0xFF000000 | var9 << 16 | var10 << 8 | var11;
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      int var8 = var3.getValue(POWER);
      if (var8 != 0) {
         double var9 = var2.getX() + 0.5 + (var4.nextFloat() - 0.5) * 0.2;
         double var11 = var2.getY() + 0.0625F;
         double var13 = var2.getZ() + 0.5 + (var4.nextFloat() - 0.5) * 0.2;
         float var15 = var8 / 15.0F;
         float var16 = var15 * 0.6F + 0.4F;
         float var17 = Math.max(0.0F, var15 * var15 * 0.7F - 0.5F);
         float var18 = Math.max(0.0F, var15 * var15 * 0.6F - 0.7F);
         var1.spawnParticle(EnumParticleTypes.REDSTONE, var9, var11, var13, var16, var17, var18);
      }
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.redstone;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(POWER, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(POWER);
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

      EnumAttachPosition(String var3) {
         this.name = var3;
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
