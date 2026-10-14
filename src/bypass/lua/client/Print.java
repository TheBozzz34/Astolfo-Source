package bypass.lua.client;

import bypass.ClientChat;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Print extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      ClientChat.CHAT.I(luaValue.tojstring());
      return valueOf(true);
   }
}
