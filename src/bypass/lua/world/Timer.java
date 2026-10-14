package bypass.lua.world;

import bypass.ClientAccess;
import bypass.module.player.Scaffold;
import bypass.util.GameUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Timer extends ZeroArgFunction {
   private static final long a = 26471393788142L;

   @Override
   public LuaValue call() {
      long l = a ^ 89263565533700L;
      long m = l ^ 138864123580249L;
      return ClientAccess.client.getModuleManager().get(Scaffold.class).isEnabled()
            && GameUtil.isOnHypixel()
            && Scaffold.keepSprint.getValue()
            && ClientAccess.mc.thePlayer.onGround
         ? valueOf(1.0)
         : valueOf(ClientAccess.mc.timer.timerSpeed);
   }
}
