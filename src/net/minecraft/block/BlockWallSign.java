package net.minecraft.block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWallSign extends BlockSign {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   private static final long i = 97930254345797L;

   public BlockWallSign() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      EnumFacing enumFacing = worldIn.getBlockState(pos).getValue(FACING);
      float f = 0.28125F;
      float g = 0.78125F;
      float h = 0.0F;
      float f2 = 1.0F;
      float f3 = 0.125F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      switch (enumFacing) {
         case NORTH:
            this.setBlockBounds(h, f, 1.0F - f3, f2, g, 1.0F);
            break;
         case SOUTH:
            this.setBlockBounds(h, f, 0.0F, f2, g, f3);
            break;
         case WEST:
            this.setBlockBounds(1.0F - f3, f, h, 1.0F, g, f2);
            break;
         case EAST:
            this.setBlockBounds(0.0F, f, h, f3, g, f2);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      EnumFacing enumFacing = state.getValue(FACING);
      if (!worldIn.getBlockState(pos.offset(enumFacing.getOpposite())).getBlock().getMaterial().isSolid()) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing enumFacing = EnumFacing.getFront(meta);
      if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
         enumFacing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, enumFacing);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getIndex();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }
}
