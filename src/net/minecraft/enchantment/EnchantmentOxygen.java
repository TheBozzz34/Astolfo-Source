package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentOxygen extends Enchantment {
   private static final long m = 96090042451538L;

   public EnchantmentOxygen(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("oxygen");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 10 * var1;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return this.getMinEnchantability(var1) + 30;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
