package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class SetMotion extends ThreeArgFunction {
   private static final long a = 42601729641317L;

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2, LuaValue luaValue3) {
      if (ClientAccess.mc.thePlayer != null) {
         if (luaValue.isnumber() && luaValue2.isnumber() && luaValue3.isnumber()) {
            ClientAccess.mc.thePlayer.motionX = luaValue.todouble();
            ClientAccess.mc.thePlayer.motionY = luaValue2.todouble();
            ClientAccess.mc.thePlayer.motionZ = luaValue3.todouble();
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      } else {
         return valueOf(false);
      }
   }
}
