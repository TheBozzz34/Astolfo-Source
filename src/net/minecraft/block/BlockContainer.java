package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   private static final long bb = 61819320743519L;

   public BlockContainer(Material var1) {
      this(var1, var1.getMaterialMapColor());
   }

   public BlockContainer(Material var1, MapColor var2) {
      super(var1, var2);
      this.isBlockContainer = true;
   }

   public boolean isInvalidNeighbor(World var1, BlockPos var2, EnumFacing var3) {
      return var1.getBlockState(var2.offset(var3)).getBlock().getMaterial() == Material.cactus;
   }

   public boolean hasInvalidNeighbor(World var1, BlockPos var2) {
      return this.isInvalidNeighbor(var1, var2, EnumFacing.NORTH)
         || this.isInvalidNeighbor(var1, var2, EnumFacing.SOUTH)
         || this.isInvalidNeighbor(var1, var2, EnumFacing.WEST)
         || this.isInvalidNeighbor(var1, var2, EnumFacing.EAST);
   }

   @Override
   public int getRenderType() {
      return -1;
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      super.breakBlock(var1, var2, var3);
      var1.removeTileEntity(var2);
   }

   @Override
   public boolean onBlockEventReceived(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      super.onBlockEventReceived(var1, var2, var3, var4, var5);
      TileEntity var9 = var1.getTileEntity(var2);
      return var9 == null ? false : var9.receiveClientEvent(var4, var5);
   }
}
