package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetSneaking extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!luaValue.isboolean()) {
         return valueOf(false);
      } else if (ClientAccess.mc.thePlayer != null && luaValue.isboolean()) {
         ClientAccess.mc.gameSettings.keyBindSneak.pressed = luaValue.checkboolean();
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
