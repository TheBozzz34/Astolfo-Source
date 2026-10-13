package bypass.event.player;

import bypass.event.CancellableEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class StepEvent extends CancellableEvent {
   private StepState state;
   private Entity entity;
   private Vec3 vec;
   private float height = 0.6F;

   public StepEvent(StepState var1, Entity var2, Vec3 var3) {
      this.state = var1;
      this.entity = var2;
      this.vec = var3;
   }

   public StepState getState() {
      return this.state;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public Vec3 getVec() {
      return this.vec;
   }

   public void setHeight(float var1) {
      this.height = var1;
   }

   public float getHeight() {
      return this.height;
   }
}
