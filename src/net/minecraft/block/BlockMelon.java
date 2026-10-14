package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockMelon extends Block {
   public BlockMelon() {
      super(Material.gourd, MapColor.limeColor);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.melon;
   }

   @Override
   public int quantityDropped(Random randomIn) {
      return 3 + randomIn.nextInt(5);
   }

   @Override
   public int quantityDroppedWithBonus(int fortune, Random randomIn) {
      return Math.min(9, this.quantityDropped(randomIn) + randomIn.nextInt(1 + fortune));
   }
}
