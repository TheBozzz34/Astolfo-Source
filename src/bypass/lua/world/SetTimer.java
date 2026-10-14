package bypass.lua.world;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetTimer extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!luaValue.isnil() && luaValue.isnumber()) {
         ClientAccess.mc.timer.timerSpeed = luaValue.tofloat();
         return valueOf(true);
      } else {
         ClientAccess.mc.timer.timerSpeed = 1.0F;
         return NIL;
      }
   }
}
