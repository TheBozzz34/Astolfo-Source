package bypass.event.player;

import bypass.event.CancellableEvent;
import bypass.event.EventState;

public class MoveWithHeadingEvent extends CancellableEvent {
   private EventState state;
   private float strafe;
   private float forward;
   private static final long a = 25738369960069L;

   public MoveWithHeadingEvent(EventState var1, float var2, float var3) {
      this.state = var1;
      this.strafe = var2;
      this.forward = var3;
   }

   public MoveWithHeadingEvent(EventState var1) {
      this.state = var1;
   }

   public EventState getState() {
      return this.state;
   }

   public float getStrafe() {
      return this.strafe;
   }

   public float getForward() {
      return this.forward;
   }

   public void setStrafe(float var1) {
      this.strafe = var1;
   }

   public void setForward(float var1) {
      this.forward = var1;
   }
}
