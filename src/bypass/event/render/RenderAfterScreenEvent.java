package bypass.event.render;

import bypass.event.Event;

public class RenderAfterScreenEvent implements Event {
   private int width;
   private int height;

   public RenderAfterScreenEvent(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }
}
