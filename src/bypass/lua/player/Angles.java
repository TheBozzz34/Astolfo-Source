package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Angles extends VarArgFunction {
   private static final long a = 39980338846521L;

   @Override
   public Varargs onInvoke(Varargs varargs) {
      return LuaValue.varargsOf(
         new LuaValue[]{LuaValue.valueOf(ClientAccess.mc.thePlayer.rotationYaw), LuaValue.valueOf(ClientAccess.mc.thePlayer.rotationPitch)}
      );
   }
}
