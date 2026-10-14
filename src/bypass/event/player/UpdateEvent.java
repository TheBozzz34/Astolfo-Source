package bypass.event.player;

import bypass.event.Event;
import bypass.event.EventState;

public class UpdateEvent implements Event {
   private EventState state;

   public UpdateEvent(EventState eventState) {
      this.state = eventState;
   }

   public EventState getState() {
      return this.state;
   }
}
