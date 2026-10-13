package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class OnGround extends ZeroArgFunction {
   private static final long a = 75762686258207L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.onGround) : valueOf(false);
   }
}
