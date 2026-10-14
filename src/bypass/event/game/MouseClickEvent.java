package bypass.event.game;

import bypass.event.CancellableEvent;

public class MouseClickEvent extends CancellableEvent {
   public int button;

   public MouseClickEvent(int i) {
      this.button = i;
   }
}
