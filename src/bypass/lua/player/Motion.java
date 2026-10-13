package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Motion extends VarArgFunction {
   private static final long a = 43755509794205L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      return ClientAccess.mc.thePlayer == null
         ? NIL
         : LuaValue.varargsOf(
            new LuaValue[]{
               LuaValue.valueOf(ClientAccess.mc.thePlayer.motionX),
               LuaValue.valueOf(ClientAccess.mc.thePlayer.motionY),
               LuaValue.valueOf(ClientAccess.mc.thePlayer.motionZ)
            }
         );
   }
}
