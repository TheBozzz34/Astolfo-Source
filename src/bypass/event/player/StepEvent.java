package bypass.event.player;

import bypass.event.CancellableEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class StepEvent extends CancellableEvent {
   private StepState state;
   private Entity entity;
   private Vec3 vec;
   private float height = 0.6F;

   public StepEvent(StepState stepState, Entity entity, Vec3 vec3) {
      this.state = stepState;
      this.entity = entity;
      this.vec = vec3;
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

   public void setHeight(float f) {
      this.height = f;
   }

   public float getHeight() {
      return this.height;
   }
}
