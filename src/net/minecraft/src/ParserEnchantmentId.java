package net.minecraft.src;

import net.minecraft.enchantment.Enchantment;

public class ParserEnchantmentId implements IParserInt {
   private static final long a = 93309168489837L;

   @Override
   public int E(String string, int i) {
      Enchantment enchantment = Enchantment.getEnchantmentByLocation(string);
      return enchantment == null ? i : enchantment.effectId;
   }
}
