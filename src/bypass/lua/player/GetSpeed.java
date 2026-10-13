package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class GetSpeed extends ZeroArgFunction {
   private static final long a = 137518534961168L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.B()) : valueOf(-1);
   }
}
