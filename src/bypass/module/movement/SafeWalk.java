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
   private Listener<SafeWalkEvent> onSafeWalk = var1x -> {
      if (mc.thePlayer != null) {
         var1x.setSafe(!this.air.getValue());
         var1x.cancel();
      }
   };
   private static final long c = 20099100964816L;

   public SafeWalk(Category var1) {
      super(var1);
      this.addSettings(this.air);
   }
}
