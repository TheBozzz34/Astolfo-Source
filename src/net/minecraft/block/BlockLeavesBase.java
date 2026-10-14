package net.minecraft.block;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockLeavesBase extends Block {
   public boolean fancyGraphics;
   private static Map mapOriginalOpacity = new IdentityHashMap();

   public BlockLeavesBase(Material materialIn, boolean fancyGraphics) {
      super(materialIn);
      this.fancyGraphics = fancyGraphics;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return Config.isCullFacesLeaves() && worldIn.getBlockState(pos).getBlock() == this ? false : super.shouldSideBeRendered(worldIn, pos, side);
   }

   public static void setLightOpacity(Block block, int i) {
      if (!mapOriginalOpacity.containsKey(block)) {
         mapOriginalOpacity.put(block, block.getLightOpacity());
      }

      block.setLightOpacity(i);
   }

   public static void restoreLightOpacity(Block block) {
      if (mapOriginalOpacity.containsKey(block)) {
         int i = (Integer)mapOriginalOpacity.get(block);
         setLightOpacity(block, i);
      }
   }
}
