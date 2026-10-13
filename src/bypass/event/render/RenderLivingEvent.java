package bypass.event.render;

import bypass.event.CancellableEvent;
import net.minecraft.entity.EntityLivingBase;

public class RenderLivingEvent extends CancellableEvent {
   private float prevPitch;
   private float pitch;
   private EntityLivingBase entity;

   public RenderLivingEvent(EntityLivingBase var1) {
      this.entity = var1;
      this.pitch = var1.rotationPitch;
      this.prevPitch = var1.prevRotationPitch;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float var1) {
      this.pitch = var1;
   }

   public float getPrevPitch() {
      return this.prevPitch;
   }

   public void setPrevPitch(float var1) {
      this.prevPitch = var1;
   }
}
