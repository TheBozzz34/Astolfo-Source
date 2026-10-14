package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class SwingItem extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      if (ClientAccess.mc.thePlayer != null) {
         ClientAccess.mc.thePlayer.swingItem();
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
