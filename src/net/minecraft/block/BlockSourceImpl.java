package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockSourceImpl implements IBlockSource {
   private final World worldObj;
   private final BlockPos pos;

   public BlockSourceImpl(World var1, BlockPos var2) {
      this.worldObj = var1;
      this.pos = var2;
   }

   @Override
   public World getWorld() {
      return this.worldObj;
   }

   @Override
   public double getX() {
      return this.pos.getX() + 0.5;
   }

   @Override
   public double getY() {
      return this.pos.getY() + 0.5;
   }

   @Override
   public double getZ() {
      return this.pos.getZ() + 0.5;
   }

   @Override
   public BlockPos getBlockPos() {
      return this.pos;
   }

   @Override
   public int getBlockMetadata() {
      IBlockState var1 = this.worldObj.getBlockState(this.pos);
      return var1.getBlock().getMetaFromState(var1);
   }

   @Override
   public <T extends TileEntity> T getBlockTileEntity() {
      return (T)this.worldObj.getTileEntity(this.pos);
   }
}
