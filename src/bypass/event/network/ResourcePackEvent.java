package bypass.event.network;

import bypass.event.CancellableEvent;

public class ResourcePackEvent extends CancellableEvent {
   private String x;

   public ResourcePackEvent(String string) {
      this.x = string;
   }

   public String Q() {
      return this.x;
   }
}
