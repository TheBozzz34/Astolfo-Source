package bypass.lua.player;

import bypass.ClientAccess;
import bypass.module.player.Scaffold;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class IsOnEdge extends ZeroArgFunction {
   private static final long a = 116869730363998L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(Scaffold.isOnEdge()) : valueOf(false);
   }
}
