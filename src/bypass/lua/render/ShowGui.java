package bypass.lua.render;

import bypass.gui.screen.LuaScreen;
import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class ShowGui extends OneArgFunction {
   private static final long a = 47917397440172L;

   @Override
   public LuaValue call(LuaValue var1) {
      LuaTable var5 = null;
      if (var1.isfunction()) {
         LuaValue var6 = var1.call();
         if (var6.istable()) {
            var5 = (LuaTable)var6.call();
         }
      } else if (var1.istable()) {
         var5 = (LuaTable)var1;
      }

      if (var5 != null) {
         Minecraft.getMinecraft().displayGuiScreen(new LuaScreen(var5));
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
