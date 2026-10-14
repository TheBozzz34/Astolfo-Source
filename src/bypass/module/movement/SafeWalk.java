package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.SafeWalkEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;

public final class SafeWalk extends Module {
   private final BooleanSetting air = new BooleanSetting("Air", "Safe walks on air", false);
   @EventHandler(SafeWalkEvent.class)
   private Listener<SafeWalkEvent> onSafeWalk = safeWalkEvent -> {
      if (mc.thePlayer != null) {
         safeWalkEvent.setSafe(!this.air.getValue());
         safeWalkEvent.cancel();
      }
   };

   public SafeWalk(Category category) {
      super(category);
      this.addSettings(this.air);
   }
}
