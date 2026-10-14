package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetSprinting extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (ClientAccess.mc.thePlayer != null && luaValue.isboolean()) {
         ClientAccess.mc.thePlayer.setSprinting(luaValue.checkboolean());
      }

      return valueOf(false);
   }
}
