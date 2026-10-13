package bypass.lua.render;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class GetStringWidth extends OneArgFunction {
   private static final long a = 88159425224570L;

   @Override
   public LuaValue call(LuaValue var1) {
      return var1.isstring() ? valueOf(ClientAccess.mc.fontRendererObj.getStringWidth(var1.checkjstring())) : valueOf(0);
   }
}
