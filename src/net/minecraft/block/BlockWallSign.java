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
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      EnumFacing var6 = var1.getBlockState(var2).getValue(FACING);
      float var7 = 0.28125F;
      float var8 = 0.78125F;
      float var9 = 0.0F;
      float var10 = 1.0F;
      float var11 = 0.125F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      switch (var6) {
         case NORTH:
            this.setBlockBounds(var9, var7, 1.0F - var11, var10, var8, 1.0F);
            break;
         case SOUTH:
            this.setBlockBounds(var9, var7, 0.0F, var10, var8, var11);
            break;
         case WEST:
            this.setBlockBounds(1.0F - var11, var7, var9, 1.0F, var8, var10);
            break;
         case EAST:
            this.setBlockBounds(0.0F, var7, var9, var11, var8, var10);
      }
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var8 = var3.getValue(FACING);
      if (!var1.getBlockState(var2.offset(var8.getOpposite())).getBlock().getMaterial().isSolid()) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }

      super.onNeighborBlockChange(var1, var2, var3, var4);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      EnumFacing var5 = EnumFacing.getFront(var1);
      if (var5.getAxis() == EnumFacing.Axis.Y) {
         var5 = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, var5);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(FACING).getIndex();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }
}
