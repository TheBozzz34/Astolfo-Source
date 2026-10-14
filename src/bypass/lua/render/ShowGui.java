package bypass.lua.render;

import bypass.gui.screen.LuaScreen;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class ShowGui extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      LuaTable luaTable = null;
      if (luaValue.isfunction()) {
         LuaValue luaValue2 = luaValue.call();
         if (luaValue2.istable()) {
            luaTable = (LuaTable)luaValue2.call();
         }
      } else if (luaValue.istable()) {
         luaTable = (LuaTable)luaValue;
      }

      if (luaTable != null) {
         Minecraft.getMinecraft().displayGuiScreen(new LuaScreen(luaTable));
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
