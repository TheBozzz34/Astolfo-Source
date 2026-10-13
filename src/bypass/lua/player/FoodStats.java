package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class FoodStats extends ZeroArgFunction {
   private static final long a = 88889960346121L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.getFoodStats().getFoodLevel()) : valueOf(-1);
   }
}
