package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Burning extends ZeroArgFunction {
   private static final long a = 32092006081534L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.isBurning()) : valueOf(false);
   }
}
