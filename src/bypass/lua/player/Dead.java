package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Dead extends ZeroArgFunction {
   private static final long a = 62162185820743L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(!ClientAccess.mc.thePlayer.isEntityAlive()) : valueOf(false);
   }
}
