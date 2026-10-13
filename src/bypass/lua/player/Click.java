package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class Click extends ThreeArgFunction {
   private static final long a = 78949646917886L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2, LuaValue var3) {
      if (ClientAccess.mc.thePlayer != null && var1.isnumber() && var2.isnumber() && var3.isnumber()) {
         int var7 = 0;
         if (ClientAccess.mc.thePlayer.openContainer != null) {
            var7 = ClientAccess.mc.thePlayer.openContainer.windowId;
         }

         ClientAccess.mc.playerController.windowClick(var7, var1.checkint(), var2.checkint(), var3.checkint(), ClientAccess.mc.thePlayer);
         return valueOf(true);
      } else {
         return NIL;
      }
   }
}
