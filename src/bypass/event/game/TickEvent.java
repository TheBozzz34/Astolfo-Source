package bypass.event.game;

import bypass.event.Event;
import bypass.event.EventState;

public class TickEvent implements Event {
   private EventState state;

   public TickEvent(EventState var1) {
      this.state = var1;
   }

   public EventState getState() {
      return this.state;
   }
}
