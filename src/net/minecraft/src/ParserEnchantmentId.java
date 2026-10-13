package net.minecraft.src;

import net.minecraft.enchantment.Enchantment;

public class ParserEnchantmentId implements IParserInt {
   private static final long a = 93309168489837L;

   @Override
   public int E(String var1, int var2) {
      Enchantment var6 = Enchantment.getEnchantmentByLocation(var1);
      return var6 == null ? var2 : var6.effectId;
   }
}
