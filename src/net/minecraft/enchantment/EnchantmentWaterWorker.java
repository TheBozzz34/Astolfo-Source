package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWorker extends Enchantment {
   private static final long m = 137822471169200L;
   private static final long p = -6637308731136671704L;

   public EnchantmentWaterWorker(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("waterWorker");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 1;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return this.getMinEnchantability(var1) + (int)p;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }
}
