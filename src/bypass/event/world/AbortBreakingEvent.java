package bypass.event.world;

import bypass.event.CancellableEvent;

public class AbortBreakingEvent extends CancellableEvent {
   public AbortBreakingEvent(boolean bl) {
      this.setCancelled(bl);
   }
}
