package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class SetPosition extends ThreeArgFunction {
   private static final long a = 43503132481265L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2, LuaValue var3) {
      if (ClientAccess.mc.thePlayer != null) {
         if (var1.isnumber() && var2.isnumber() && var3.isnumber()) {
            ClientAccess.mc.thePlayer.setPosition(var1.todouble(), var2.todouble(), var3.todouble());
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      } else {
         return valueOf(false);
      }
   }
}
