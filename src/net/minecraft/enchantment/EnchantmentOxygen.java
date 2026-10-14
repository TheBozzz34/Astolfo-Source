package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentOxygen extends Enchantment {
   private static final long m = 96090042451538L;

   public EnchantmentOxygen(int enchID, ResourceLocation resourceLocation, int i) {
      super(enchID, resourceLocation, i, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("oxygen");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 * enchantmentLevel;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 30;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
