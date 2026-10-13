package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFireAspect extends Enchantment {
   private static final long m = 43545394507364L;

   public EnchantmentFireAspect(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.WEAPON);
      this.setName("fire");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 10 + 20 * (var1 - 1);
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return super.getMinEnchantability(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}
