package net.minecraft.client.audio;

import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;

public class GuardianSound extends MovingSound {
   private final EntityGuardian guardian;
   private static final long a = 76963177436223L;

   public GuardianSound(EntityGuardian var1) {
      super(new ResourceLocation("minecraft:mob.guardian.attack"));
      this.guardian = var1;
      this.attenuationType = ISound.AttenuationType.NONE;
      this.repeat = true;
      this.repeatDelay = 0;
   }

   @Override
   public void update() {
      if (!this.guardian.isDead && this.guardian.hasTargetedEntity()) {
         this.xPosF = (float)this.guardian.posX;
         this.yPosF = (float)this.guardian.posY;
         this.zPosF = (float)this.guardian.posZ;
         float var4 = this.guardian.func_175477_p(0.0F);
         this.volume = 0.0F + 1.0F * var4 * var4;
         this.pitch = 0.7F + 0.5F * var4;
      } else {
         this.donePlaying = true;
      }
   }
}
