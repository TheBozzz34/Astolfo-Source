package net.minecraft.src;

import net.minecraft.enchantment.Enchantment;

public class ParserEnchantmentId implements IParserInt {
   @Override
   public int parse(String string, int i) {
      Enchantment enchantment = Enchantment.getEnchantmentByLocation(string);
      return enchantment == null ? i : enchantment.effectId;
   }
}
