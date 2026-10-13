package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedPowered extends Block {
   private static final long e = 81423072695778L;
   private static final long i = 1552916705146044431L;

   public BlockCompressedPowered(Material var1, MapColor var2) {
      super(var1, var2);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return (int)i;
   }
}
