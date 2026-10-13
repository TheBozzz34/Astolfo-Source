package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsPotionActive extends OneArgFunction {
   private static final long a = 38129615873997L;

   @Override
   public LuaValue call(LuaValue var1) {
      return ClientAccess.mc.thePlayer != null && var1.isnumber() ? valueOf(ClientAccess.mc.thePlayer.isPotionActive(var1.checkint())) : valueOf(false);
   }
}
