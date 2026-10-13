package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Message extends OneArgFunction {
   private static final long a = 70715108024355L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isstring()) {
         return valueOf(false);
      } else if (ClientAccess.mc.thePlayer != null) {
         LuaString var5 = (LuaString)var1;
         System.out.println(var5.checkjstring());
         ClientAccess.mc.thePlayer.sendClientChatMessage(var5.checkjstring());
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
