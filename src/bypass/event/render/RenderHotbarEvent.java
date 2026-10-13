package bypass.event.render;

import bypass.event.Event;

public class RenderHotbarEvent implements Event {
   private int slot;

   public RenderHotbarEvent(int var1) {
      this.slot = var1;
   }

   public int getSlot() {
      return this.slot;
   }

   public void setSlot(int var1) {
      this.slot = var1;
   }
}
