package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public static final PropertyBool OPEN = PropertyBool.create("open");
   public static final PropertyEnum<BlockTrapDoor.DoorHalf> HALF = PropertyEnum.create("half", BlockTrapDoor.DoorHalf.class);
   private static final long i = 69940603479000L;

   public BlockTrapDoor(Material var1) {
      super(var1);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, false).withProperty(HALF, BlockTrapDoor.DoorHalf.BOTTOM)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.setCreativeTab(CreativeTabs.tabRedstone);
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
      return !var1.getBlockState(var2).getValue(OPEN);
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
      this.setBounds(var1.getBlockState(var2));
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
   }

   public void setBounds(IBlockState var1) {
      if (var1.getBlock() == this) {
         boolean var5 = var1.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP;
         Boolean var6 = var1.getValue(OPEN);
         EnumFacing var7 = var1.getValue(FACING);
         if (var5) {
            this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
         } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
         }

         if (var6) {
            if (var7 == EnumFacing.NORTH) {
               this.setBlockBounds(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
            }

            if (var7 == EnumFacing.SOUTH) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
            }

            if (var7 == EnumFacing.WEST) {
               this.setBlockBounds(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if (var7 == EnumFacing.EAST) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
            }
         }
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (this.blockMaterial == Material.iron) {
         return true;
      }

      var3 = var3.cycleProperty(OPEN);
      var1.setBlockState(var2, var3, 2);
      var1.playAuxSFXAtEntity(var4, var3.getValue(OPEN) ? 1003 : 1006, var2, 0);
      return true;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.isRemote) {
         BlockPos var8 = var2.offset(var3.getValue(FACING).getOpposite());
         if (!isValidSupportBlock(var1.getBlockState(var8).getBlock())) {
            var1.setBlockToAir(var2);
            this.dropBlockAsItem(var1, var2, var3, 0);
         } else {
            boolean var9 = var1.isBlockPowered(var2);
            if (var9 || var4.canProvidePower()) {
               boolean var10 = var3.getValue(OPEN);
               if (var10 != var9) {
                  var1.setBlockState(var2, var3.withProperty(OPEN, var9), 2);
                  var1.playAuxSFXAtEntity((EntityPlayer)null, var9 ? 1003 : 1006, var2, 0);
               }
            }
         }
      }
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.collisionRayTrace(var1, var2, var3, var4);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var12 = this.getDefaultState();
      if (var3.getAxis().isHorizontal()) {
         var12 = var12.withProperty(FACING, var3).withProperty(OPEN, false);
         var12 = var12.withProperty(HALF, var5 > 0.5F ? BlockTrapDoor.DoorHalf.TOP : BlockTrapDoor.DoorHalf.BOTTOM);
      }

      return var12;
   }

   @Override
   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3) {
      return !var3.getAxis().isVertical() && isValidSupportBlock(var1.getBlockState(var2.offset(var3.getOpposite())).getBlock());
   }

   public static EnumFacing getFacing(int var0) {
      switch (var0 & 3) {
         case 0:
            return EnumFacing.NORTH;
         case 1:
            return EnumFacing.SOUTH;
         case 2:
            return EnumFacing.WEST;
         case 3:
         default:
            return EnumFacing.EAST;
      }
   }

   public static int getMetaForFacing(EnumFacing var0) {
      switch (var0) {
         case NORTH:
            return 0;
         case SOUTH:
            return 1;
         case WEST:
            return 2;
         case EAST:
         default:
            return 3;
      }
   }

   private static boolean isValidSupportBlock(Block var0) {
      return var0.blockMaterial.isOpaque() && var0.isFullCube() || var0 == Blocks.glowstone || var0 instanceof BlockSlab || var0 instanceof BlockStairs;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(FACING, getFacing(var1))
         .withProperty(OPEN, (var1 & 4) != 0)
         .withProperty(HALF, (var1 & 8) == 0 ? BlockTrapDoor.DoorHalf.BOTTOM : BlockTrapDoor.DoorHalf.TOP);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= getMetaForFacing(var1.getValue(FACING));
      if (var1.getValue(OPEN)) {
         var5 |= 4;
      }

      if (var1.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, OPEN, HALF);
   }

   public enum DoorHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      DoorHalf(String var3) {
         this.name = var3;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
