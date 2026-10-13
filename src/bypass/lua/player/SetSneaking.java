package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetSneaking extends OneArgFunction {
   private static final long a = 133748655312355L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isboolean()) {
         return valueOf(false);
      } else if (ClientAccess.mc.thePlayer != null && var1.isboolean()) {
         ClientAccess.mc.gameSettings.keyBindSneak.pressed = var1.checkboolean();
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
