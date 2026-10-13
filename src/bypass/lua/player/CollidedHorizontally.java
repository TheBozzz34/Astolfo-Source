package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class CollidedHorizontally extends ZeroArgFunction {
   private static final long a = 32594327480055L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.isCollidedHorizontally) : valueOf(false);
   }
}
