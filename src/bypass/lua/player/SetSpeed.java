package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetSpeed extends OneArgFunction {
   private static final long a = 35408269996514L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (ClientAccess.mc.thePlayer != null) {
         if (var1.isnumber()) {
            ClientAccess.mc.thePlayer.G(var1.todouble());
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      } else {
         return valueOf(false);
      }
   }
}
