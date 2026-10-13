package bypass.event.player;

import bypass.event.Event;

public class RotationYawEvent implements Event {
   private float yaw;

   public RotationYawEvent(float var1) {
      this.yaw = var1;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float var1) {
      this.yaw = var1;
   }
}
