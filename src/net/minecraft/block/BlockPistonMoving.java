package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public static final PropertyDirection FACING = BlockPistonExtension.FACING;
   public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;
   private static final long e = 24212196979601L;

   public BlockPistonMoving() {
      super(Material.piston);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT)
      );
      this.setHardness(-1.0F);
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return null;
   }

   public static TileEntity newTileEntity(IBlockState var0, EnumFacing var1, boolean var2, boolean var3) {
      return new TileEntityPiston(var0, var1, var2, var3);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var7 = var1.getTileEntity(var2);
      if (var7 instanceof TileEntityPiston) {
         ((TileEntityPiston)var7).clearPistonTileEntity();
      } else {
         super.breakBlock(var1, var2, var3);
      }
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return false;
   }

   @Override
   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3) {
      return false;
   }

   @Override
   public void onBlockDestroyedByPlayer(World var1, BlockPos var2, IBlockState var3) {
      BlockPos var7 = var2.offset(var3.getValue(FACING).getOpposite());
      IBlockState var8 = var1.getBlockState(var7);
      if (var8.getBlock() instanceof BlockPistonBase && var8.getValue(BlockPistonBase.EXTENDED)) {
         var1.setBlockToAir(var7);
      }
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
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var1.isRemote && var1.getTileEntity(var2) == null) {
         var1.setBlockToAir(var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return null;
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.isRemote) {
         TileEntityPiston var9 = this.getTileEntity(var1, var2);
         if (var9 != null) {
            IBlockState var10 = var9.getPistonState();
            var10.getBlock().dropBlockAsItem(var1, var2, var10, 0);
         }
      }
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      return null;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.isRemote) {
         var1.getTileEntity(var2);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      TileEntityPiston var7 = this.getTileEntity(var1, var2);
      if (var7 == null) {
         return null;
      }

      float var8 = var7.getProgress(0.0F);
      if (var7.isExtending()) {
         var8 = 1.0F - var8;
      }

      return this.getBoundingBox(var1, var2, var7.getPistonState(), var8, var7.getFacing());
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      TileEntityPiston var6 = this.getTileEntity(var1, var2);
      if (var6 != null) {
         IBlockState var7 = var6.getPistonState();
         Block var8 = var7.getBlock();
         if (var8 == this || var8.getMaterial() == Material.air) {
            return;
         }

         float var9 = var6.getProgress(0.0F);
         if (var6.isExtending()) {
            var9 = 1.0F - var9;
         }

         var8.setBlockBoundsBasedOnState(var1, var2);
         if (var8 == Blocks.piston || var8 == Blocks.sticky_piston) {
            var9 = 0.0F;
         }

         EnumFacing var10 = var6.getFacing();
         this.minX = var8.getBlockBoundsMinX() - var10.getFrontOffsetX() * var9;
         this.minY = var8.getBlockBoundsMinY() - var10.getFrontOffsetY() * var9;
         this.minZ = var8.getBlockBoundsMinZ() - var10.getFrontOffsetZ() * var9;
         this.maxX = var8.getBlockBoundsMaxX() - var10.getFrontOffsetX() * var9;
         this.maxY = var8.getBlockBoundsMaxY() - var10.getFrontOffsetY() * var9;
         this.maxZ = var8.getBlockBoundsMaxZ() - var10.getFrontOffsetZ() * var9;
      }
   }

   public AxisAlignedBB getBoundingBox(World var1, BlockPos var2, IBlockState var3, float var4, EnumFacing var5) {
      if (var3.getBlock() != this && var3.getBlock().getMaterial() != Material.air) {
         AxisAlignedBB var9 = var3.getBlock().getCollisionBoundingBox(var1, var2, var3);
         if (var9 == null) {
            return null;
         }

         double var10 = var9.minX;
         double var12 = var9.minY;
         double var14 = var9.minZ;
         double var16 = var9.maxX;
         double var18 = var9.maxY;
         double var20 = var9.maxZ;
         if (var5.getFrontOffsetX() < 0) {
            var10 -= var5.getFrontOffsetX() * var4;
         } else {
            var16 -= var5.getFrontOffsetX() * var4;
         }

         if (var5.getFrontOffsetY() < 0) {
            var12 -= var5.getFrontOffsetY() * var4;
         } else {
            var18 -= var5.getFrontOffsetY() * var4;
         }

         if (var5.getFrontOffsetZ() < 0) {
            var14 -= var5.getFrontOffsetZ() * var4;
         } else {
            var20 -= var5.getFrontOffsetZ() * var4;
         }

         return new AxisAlignedBB(var10, var12, var14, var16, var18, var20);
      } else {
         return null;
      }
   }

   private TileEntityPiston getTileEntity(IBlockAccess var1, BlockPos var2) {
      TileEntity var6 = var1.getTileEntity(var2);
      return var6 instanceof TileEntityPiston ? (TileEntityPiston)var6 : null;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return null;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(FACING, BlockPistonExtension.getFacing(var1))
         .withProperty(TYPE, (var1 & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getIndex();
      if (var1.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, TYPE);
   }
}
