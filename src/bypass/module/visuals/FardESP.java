package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.module.Category;
import bypass.module.Module;

public final class FardESP extends Module {
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private Listener<RenderEvent> onRender = var0 -> {};
   private static final long d = 52802462950628L;

   public FardESP() {
      super("FardESP", Category.VISUALS);
   }
}
