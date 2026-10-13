package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentFishingSpeed extends Enchantment {
   private static final long m = 102996643276754L;

   public EnchantmentFishingSpeed(int var1, ResourceLocation var2, int var3, EnumEnchantmentType var4) {
      super(var1, var2, var3, var4);
      this.setName("fishingSpeed");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 15 + (var1 - 1) * 9;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return super.getMinEnchantability(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
