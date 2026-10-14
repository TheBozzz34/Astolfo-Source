package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDurability extends Enchantment {
   private static final long m = 117443754160032L;

   public EnchantmentDurability(int enchID, ResourceLocation enchName, int enchWeight) {
      super(enchID, enchName, enchWeight, EnumEnchantmentType.BREAKABLE);
      this.setName("durability");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + (enchantmentLevel - 1) * 8;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.isItemStackDamageable() ? true : super.canApply(stack);
   }

   public static boolean negateDamage(ItemStack itemStack, int i, Random random2) {
      return itemStack.getItem() instanceof ItemArmor && random2.nextFloat() < 0.6F ? false : random2.nextInt(i + 1) > 0;
   }
}
