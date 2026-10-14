package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {
   private boolean ignoreSimilarity;

   public BlockBreakable(Material materialIn, boolean ignoreSimilarityIn) {
      this(materialIn, ignoreSimilarityIn, materialIn.getMaterialMapColor());
   }

   public BlockBreakable(Material material, boolean bl, MapColor mapColor) {
      super(material, mapColor);
      this.ignoreSimilarity = bl;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      Block block = iBlockState.getBlock();
      if (this == Blocks.glass || this == Blocks.stained_glass) {
         if (worldIn.getBlockState(pos.offset(side.getOpposite())) != iBlockState) {
            return true;
         }

         if (block == this) {
            return false;
         }
      }

      return !this.ignoreSimilarity && block == this ? false : super.shouldSideBeRendered(worldIn, pos, side);
   }
}
