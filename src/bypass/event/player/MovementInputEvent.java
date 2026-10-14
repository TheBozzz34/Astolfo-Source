package bypass.event.player;

import bypass.event.CancellableEvent;
import bypass.event.Event;
import bypass.event.EventState;

public class MovementInputEvent extends CancellableEvent implements Event {
   private EventState state;

   public MovementInputEvent(EventState eventState) {
      this.state = eventState;
   }

   public EventState getState() {
      return this.state;
   }
}
