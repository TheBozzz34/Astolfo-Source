package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class DistanceTo extends ThreeArgFunction {
   private static final long a = 43679815397130L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2, LuaValue luaValue3) {
      return ClientAccess.mc.thePlayer != null && luaValue.isnumber() && luaValue2.isnumber() && luaValue3.isnumber()
         ? valueOf(ClientAccess.mc.thePlayer.getDistance(luaValue.checkdouble(), luaValue2.checkdouble(), luaValue3.checkdouble()))
         : valueOf(false);
   }
}
