package bypass.lua.player;

import bypass.ClientAccess;
import bypass.module.movement.Speed;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class BaseSpeed extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null ? valueOf(Speed.getBaseSpeed(1.0, true)) : NIL;
   }
}
