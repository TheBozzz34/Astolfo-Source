package bypass.lua.client;

import bypass.ClientChat;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Print extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue var1) {
      ClientChat.CHAT.I(var1.tojstring());
      return valueOf(true);
   }
}
