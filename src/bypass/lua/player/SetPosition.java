package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class SetPosition extends ThreeArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2, LuaValue luaValue3) {
      if (ClientAccess.mc.thePlayer != null) {
         if (luaValue.isnumber() && luaValue2.isnumber() && luaValue3.isnumber()) {
            ClientAccess.mc.thePlayer.setPosition(luaValue.todouble(), luaValue2.todouble(), luaValue3.todouble());
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      } else {
         return valueOf(false);
      }
   }
}
