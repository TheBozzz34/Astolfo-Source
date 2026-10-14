package bypass.lua.player;

import bypass.ClientAccess;
import bypass.util.InventoryUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Drop extends OneArgFunction {
   private static final long a = 39365067755628L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (ClientAccess.mc.thePlayer != null && luaValue.isnumber()) {
         InventoryUtil.dropSlot(luaValue.checkint(), 0);
      }

      return NIL;
   }
}
