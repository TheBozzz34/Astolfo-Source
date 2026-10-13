package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public static final PropertyBool OPEN = PropertyBool.create("open");
   public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE = PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class);
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyEnum<BlockDoor.EnumDoorHalf> HALF = PropertyEnum.create("half", BlockDoor.EnumDoorHalf.class);
   private static final long e = 63161329861729L;

   public BlockDoor(Material var1) {
      super(var1);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(OPEN, false)
            .withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT)
            .withProperty(POWERED, false)
            .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
      );
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal((this.getUnlocalizedName() + ".name").replaceAll("tile", "item"));
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return isOpen(combineMetadata(var1, var2));
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getSelectedBoundingBox(var1, var2);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getCollisionBoundingBox(var1, var2, var3);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.setBoundBasedOnMeta(combineMetadata(var1, var2));
   }

   private void setBoundBasedOnMeta(int var1) {
      float var5 = 0.1875F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      EnumFacing var6 = getFacing(var1);
      boolean var7 = isOpen(var1);
      boolean var8 = isHingeLeft(var1);
      if (var7) {
         if (var6 == EnumFacing.EAST) {
            if (!var8) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var5);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - var5, 1.0F, 1.0F, 1.0F);
            }
         } else if (var6 == EnumFacing.SOUTH) {
            if (!var8) {
               this.setBlockBounds(1.0F - var5, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, var5, 1.0F, 1.0F);
            }
         } else if (var6 == EnumFacing.WEST) {
            if (!var8) {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - var5, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var5);
            }
         } else if (var6 == EnumFacing.NORTH) {
            if (!var8) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, var5, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(1.0F - var5, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      } else if (var6 == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, var5, 1.0F, 1.0F);
      } else if (var6 == EnumFacing.SOUTH) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var5);
      } else if (var6 == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - var5, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else if (var6 == EnumFacing.NORTH) {
         this.setBlockBounds(0.0F, 0.0F, 1.0F - var5, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (this.blockMaterial == Material.iron) {
         return true;
      }

      BlockPos var12 = var3.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? var2 : var2.down();
      IBlockState var13 = var2.equals(var12) ? var3 : var1.getBlockState(var12);
      if (var13.getBlock() != this) {
         return false;
      }

      var3 = var13.cycleProperty(OPEN);
      var1.setBlockState(var12, var3, 2);
      var1.markBlockRangeForRenderUpdate(var12, var2);
      var1.playAuxSFXAtEntity(var4, var3.getValue(OPEN) ? 1003 : 1006, var2, 0);
      return true;
   }

   public void toggleDoor(World var1, BlockPos var2, boolean var3) {
      IBlockState var7 = var1.getBlockState(var2);
      if (var7.getBlock() == this) {
         BlockPos var8 = var7.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? var2 : var2.down();
         IBlockState var9 = var2 == var8 ? var7 : var1.getBlockState(var8);
         if (var9.getBlock() == this && var9.getValue(OPEN) != var3) {
            var1.setBlockState(var8, var9.withProperty(OPEN, var3), 2);
            var1.markBlockRangeForRenderUpdate(var8, var2);
            var1.playAuxSFXAtEntity((EntityPlayer)null, var3 ? 1003 : 1006, var2, 0);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (var3.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         BlockPos var8 = var2.down();
         IBlockState var9 = var1.getBlockState(var8);
         if (var9.getBlock() != this) {
            var1.setBlockToAir(var2);
         } else if (var4 != this) {
            this.onNeighborBlockChange(var1, var8, var9, var4);
         }
      } else {
         boolean var12 = false;
         BlockPos var13 = var2.up();
         IBlockState var10 = var1.getBlockState(var13);
         if (var10.getBlock() != this) {
            var1.setBlockToAir(var2);
            var12 = true;
         }

         if (!World.doesBlockHaveSolidTopSurface(var1, var2.down())) {
            var1.setBlockToAir(var2);
            var12 = true;
            if (var10.getBlock() == this) {
               var1.setBlockToAir(var13);
            }
         }

         if (var12) {
            if (!var1.isRemote) {
               this.dropBlockAsItem(var1, var2, var3, 0);
            }
         } else {
            boolean var11 = var1.isBlockPowered(var2) || var1.isBlockPowered(var13);
            if ((var11 || var4.canProvidePower()) && var4 != this && var11 != var10.getValue(POWERED)) {
               var1.setBlockState(var13, var10.withProperty(POWERED, var11), 2);
               if (var11 != var3.getValue(OPEN)) {
                  var1.setBlockState(var2, var3.withProperty(OPEN, var11), 2);
                  var1.markBlockRangeForRenderUpdate(var2, var2);
                  var1.playAuxSFXAtEntity((EntityPlayer)null, var11 ? 1003 : 1006, var2, 0);
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return var1.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.getItem();
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.collisionRayTrace(var1, var2, var3, var4);
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return var2.getY() >= 255
         ? false
         : World.doesBlockHaveSolidTopSurface(var1, var2.down()) && super.canPlaceBlockAt(var1, var2) && super.canPlaceBlockAt(var1, var2.up());
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   public static int combineMetadata(IBlockAccess var0, BlockPos var1) {
      IBlockState var5 = var0.getBlockState(var1);
      int var6 = var5.getBlock().getMetaFromState(var5);
      boolean var7 = isTop(var6);
      IBlockState var8 = var0.getBlockState(var1.down());
      int var9 = var8.getBlock().getMetaFromState(var8);
      int var10 = var7 ? var9 : var6;
      IBlockState var11 = var0.getBlockState(var1.up());
      int var12 = var11.getBlock().getMetaFromState(var11);
      int var13 = var7 ? var6 : var12;
      boolean var14 = (var13 & 1) != 0;
      boolean var15 = (var13 & 2) != 0;
      return removeHalfBit(var10) | (var7 ? 8 : 0) | (var14 ? 16 : 0) | (var15 ? 32 : 0);
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return this.getItem();
   }

   private Item getItem() {
      return this == Blocks.iron_door
         ? Items.iron_door
         : (
            this == Blocks.spruce_door
               ? Items.spruce_door
               : (
                  this == Blocks.birch_door
                     ? Items.birch_door
                     : (
                        this == Blocks.jungle_door
                           ? Items.jungle_door
                           : (this == Blocks.acacia_door ? Items.acacia_door : (this == Blocks.dark_oak_door ? Items.dark_oak_door : Items.oak_door))
                     )
               )
         );
   }

   @Override
   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      BlockPos var8 = var2.down();
      if (var4.capabilities.isCreativeMode && var3.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER && var1.getBlockState(var8).getBlock() == this) {
         var1.setBlockToAir(var8);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (var1.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER) {
         IBlockState var7 = var2.getBlockState(var3.up());
         if (var7.getBlock() == this) {
            var1 = var1.withProperty(HINGE, var7.getValue(HINGE)).withProperty(POWERED, var7.getValue(POWERED));
         }
      } else {
         IBlockState var8 = var2.getBlockState(var3.down());
         if (var8.getBlock() == this) {
            var1 = var1.withProperty(FACING, var8.getValue(FACING)).withProperty(OPEN, var8.getValue(OPEN));
         }
      }

      return var1;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return (var1 & 8) > 0
         ? this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER)
            .withProperty(HINGE, (var1 & 1) > 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT)
            .withProperty(POWERED, (var1 & 2) > 0)
         : this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
            .withProperty(FACING, EnumFacing.getHorizontal(var1 & 3).rotateYCCW())
            .withProperty(OPEN, (var1 & 4) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      if (var1.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         var5 |= 8;
         if (var1.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT) {
            var5 |= 1;
         }

         if (var1.getValue(POWERED)) {
            var5 |= 2;
         }
      } else {
         var5 |= var1.getValue(FACING).rotateY().getHorizontalIndex();
         if (var1.getValue(OPEN)) {
            var5 |= 4;
         }
      }

      return var5;
   }

   public static int removeHalfBit(int var0) {
      return var0 & 7;
   }

   public static boolean isOpen(IBlockAccess var0, BlockPos var1) {
      return isOpen(combineMetadata(var0, var1));
   }

   public static EnumFacing getFacing(IBlockAccess var0, BlockPos var1) {
      return getFacing(combineMetadata(var0, var1));
   }

   public static EnumFacing getFacing(int var0) {
      return EnumFacing.getHorizontal(var0 & 3).rotateYCCW();
   }

   public static boolean isOpen(int var0) {
      return (var0 & 4) != 0;
   }

   public static boolean isTop(int var0) {
      return (var0 & 8) != 0;
   }

   public static boolean isHingeLeft(int var0) {
      return (var0 & 16) != 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, HALF, FACING, OPEN, HINGE, POWERED);
   }

   public enum EnumDoorHalf implements IStringSerializable {
      UPPER,
      LOWER;

      private static final long a = 104992981620644L;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public enum EnumHingePosition implements IStringSerializable {
      LEFT,
      RIGHT;

      private static final long a = 100244886919177L;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == LEFT ? "left" : "right";
      }
   }
}
