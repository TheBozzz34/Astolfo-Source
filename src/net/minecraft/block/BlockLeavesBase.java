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
   private static final long bb = 97820776847832L;

   public BlockLeavesBase(Material var1, boolean var2) {
      super(var1);
      this.fancyGraphics = var2;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return Config.isCullFacesLeaves() && var1.getBlockState(var2).getBlock() == this ? false : super.shouldSideBeRendered(var1, var2, var3);
   }

   public static void setLightOpacity(Block var0, int var1) {
      if (!mapOriginalOpacity.containsKey(var0)) {
         mapOriginalOpacity.put(var0, var0.getLightOpacity());
      }

      var0.setLightOpacity(var1);
   }

   public static void restoreLightOpacity(Block var0) {
      if (mapOriginalOpacity.containsKey(var0)) {
         int var4 = (Integer)mapOriginalOpacity.get(var0);
         setLightOpacity(var0, var4);
      }
   }
}
