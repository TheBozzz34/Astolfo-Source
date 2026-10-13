package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentUntouching extends Enchantment {
   private static final long m = 45805392897086L;

   public EnchantmentUntouching(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.DIGGER);
      this.setName("untouching");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 15;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return super.getMinEnchantability(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }

   @Override
   public boolean canApplyTogether(Enchantment var1) {
      return super.canApplyTogether(var1) && var1.effectId != fortune.effectId;
   }

   @Override
   public boolean canApply(ItemStack var1) {
      return var1.getItem() == Items.shears ? true : super.canApply(var1);
   }
}
