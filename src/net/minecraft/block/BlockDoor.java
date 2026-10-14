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

   public BlockDoor(Material materialIn) {
      super(materialIn);
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
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBoundBasedOnMeta(combineMetadata(worldIn, pos));
   }

   private void setBoundBasedOnMeta(int combinedMeta) {
      float f = 0.1875F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      EnumFacing enumFacing = getFacing(combinedMeta);
      boolean bl = isOpen(combinedMeta);
      boolean bl2 = isHingeLeft(combinedMeta);
      if (bl) {
         if (enumFacing == EnumFacing.EAST) {
            if (!bl2) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
         } else if (enumFacing == EnumFacing.SOUTH) {
            if (!bl2) {
               this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
         } else if (enumFacing == EnumFacing.WEST) {
            if (!bl2) {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
         } else if (enumFacing == EnumFacing.NORTH) {
            if (!bl2) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      } else if (enumFacing == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
      } else if (enumFacing == EnumFacing.SOUTH) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
      } else if (enumFacing == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else if (enumFacing == EnumFacing.NORTH) {
         this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (this.blockMaterial == Material.iron) {
         return true;
      }

      BlockPos blockPos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
      IBlockState iBlockState = pos.equals(blockPos) ? state : worldIn.getBlockState(blockPos);
      if (iBlockState.getBlock() != this) {
         return false;
      }

      state = iBlockState.cycleProperty(OPEN);
      worldIn.setBlockState(blockPos, state, 2);
      worldIn.markBlockRangeForRenderUpdate(blockPos, pos);
      worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN) ? 1003 : 1006, pos, 0);
      return true;
   }

   public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      if (iBlockState.getBlock() == this) {
         BlockPos blockPos = iBlockState.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
         IBlockState iBlockState2 = pos == blockPos ? iBlockState : worldIn.getBlockState(blockPos);
         if (iBlockState2.getBlock() == this && iBlockState2.getValue(OPEN) != open) {
            worldIn.setBlockState(blockPos, iBlockState2.withProperty(OPEN, open), 2);
            worldIn.markBlockRangeForRenderUpdate(blockPos, pos);
            worldIn.playAuxSFXAtEntity((EntityPlayer)null, open ? 1003 : 1006, pos, 0);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         BlockPos blockPos = pos.down();
         IBlockState iBlockState = worldIn.getBlockState(blockPos);
         if (iBlockState.getBlock() != this) {
            worldIn.setBlockToAir(pos);
         } else if (neighborBlock != this) {
            this.onNeighborBlockChange(worldIn, blockPos, iBlockState, neighborBlock);
         }
      } else {
         boolean bl = false;
         BlockPos blockPos2 = pos.up();
         IBlockState iBlockState2 = worldIn.getBlockState(blockPos2);
         if (iBlockState2.getBlock() != this) {
            worldIn.setBlockToAir(pos);
            bl = true;
         }

         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            worldIn.setBlockToAir(pos);
            bl = true;
            if (iBlockState2.getBlock() == this) {
               worldIn.setBlockToAir(blockPos2);
            }
         }

         if (bl) {
            if (!worldIn.isRemote) {
               this.dropBlockAsItem(worldIn, pos, state, 0);
            }
         } else {
            boolean bl2 = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockPos2);
            if ((bl2 || neighborBlock.canProvidePower()) && neighborBlock != this && bl2 != iBlockState2.getValue(POWERED)) {
               worldIn.setBlockState(blockPos2, iBlockState2.withProperty(POWERED, bl2), 2);
               if (bl2 != state.getValue(OPEN)) {
                  worldIn.setBlockState(pos, state.withProperty(OPEN, bl2), 2);
                  worldIn.markBlockRangeForRenderUpdate(pos, pos);
                  worldIn.playAuxSFXAtEntity((EntityPlayer)null, bl2 ? 1003 : 1006, pos, 0);
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.getItem();
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 startIn, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.collisionRayTrace(worldIn, pos, startIn, end);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return pos.getY() >= 255
         ? false
         : World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   public static int combineMetadata(IBlockAccess worldIn, BlockPos pos) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      int i = iBlockState.getBlock().getMetaFromState(iBlockState);
      boolean bl = isTop(i);
      IBlockState iBlockState2 = worldIn.getBlockState(pos.down());
      int j = iBlockState2.getBlock().getMetaFromState(iBlockState2);
      int k = bl ? j : i;
      IBlockState iBlockState3 = worldIn.getBlockState(pos.up());
      int l = iBlockState3.getBlock().getMetaFromState(iBlockState3);
      int m = bl ? i : l;
      boolean bl2 = (m & 1) != 0;
      boolean bl3 = (m & 2) != 0;
      return removeHalfBit(k) | (bl ? 8 : 0) | (bl2 ? 16 : 0) | (bl3 ? 32 : 0);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
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
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      BlockPos blockPos = pos.down();
      if (player.capabilities.isCreativeMode && state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER && worldIn.getBlockState(blockPos).getBlock() == this) {
         worldIn.setBlockToAir(blockPos);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER) {
         IBlockState iBlockState = worldIn.getBlockState(pos.up());
         if (iBlockState.getBlock() == this) {
            state = state.withProperty(HINGE, iBlockState.getValue(HINGE)).withProperty(POWERED, iBlockState.getValue(POWERED));
         }
      } else {
         IBlockState iBlockState2 = worldIn.getBlockState(pos.down());
         if (iBlockState2.getBlock() == this) {
            state = state.withProperty(FACING, iBlockState2.getValue(FACING)).withProperty(OPEN, iBlockState2.getValue(OPEN));
         }
      }

      return state;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return (meta & 8) > 0
         ? this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER)
            .withProperty(HINGE, (meta & 1) > 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT)
            .withProperty(POWERED, (meta & 2) > 0)
         : this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
            .withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW())
            .withProperty(OPEN, (meta & 4) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         i |= 8;
         if (state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT) {
            i |= 1;
         }

         if (state.getValue(POWERED)) {
            i |= 2;
         }
      } else {
         i |= state.getValue(FACING).rotateY().getHorizontalIndex();
         if (state.getValue(OPEN)) {
            i |= 4;
         }
      }

      return i;
   }

   public static int removeHalfBit(int meta) {
      return meta & 7;
   }

   public static boolean isOpen(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   public static EnumFacing getFacing(IBlockAccess worldIn, BlockPos pos) {
      return getFacing(combineMetadata(worldIn, pos));
   }

   public static EnumFacing getFacing(int combinedMeta) {
      return EnumFacing.getHorizontal(combinedMeta & 3).rotateYCCW();
   }

   public static boolean isOpen(int combinedMeta) {
      return (combinedMeta & 4) != 0;
   }

   public static boolean isTop(int meta) {
      return (meta & 8) != 0;
   }

   public static boolean isHingeLeft(int combinedMeta) {
      return (combinedMeta & 16) != 0;
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
