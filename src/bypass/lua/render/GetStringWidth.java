package bypass.lua.render;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class GetStringWidth extends OneArgFunction {
   private static final long a = 88159425224570L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      return luaValue.isstring() ? valueOf(ClientAccess.mc.fontRendererObj.getStringWidth(luaValue.checkjstring())) : valueOf(0);
   }
}
