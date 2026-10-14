package bypass.event.player;

import bypass.event.CancellableEvent;
import bypass.event.EventState;

public class MoveWithHeadingEvent extends CancellableEvent {
   private EventState state;
   private float strafe;
   private float forward;
   private static final long a = 25738369960069L;

   public MoveWithHeadingEvent(EventState eventState, float f, float g) {
      this.state = eventState;
      this.strafe = f;
      this.forward = g;
   }

   public MoveWithHeadingEvent(EventState eventState) {
      this.state = eventState;
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

   public void setStrafe(float f) {
      this.strafe = f;
   }

   public void setForward(float f) {
      this.forward = f;
   }
}
