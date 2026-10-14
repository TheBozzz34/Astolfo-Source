package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWorker extends Enchantment {
   private static final long m = 137822471169200L;
   private static final long p = -6637308731136671704L;

   public EnchantmentWaterWorker(int i, ResourceLocation resourceLocation, int j) {
      super(i, resourceLocation, j, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("waterWorker");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 1;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + (int)p;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }
}
