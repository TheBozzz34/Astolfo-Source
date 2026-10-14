package bypass.event.render;

import bypass.event.CancellableEvent;
import net.minecraft.entity.EntityLivingBase;

public class RenderLivingEvent extends CancellableEvent {
   private float prevPitch;
   private float pitch;
   private EntityLivingBase entity;

   public RenderLivingEvent(EntityLivingBase entityLivingBase) {
      this.entity = entityLivingBase;
      this.pitch = entityLivingBase.rotationPitch;
      this.prevPitch = entityLivingBase.prevRotationPitch;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public float getPrevPitch() {
      return this.prevPitch;
   }

   public void setPrevPitch(float f) {
      this.prevPitch = f;
   }
}
