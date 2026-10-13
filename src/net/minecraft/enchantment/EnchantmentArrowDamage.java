package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowDamage extends Enchantment {
   private static final long m = 67593200183454L;

   public EnchantmentArrowDamage(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.BOW);
      this.setName("arrowDamage");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 1 + (var1 - 1) * 10;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return this.getMinEnchantability(var1) + 15;
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }
}
