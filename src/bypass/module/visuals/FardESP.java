package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.module.Category;
import bypass.module.Module;

public final class FardESP extends Module {
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private Listener<RenderEvent> onRender = renderEvent -> {};

   public FardESP() {
      super("FardESP", Category.VISUALS);
   }
}
