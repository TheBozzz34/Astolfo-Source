package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class SwingItem extends ZeroArgFunction {
   private static final long a = 1796050554085L;

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
