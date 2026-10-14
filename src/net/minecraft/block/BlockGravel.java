package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockGravel extends BlockFalling {
   private static final long e = 68009587588647L;
   private static final long i = -8326353660225257462L;

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      if (fortune > 3) {
         fortune = 3;
      }

      return rand.nextInt((int)i - fortune * 3) == 0 ? Items.flint : Item.getItemFromBlock(this);
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return MapColor.stoneColor;
   }
}
