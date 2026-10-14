package bypass.event.player;

import bypass.event.CancellableEvent;

public class StrafeEvent extends CancellableEvent {
   public float strafe;
   public float forward;
   public float friction;

   public StrafeEvent(float f, float g, float h) {
      this.strafe = f;
      this.forward = g;
      this.friction = h;
   }
}
