package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class PrevPosition extends VarArgFunction {
   private static final long a = 107751592928515L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      return ClientAccess.mc.thePlayer == null
         ? NIL
         : LuaValue.varargsOf(
            new LuaValue[]{
               LuaValue.valueOf(ClientAccess.mc.thePlayer.lastReportedPosX),
               LuaValue.valueOf(ClientAccess.mc.thePlayer.lastReportedPosY),
               LuaValue.valueOf(ClientAccess.mc.thePlayer.lastReportedPosZ)
            }
         );
   }
}
