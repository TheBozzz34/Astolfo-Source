package bypass.event.render;

import bypass.event.Event;

public class RenderAfterScreenEvent implements Event {
   private int width;
   private int height;

   public RenderAfterScreenEvent(int i, int j) {
      this.width = i;
      this.height = j;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }
}
