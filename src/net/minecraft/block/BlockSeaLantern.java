package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockSeaLantern extends Block {
   public BlockSeaLantern(Material var1) {
      super(var1);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public int quantityDropped(Random var1) {
      return 2 + var1.nextInt(2);
   }

   @Override
   public int quantityDroppedWithBonus(int var1, Random var2) {
      return MathHelper.clamp_int(this.quantityDropped(var2) + var2.nextInt(var1 + 1), 1, 5);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.prismarine_crystals;
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return MapColor.quartzColor;
   }

   @Override
   public boolean canSilkHarvest() {
      return true;
   }
}
