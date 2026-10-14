package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class Click extends ThreeArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2, LuaValue luaValue3) {
      if (ClientAccess.mc.thePlayer != null && luaValue.isnumber() && luaValue2.isnumber() && luaValue3.isnumber()) {
         int i = 0;
         if (ClientAccess.mc.thePlayer.openContainer != null) {
            i = ClientAccess.mc.thePlayer.openContainer.windowId;
         }

         ClientAccess.mc.playerController.windowClick(i, luaValue.checkint(), luaValue2.checkint(), luaValue3.checkint(), ClientAccess.mc.thePlayer);
         return valueOf(true);
      } else {
         return NIL;
      }
   }
}
