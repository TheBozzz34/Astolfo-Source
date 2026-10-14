package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class CameraPosition extends VarArgFunction {
   private static final long a = 58165353183243L;

   @Override
   public Varargs onInvoke(Varargs varargs) {
      return ClientAccess.mc.thePlayer == null
         ? NIL
         : LuaValue.varargsOf(
            new LuaValue[]{
               LuaValue.valueOf(ClientAccess.mc.getRenderManager().viewerPosX),
               LuaValue.valueOf(ClientAccess.mc.getRenderManager().viewerPosY),
               LuaValue.valueOf(ClientAccess.mc.getRenderManager().viewerPosZ)
            }
         );
   }
}
