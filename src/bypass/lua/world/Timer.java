package bypass.lua.world;

import bypass.ClientAccess;
import bypass.module.player.Scaffold;
import bypass.util.GameUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Timer extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      return ClientAccess.client.getModuleManager().get(Scaffold.class).isEnabled()
            && GameUtil.isOnHypixel()
            && Scaffold.keepSprint.getValue()
            && ClientAccess.mc.thePlayer.onGround
         ? valueOf(1.0)
         : valueOf(ClientAccess.mc.timer.timerSpeed);
   }
}
