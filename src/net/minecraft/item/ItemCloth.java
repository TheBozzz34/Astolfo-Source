package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemCloth extends ItemBlock {
   public ItemCloth(Block var1) {
      super(var1);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public int getMetadata(int var1) {
      return var1;
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      return super.getUnlocalizedName() + "." + EnumDyeColor.byMetadata(var1.getMetadata()).getUnlocalizedName();
   }
}
