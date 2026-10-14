package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom.Item {
   private final ItemStack returnStack;
   private float maxDamagePercent;
   private boolean enchantable;

   public WeightedRandomFishable(ItemStack returnStackIn, int itemWeightIn) {
      super(itemWeightIn);
      this.returnStack = returnStackIn;
   }

   public ItemStack getItemStack(Random randomIn) {
      ItemStack itemStack = this.returnStack.copy();
      if (this.maxDamagePercent > 0.0F) {
         int i = (int)(this.maxDamagePercent * this.returnStack.getMaxDamage());
         int j = itemStack.getMaxDamage() - randomIn.nextInt(randomIn.nextInt(i) + 1);
         if (j > i) {
            j = i;
         }

         if (j < 1) {
            j = 1;
         }

         itemStack.setItemDamage(j);
      }

      if (this.enchantable) {
         EnchantmentHelper.addRandomEnchantment(randomIn, itemStack, 30);
      }

      return itemStack;
   }

   public WeightedRandomFishable setMaxDamagePercent(float maxDamagePercentIn) {
      this.maxDamagePercent = maxDamagePercentIn;
      return this;
   }

   public WeightedRandomFishable setEnchantable() {
      this.enchantable = true;
      return this;
   }
}
