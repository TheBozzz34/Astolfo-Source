package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class EyeHeight extends ZeroArgFunction {
   private static final long a = 19745353300104L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.getEyeHeight()) : valueOf(-1);
   }
}
