package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Ping extends ZeroArgFunction {
   private static final long a = 122457512081402L;
   private static final long b = -8005043291340930073L;

   @Override
   public LuaValue call() {
      if (ClientAccess.mc.thePlayer == null) {
         return valueOf(false);
      }

      int var4 = ClientAccess.mc.thePlayer.getPlayerInfo() != null && ClientAccess.mc.thePlayer.getPlayerInfo().getResponseTime() > 1
         ? ClientAccess.mc.thePlayer.getPlayerInfo().getResponseTime()
         : (
            ClientAccess.mc.getCurrentServerData() != null && ClientAccess.mc.getCurrentServerData().pingToServer > 0L
               ? (int)ClientAccess.mc.getCurrentServerData().pingToServer
               : 0
         );
      return valueOf(Math.min(var4, (int)b));
   }
}
