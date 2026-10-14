package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class SetAngles extends TwoArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      if (ClientAccess.mc.thePlayer != null && luaValue.isnumber() && luaValue2.isnumber()) {
         ClientAccess.mc.thePlayer.rotationYaw = luaValue.tofloat();
         ClientAccess.mc.thePlayer.rotationPitch = luaValue2.tofloat();
      }

      return valueOf(false);
   }
}
