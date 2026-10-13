package bypass.event.world;

import bypass.event.CancellableEvent;

public class AbortBreakingEvent extends CancellableEvent {
   public AbortBreakingEvent(boolean var1) {
      this.setCancelled(var1);
   }
}
