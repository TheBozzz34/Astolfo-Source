package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Facing extends ZeroArgFunction {
   private static final long a = 11744885471319L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.getHorizontalFacing().ordinal() + 1) : valueOf(-1);
   }
}
