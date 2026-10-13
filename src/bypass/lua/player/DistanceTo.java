package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class DistanceTo extends ThreeArgFunction {
   private static final long a = 43679815397130L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2, LuaValue var3) {
      return ClientAccess.mc.thePlayer != null && var1.isnumber() && var2.isnumber() && var3.isnumber()
         ? valueOf(ClientAccess.mc.thePlayer.getDistance(var1.checkdouble(), var2.checkdouble(), var3.checkdouble()))
         : valueOf(false);
   }
}
