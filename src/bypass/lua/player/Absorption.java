package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Absorption extends ZeroArgFunction {
   private static final long a = 42734516191366L;

   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(ClientAccess.mc.thePlayer.getAbsorptionAmount()) : valueOf(-1);
   }
}
