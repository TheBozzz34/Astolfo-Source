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
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return null;
   }

   public static TileEntity newTileEntity(IBlockState state, EnumFacing facing, boolean extending, boolean renderHead) {
      return new TileEntityPiston(state, facing, extending, renderHead);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityPiston) {
         ((TileEntityPiston)tileEntity).clearPistonTileEntity();
      } else {
         super.breakBlock(worldIn, pos, state);
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return false;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return false;
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      BlockPos blockPos = pos.offset(state.getValue(FACING).getOpposite());
      IBlockState iBlockState = worldIn.getBlockState(blockPos);
      if (iBlockState.getBlock() instanceof BlockPistonBase && iBlockState.getValue(BlockPistonBase.EXTENDED)) {
         worldIn.setBlockToAir(blockPos);
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
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         worldIn.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         TileEntityPiston tileEntityPiston = this.getTileEntity(worldIn, pos);
         if (tileEntityPiston != null) {
            IBlockState iBlockState = tileEntityPiston.getPistonState();
            iBlockState.getBlock().dropBlockAsItem(worldIn, pos, iBlockState, 0);
         }
      }
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 startIn, Vec3 end) {
      return null;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         worldIn.getTileEntity(pos);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      TileEntityPiston tileEntityPiston = this.getTileEntity(worldIn, pos);
      if (tileEntityPiston == null) {
         return null;
      }

      float f = tileEntityPiston.getProgress(0.0F);
      if (tileEntityPiston.isExtending()) {
         f = 1.0F - f;
      }

      return this.getBoundingBox(worldIn, pos, tileEntityPiston.getPistonState(), f, tileEntityPiston.getFacing());
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      TileEntityPiston tileEntityPiston = this.getTileEntity(worldIn, pos);
      if (tileEntityPiston != null) {
         IBlockState iBlockState = tileEntityPiston.getPistonState();
         Block block = iBlockState.getBlock();
         if (block == this || block.getMaterial() == Material.air) {
            return;
         }

         float f = tileEntityPiston.getProgress(0.0F);
         if (tileEntityPiston.isExtending()) {
            f = 1.0F - f;
         }

         block.setBlockBoundsBasedOnState(worldIn, pos);
         if (block == Blocks.piston || block == Blocks.sticky_piston) {
            f = 0.0F;
         }

         EnumFacing enumFacing = tileEntityPiston.getFacing();
         this.minX = block.getBlockBoundsMinX() - enumFacing.getFrontOffsetX() * f;
         this.minY = block.getBlockBoundsMinY() - enumFacing.getFrontOffsetY() * f;
         this.minZ = block.getBlockBoundsMinZ() - enumFacing.getFrontOffsetZ() * f;
         this.maxX = block.getBlockBoundsMaxX() - enumFacing.getFrontOffsetX() * f;
         this.maxY = block.getBlockBoundsMaxY() - enumFacing.getFrontOffsetY() * f;
         this.maxZ = block.getBlockBoundsMaxZ() - enumFacing.getFrontOffsetZ() * f;
      }
   }

   public AxisAlignedBB getBoundingBox(World worldIn, BlockPos pos, IBlockState extendingBlock, float progress, EnumFacing direction) {
      if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air) {
         AxisAlignedBB axisAlignedBB = extendingBlock.getBlock().getCollisionBoundingBox(worldIn, pos, extendingBlock);
         if (axisAlignedBB == null) {
            return null;
         }

         double d = axisAlignedBB.minX;
         double e = axisAlignedBB.minY;
         double d2 = axisAlignedBB.minZ;
         double d3 = axisAlignedBB.maxX;
         double d4 = axisAlignedBB.maxY;
         double d5 = axisAlignedBB.maxZ;
         if (direction.getFrontOffsetX() < 0) {
            d -= direction.getFrontOffsetX() * progress;
         } else {
            d3 -= direction.getFrontOffsetX() * progress;
         }

         if (direction.getFrontOffsetY() < 0) {
            e -= direction.getFrontOffsetY() * progress;
         } else {
            d4 -= direction.getFrontOffsetY() * progress;
         }

         if (direction.getFrontOffsetZ() < 0) {
            d2 -= direction.getFrontOffsetZ() * progress;
         } else {
            d5 -= direction.getFrontOffsetZ() * progress;
         }

         return new AxisAlignedBB(d, e, d2, d3, d4, d5);
      } else {
         return null;
      }
   }

   private TileEntityPiston getTileEntity(IBlockAccess worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntityPiston ? (TileEntityPiston)tileEntity : null;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, BlockPistonExtension.getFacing(meta))
         .withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(FACING).getIndex();
      if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, TYPE);
   }
}
