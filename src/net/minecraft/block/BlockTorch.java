package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      public boolean apply(EnumFacing enumFacing) {
         return enumFacing != EnumFacing.DOWN;
      }
   });

   public BlockTorch() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
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

   private boolean canPlaceOn(World worldIn, BlockPos pos) {
      if (World.doesBlockHaveSolidTopSurface(worldIn, pos)) {
         return true;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      return block instanceof BlockFence || block == Blocks.glass || block == Blocks.cobblestone_wall || block == Blocks.stained_glass;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : FACING.getAllowedValues()) {
         if (this.canPlaceAt(worldIn, pos, enumFacing)) {
            return true;
         }
      }

      return false;
   }

   private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
      BlockPos blockPos = pos.offset(facing.getOpposite());
      boolean bl = facing.getAxis().isHorizontal();
      return bl && worldIn.isBlockNormalCube(blockPos, true) || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockPos);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (this.canPlaceAt(worldIn, pos, facing)) {
         return this.getDefaultState().withProperty(FACING, facing);
      }

      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         if (worldIn.isBlockNormalCube(pos.offset(((EnumFacing)object).getOpposite()), true)) {
            return this.getDefaultState().withProperty(FACING, (EnumFacing)object);
         }
      }

      return this.getDefaultState();
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkForDrop(worldIn, pos, state);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.onNeighborChangeInternal(worldIn, pos, state);
   }

   public boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.checkForDrop(worldIn, pos, state)) {
         return true;
      }

      EnumFacing enumFacing = state.getValue(FACING);
      EnumFacing.Axis axis = enumFacing.getAxis();
      EnumFacing enumFacing2 = enumFacing.getOpposite();
      boolean bl = false;
      if (axis.isHorizontal() && !worldIn.isBlockNormalCube(pos.offset(enumFacing2), true)) {
         bl = true;
      } else if (axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(enumFacing2))) {
         bl = true;
      }

      if (bl) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   public boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, state.getValue(FACING))) {
         return true;
      }

      if (worldIn.getBlockState(pos).getBlock() == this) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      return false;
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 startIn, Vec3 end) {
      EnumFacing enumFacing = worldIn.getBlockState(pos).getValue(FACING);
      float f = 0.15F;
      if (enumFacing == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
      } else if (enumFacing == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
      } else if (enumFacing == EnumFacing.SOUTH) {
         this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
      } else if (enumFacing == EnumFacing.NORTH) {
         this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      } else {
         f = 0.1F;
         this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
      }

      return super.collisionRayTrace(worldIn, pos, startIn, end);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      EnumFacing enumFacing = state.getValue(FACING);
      double d = pos.getX() + 0.5;
      double e = pos.getY() + 0.7;
      double d2 = pos.getZ() + 0.5;
      double d3 = 0.22;
      double d4 = 0.27;
      if (enumFacing.getAxis().isHorizontal()) {
         EnumFacing enumFacing2 = enumFacing.getOpposite();
         worldIn.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL, d + d4 * enumFacing2.getFrontOffsetX(), e + d3, d2 + d4 * enumFacing2.getFrontOffsetZ(), 0.0, 0.0, 0.0
         );
         worldIn.spawnParticle(
            EnumParticleTypes.FLAME, d + d4 * enumFacing2.getFrontOffsetX(), e + d3, d2 + d4 * enumFacing2.getFrontOffsetZ(), 0.0, 0.0, 0.0
         );
      } else {
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, e, d2, 0.0, 0.0, 0.0);
         worldIn.spawnParticle(EnumParticleTypes.FLAME, d, e, d2, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState iBlockState = this.getDefaultState();
      switch (meta) {
         case 1:
            iBlockState = iBlockState.withProperty(FACING, EnumFacing.EAST);
            break;
         case 2:
            iBlockState = iBlockState.withProperty(FACING, EnumFacing.WEST);
            break;
         case 3:
            iBlockState = iBlockState.withProperty(FACING, EnumFacing.SOUTH);
            break;
         case 4:
            iBlockState = iBlockState.withProperty(FACING, EnumFacing.NORTH);
            break;
         case 5:
         default:
            iBlockState = iBlockState.withProperty(FACING, EnumFacing.UP);
      }

      return iBlockState;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      switch ((EnumFacing)state.getValue(FACING)) {
         case EAST:
            i |= 1;
            break;
         case WEST:
            i |= 2;
            break;
         case SOUTH:
            i |= 3;
            break;
         case NORTH:
            i |= 4;
            break;
         case DOWN:
         case UP:
         default:
            i |= 5;
      }

      return i;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }
}
