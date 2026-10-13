package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class MovingSound extends PositionedSound implements ITickableSound {
   public boolean donePlaying = false;

   public MovingSound(ResourceLocation var1) {
      super(var1);
   }

   @Override
   public boolean isDonePlaying() {
      return this.donePlaying;
   }
}
