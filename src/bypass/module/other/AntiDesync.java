package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.world.DestroyBlockEvent;
import bypass.event.world.RightClickBlockEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;

public final class AntiDesync extends Module {
   private final BooleanSetting breakSetting = new BooleanSetting("Break", "Prevents ghost blocks when breaking blocks", true);
   private final BooleanSetting place = new BooleanSetting("Place", "Prevents ghost blocks when placing blocks", false);
   @EventHandler(DestroyBlockEvent.class)
   private final Listener<DestroyBlockEvent> onDestroyBlock = destroyBlockEvent -> {
      if (this.breakSetting.getValue()) {
         destroyBlockEvent.cancel();
      }
   };
   @EventHandler(RightClickBlockEvent.class)
   private final Listener<RightClickBlockEvent> onRightClickBlock = rightClickBlockEvent -> {
      if (this.place.getValue()) {
         mc.thePlayer.swingItem();
         rightClickBlockEvent.cancel();
      }
   };
   private static final long d = 112189695958198L;

   public AntiDesync(Category category) {
      super(category);
      this.addSettings(this.breakSetting, this.place);
      this.setHidden(false);
   }
}
