package net.minecraft.potion;

import net.minecraft.util.ResourceLocation;

public class PotionHealth extends Potion {
   private static final long j = 18922483111348L;

   public PotionHealth(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }

   @Override
   public boolean isInstant() {
      return true;
   }

   @Override
   public boolean isReady(int i, int k) {
      return i >= 1;
   }
}
