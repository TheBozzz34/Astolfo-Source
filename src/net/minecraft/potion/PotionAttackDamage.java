package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class PotionAttackDamage extends Potion {
   private static final long j = 96238872918620L;

   public PotionAttackDamage(int var1, ResourceLocation var2, boolean var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public double getAttributeModifierAmount(int var1, AttributeModifier var2) {
      return this.id == Potion.weakness.id ? -0.5F * (var1 + 1) : 1.3 * (var1 + 1);
   }
}
