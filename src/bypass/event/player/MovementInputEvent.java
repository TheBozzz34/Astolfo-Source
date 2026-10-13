package bypass.event.player;

import bypass.event.CancellableEvent;
import bypass.event.Event;
import bypass.event.EventState;

public class MovementInputEvent extends CancellableEvent implements Event {
   private EventState state;
   private static final long a = 18927563709327L;

   public MovementInputEvent(EventState var1) {
      this.state = var1;
   }

   public EventState getState() {
      return this.state;
   }
}
