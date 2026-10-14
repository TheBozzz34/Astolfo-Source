package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentKnockback extends Enchantment {
   private static final long m = 8679223616938L;

   public EnchantmentKnockback(int i, ResourceLocation resourceLocation, int j) {
      super(i, resourceLocation, j, EnumEnchantmentType.WEAPON);
      this.setName("knockback");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + 20 * (enchantmentLevel - 1);
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
