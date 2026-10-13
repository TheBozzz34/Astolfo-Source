package bypass.event.network;

import bypass.event.CancellableEvent;

public class ResourcePackEvent extends CancellableEvent {
   private String x;

   public ResourcePackEvent(String var1) {
      this.x = var1;
   }

   public String Q() {
      return this.x;
   }
}
