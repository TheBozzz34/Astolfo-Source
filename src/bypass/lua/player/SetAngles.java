package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class SetAngles extends TwoArgFunction {
   private static final long a = 92987306766068L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      if (ClientAccess.mc.thePlayer != null && var1.isnumber() && var2.isnumber()) {
         ClientAccess.mc.thePlayer.rotationYaw = var1.tofloat();
         ClientAccess.mc.thePlayer.rotationPitch = var2.tofloat();
      }

      return valueOf(false);
   }
}
