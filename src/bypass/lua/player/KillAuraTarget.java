package bypass.lua.player;

import bypass.ClientAccess;
import bypass.module.combat.KillAura;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class KillAuraTarget extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      return ClientAccess.mc.thePlayer != null && KillAura.target != null ? valueOf(KillAura.target.getEntityId()) : NIL;
   }
}
