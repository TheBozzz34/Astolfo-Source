package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class OnLadder extends ZeroArgFunction {
   private static final long a = 110476106599346L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.isOnLadder()) : valueOf(false);
   }
}
