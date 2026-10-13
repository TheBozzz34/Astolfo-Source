package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class SetMotion extends ThreeArgFunction {
   private static final long a = 42601729641317L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2, LuaValue var3) {
      if (ClientAccess.mc.thePlayer != null) {
         if (var1.isnumber() && var2.isnumber() && var3.isnumber()) {
            ClientAccess.mc.thePlayer.motionX = var1.todouble();
            ClientAccess.mc.thePlayer.motionY = var2.todouble();
            ClientAccess.mc.thePlayer.motionZ = var3.todouble();
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      } else {
         return valueOf(false);
      }
   }
}
