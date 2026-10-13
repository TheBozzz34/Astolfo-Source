package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentWaterWalker extends Enchantment {
   private static final long m = 45926501747650L;

   public EnchantmentWaterWalker(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_FEET);
      this.setName("waterWalker");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return var1 * 10;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return this.getMinEnchantability(var1) + 15;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}
