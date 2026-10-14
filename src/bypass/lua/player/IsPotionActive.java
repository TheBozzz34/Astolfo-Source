package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsPotionActive extends OneArgFunction {
   private static final long a = 38129615873997L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      return ClientAccess.mc.thePlayer != null && luaValue.isnumber() ? valueOf(ClientAccess.mc.thePlayer.isPotionActive(luaValue.checkint())) : valueOf(false);
   }
}
