package bypass.event.game;

import bypass.event.Event;
import bypass.event.EventState;

public class TickEvent implements Event {
   private EventState state;

   public TickEvent(EventState eventState) {
      this.state = eventState;
   }

   public EventState getState() {
      return this.state;
   }
}
