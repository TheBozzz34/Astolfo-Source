package bypass.lua.world;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class SetTimer extends OneArgFunction {
   private static final long a = 123416773759207L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isnil() && var1.isnumber()) {
         ClientAccess.mc.timer.timerSpeed = var1.tofloat();
         return valueOf(true);
      } else {
         ClientAccess.mc.timer.timerSpeed = 1.0F;
         return NIL;
      }
   }
}
