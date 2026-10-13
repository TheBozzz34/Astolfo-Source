package bypass.lua.player;

import bypass.ClientAccess;
import bypass.module.movement.LongJump;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Damage extends OneArgFunction {
   private static final long a = 16039466804463L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isnumber()) {
         return valueOf(false);
      } else if (ClientAccess.mc.thePlayer != null) {
         LongJump.selfDamage(var1.checkdouble());
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
