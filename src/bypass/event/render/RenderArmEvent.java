package bypass.event.render;

import bypass.event.Event;
import bypass.event.EventState;

public class RenderArmEvent implements Event {
   private EventState state;

   public RenderArmEvent(EventState eventState) {
      this.state = eventState;
   }

   public EventState getState() {
      return this.state;
   }
}
