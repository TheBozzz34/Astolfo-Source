package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDigging extends Enchantment {
   private static final long m = 128264511979146L;

   public EnchantmentDigging(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.DIGGER);
      this.setName("digging");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 1 + 10 * (var1 - 1);
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return super.getMinEnchantability(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public boolean canApply(ItemStack var1) {
      return var1.getItem() == Items.shears ? true : super.canApply(var1);
   }
}
