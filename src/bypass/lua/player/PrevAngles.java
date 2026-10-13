package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class PrevAngles extends VarArgFunction {
   @Override
   public Varargs onInvoke(Varargs var1) {
      return LuaValue.varargsOf(
         new LuaValue[]{LuaValue.valueOf(ClientAccess.mc.thePlayer.prevRotationYaw), LuaValue.valueOf(ClientAccess.mc.thePlayer.prevRotationPitch)}
      );
   }
}
