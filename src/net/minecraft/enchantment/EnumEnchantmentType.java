package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {
   ALL,
   ARMOR,
   ARMOR_FEET,
   ARMOR_LEGS,
   ARMOR_TORSO,
   ARMOR_HEAD,
   WEAPON,
   DIGGER,
   FISHING_ROD,
   BREAKABLE,
   BOW;

   private static final long a = 13763999508849L;

   public boolean canEnchantItem(Item var1) {
      if (this == ALL) {
         return true;
      }

      if (this == BREAKABLE && var1.isDamageable()) {
         return true;
      }

      if (var1 instanceof ItemArmor) {
         if (this == ARMOR) {
            return true;
         }

         ItemArmor var5 = (ItemArmor)var1;
         return var5.armorType == 0
            ? this == ARMOR_HEAD
            : (var5.armorType == 2 ? this == ARMOR_LEGS : (var5.armorType == 1 ? this == ARMOR_TORSO : (var5.armorType == 3 ? this == ARMOR_FEET : false)));
      } else {
         return var1 instanceof ItemSword
            ? this == WEAPON
            : (
               var1 instanceof ItemTool
                  ? this == DIGGER
                  : (var1 instanceof ItemBow ? this == BOW : (var1 instanceof ItemFishingRod ? this == FISHING_ROD : false))
            );
      }
   }
}
