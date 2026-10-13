package bypass.event.player;

import bypass.event.CancellableEvent;

public class SafeWalkEvent extends CancellableEvent {
   private boolean safe;

   public void setSafe(boolean var1) {
      this.safe = var1;
   }

   public boolean isSafe() {
      return this.safe;
   }
}
