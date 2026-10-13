package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class Swap extends TwoArgFunction {
   private static final long a = 52404327214356L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      if (ClientAccess.mc.thePlayer != null && var1.isnumber() && var2.isnumber()) {
         int var6 = 0;
         if (ClientAccess.mc.thePlayer.openContainer != null) {
            var6 = ClientAccess.mc.thePlayer.openContainer.windowId;
         }

         ClientAccess.mc.playerController.windowClick(var6, var1.checkint(), var2.checkint(), 2, ClientAccess.mc.thePlayer);
         return valueOf(true);
      } else {
         return NIL;
      }
   }
}
