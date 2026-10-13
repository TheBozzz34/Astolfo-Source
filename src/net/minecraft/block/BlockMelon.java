package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockMelon extends Block {
   private static final long e = 33447890552446L;
   private static final long i = -8005927741071294455L;

   public BlockMelon() {
      super(Material.gourd, MapColor.limeColor);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.melon;
   }

   @Override
   public int quantityDropped(Random var1) {
      return 3 + var1.nextInt(5);
   }

   @Override
   public int quantityDroppedWithBonus(int var1, Random var2) {
      return Math.min((int)i, this.quantityDropped(var2) + var2.nextInt(1 + var1));
   }
}
