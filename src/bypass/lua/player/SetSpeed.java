package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetSpeed extends OneArgFunction {
   private static final long a = 35408269996514L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (ClientAccess.mc.thePlayer != null) {
         if (luaValue.isnumber()) {
            ClientAccess.mc.thePlayer.G(luaValue.todouble());
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      } else {
         return valueOf(false);
      }
   }
}
