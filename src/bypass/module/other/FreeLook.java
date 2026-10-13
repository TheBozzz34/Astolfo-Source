package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import org.lwjgl.input.Keyboard;

public class FreeLook extends Module {
   public static final BooleanSetting mouseWheel = new BooleanSetting("Mouse-Wheel", "Works when clicking mouse-wheel", false);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      if (var1x.getState() == EventState.PRE && !Keyboard.isKeyDown(this.getKey()) && !mouseWheel.getValue()) {
         this.setEnabled(false);
      }
   };
   private static final long c = 79194499737349L;

   public FreeLook(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(mouseWheel);
   }
}
