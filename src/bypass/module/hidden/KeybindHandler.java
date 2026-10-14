package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.game.KeyPressEvent;
import bypass.module.HiddenModule;
import bypass.module.Module;
import bypass.module.ModuleInfo;
import java.util.List;

public final class KeybindHandler extends HiddenModule {
   @EventHandler(KeyPressEvent.class)
   private Listener<KeyPressEvent> onKeyPress = keyPressEvent -> {
      long l = d ^ 45518458676977L;
      long m = l ^ 138356505475087L;
      List list = client.getModuleManager().getList();
      int i = 0;

      for (int j = list.size(); i < j; i++) {
         ModuleInfo moduleInfo = (ModuleInfo)list.get(i);
         if (moduleInfo instanceof Module) {
            Module module = (Module)moduleInfo;
            if (module.getKey() == keyPressEvent.getKey()) {
               module.toggle();
            }
         }
      }
   };
   private static final long d = 109695445132109L;

   public KeybindHandler() {
      client.getEventBus().subscribe(this);
   }
}
