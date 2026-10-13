package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Strafe extends VarArgFunction {
   private static final long a = 124031771196410L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      return ClientAccess.mc.thePlayer == null
         ? NIL
         : LuaValue.varargsOf(
            new LuaValue[]{
               LuaValue.valueOf(ClientAccess.mc.thePlayer.movementInput.moveForward), LuaValue.valueOf(ClientAccess.mc.thePlayer.movementInput.moveStrafe)
            }
         );
   }
}
