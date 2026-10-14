package bypass.lua.player;

import bypass.lua.PlayerLibrary;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class ConvertSpeed extends TwoArgFunction {
   private static final long a = 31235114793141L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      if (luaValue != null && !luaValue.isnil() && luaValue.istable() && luaValue2 != null && !luaValue2.isnil() && luaValue2.isnumber()) {
         double[] doubles = PlayerLibrary.U(luaValue2.tofloat());
         if (doubles != null) {
            LuaValue luaValue3 = luaValue.get("x");
            if (!luaValue3.isnil() && luaValue3.isnumber()) {
               luaValue.set("x", doubles[0]);
            }

            LuaValue luaValue4 = luaValue.get("z");
            if (!luaValue4.isnil() && luaValue4.isnumber()) {
               luaValue.set("z", doubles[1]);
            }

            return luaValue;
         }
      }

      return NIL;
   }
}
