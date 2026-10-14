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
   public BlockSeaLantern(Material materialIn) {
      super(materialIn);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public int quantityDropped(Random randomIn) {
      return 2 + randomIn.nextInt(2);
   }

   @Override
   public int quantityDroppedWithBonus(int fortune, Random randomIn) {
      return MathHelper.clamp_int(this.quantityDropped(randomIn) + randomIn.nextInt(fortune + 1), 1, 5);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.prismarine_crystals;
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return MapColor.quartzColor;
   }

   @Override
   public boolean canSilkHarvest() {
      return true;
   }
}
