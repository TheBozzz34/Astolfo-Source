package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetSpeed extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (ClientAccess.mc.thePlayer != null) {
         if (luaValue.isnumber()) {
            ClientAccess.mc.thePlayer.setSpeedIfMoving(luaValue.todouble());
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      } else {
         return valueOf(false);
      }
   }
}
