package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDurability extends Enchantment {
   private static final long m = 117443754160032L;

   public EnchantmentDurability(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.BREAKABLE);
      this.setName("durability");
   }

   @Override
   public int getMinEnchantability(int var1) {
      return 5 + (var1 - 1) * 8;
   }

   @Override
   public int getMaxEnchantability(int var1) {
      return super.getMinEnchantability(var1) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   @Override
   public boolean canApply(ItemStack var1) {
      return var1.isItemStackDamageable() ? true : super.canApply(var1);
   }

   public static boolean negateDamage(ItemStack var0, int var1, Random var2) {
      return var0.getItem() instanceof ItemArmor && var2.nextFloat() < 0.6F ? false : var2.nextInt(var1 + 1) > 0;
   }
}
