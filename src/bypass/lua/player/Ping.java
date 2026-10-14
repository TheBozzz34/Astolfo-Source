package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Ping extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      if (ClientAccess.mc.thePlayer == null) {
         return valueOf(false);
      }

      int i = ClientAccess.mc.thePlayer.getPlayerInfo() != null && ClientAccess.mc.thePlayer.getPlayerInfo().getResponseTime() > 1
         ? ClientAccess.mc.thePlayer.getPlayerInfo().getResponseTime()
         : (
            ClientAccess.mc.getCurrentServerData() != null && ClientAccess.mc.getCurrentServerData().pingToServer > 0L
               ? (int)ClientAccess.mc.getCurrentServerData().pingToServer
               : 0
         );
      return valueOf(Math.min(i, 999));
   }
}
