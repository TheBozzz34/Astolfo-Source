package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockColored extends Block {
   public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
   private static final long e = 28546238582026L;

   public BlockColored(Material var1) {
      super(var1);
      this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(COLOR).getMetadata();
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (EnumDyeColor var10 : EnumDyeColor.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return var1.getValue(COLOR).getMapColor();
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(COLOR).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, COLOR);
   }
}
