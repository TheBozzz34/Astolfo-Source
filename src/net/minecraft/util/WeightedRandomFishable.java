package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom.Item {
   private final ItemStack returnStack;
   private float maxDamagePercent;
   private boolean enchantable;
   private static final long a = 20972655405507L;
   private static final long b = -3692151808324534242L;

   public WeightedRandomFishable(ItemStack var1, int var2) {
      super(var2);
      this.returnStack = var1;
   }

   public ItemStack getItemStack(Random var1) {
      ItemStack var5 = this.returnStack.copy();
      if (this.maxDamagePercent > 0.0F) {
         int var6 = (int)(this.maxDamagePercent * this.returnStack.getMaxDamage());
         int var7 = var5.getMaxDamage() - var1.nextInt(var1.nextInt(var6) + 1);
         if (var7 > var6) {
            var7 = var6;
         }

         if (var7 < 1) {
            var7 = 1;
         }

         var5.setItemDamage(var7);
      }

      if (this.enchantable) {
         EnchantmentHelper.addRandomEnchantment(var1, var5, (int)b);
      }

      return var5;
   }

   public WeightedRandomFishable setMaxDamagePercent(float var1) {
      this.maxDamagePercent = var1;
      return this;
   }

   public WeightedRandomFishable setEnchantable() {
      this.enchantable = true;
      return this;
   }
}
