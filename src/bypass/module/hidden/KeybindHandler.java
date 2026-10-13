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
   private Listener<KeyPressEvent> onKeyPress = var0 -> {
      long var1 = d ^ 45518458676977L;
      long var3 = var1 ^ 138356505475087L;
      List var6 = client.getModuleManager().getList();
      int var7 = 0;

      for (int var8 = var6.size(); var7 < var8; var7++) {
         ModuleInfo var9 = (ModuleInfo)var6.get(var7);
         if (var9 instanceof Module) {
            Module var10 = (Module)var9;
            if (var10.getKey() == var0.getKey()) {
               var10.toggle();
            }
         }
      }
   };
   private static final long d = 109695445132109L;

   public KeybindHandler() {
      client.getEventBus().subscribe(this);
   }
}
