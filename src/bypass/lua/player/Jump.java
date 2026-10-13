package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Jump extends ZeroArgFunction {
   private static final long a = 16163680842765L;

   @Override
   public LuaValue call() {
      if (ClientAccess.mc.thePlayer != null) {
         ClientAccess.mc.thePlayer.jump();
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
