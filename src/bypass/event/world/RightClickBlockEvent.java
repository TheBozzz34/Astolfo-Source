package bypass.event.world;

import bypass.event.CancellableEvent;
import bypass.event.EventState;

public class RightClickBlockEvent extends CancellableEvent {
   private EventState state;

   public RightClickBlockEvent(EventState var1) {
      this.state = var1;
   }
}
