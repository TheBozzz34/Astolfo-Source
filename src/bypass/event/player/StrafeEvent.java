package bypass.event.player;

import bypass.event.CancellableEvent;

public class StrafeEvent extends CancellableEvent {
   public float strafe;
   public float forward;
   public float friction;

   public StrafeEvent(float var1, float var2, float var3) {
      this.strafe = var1;
      this.forward = var2;
      this.friction = var3;
   }
}
