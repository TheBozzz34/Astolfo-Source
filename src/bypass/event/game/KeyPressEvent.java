package bypass.event.game;

import bypass.event.Event;

public class KeyPressEvent implements Event {
   private int key;

   public KeyPressEvent(int var1) {
      this.key = var1;
   }

   public int getKey() {
      return this.key;
   }
}
