package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Message extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!luaValue.isstring()) {
         return valueOf(false);
      } else if (ClientAccess.mc.thePlayer != null) {
         LuaString luaString = (LuaString)luaValue;
         System.out.println(luaString.checkjstring());
         ClientAccess.mc.thePlayer.sendClientChatMessage(luaString.checkjstring());
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
