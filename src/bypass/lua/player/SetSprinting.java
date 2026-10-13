package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetSprinting extends OneArgFunction {
   private static final long a = 90859375867728L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (ClientAccess.mc.thePlayer != null && var1.isboolean()) {
         ClientAccess.mc.thePlayer.setSprinting(var1.checkboolean());
      }

      return valueOf(false);
   }
}
