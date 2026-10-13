package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowKnockback extends Enchantment {
   private static final long m = 96532536934101L;

   public EnchantmentArrowKnockback(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.BOW);
      this.setName("arrowKnockback");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 12 + (var1 - 1) * 20;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return this.getMinEnchantability(var1) + 25;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
