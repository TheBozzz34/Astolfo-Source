package bypass.lua.render;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.opengl.GL11;

public final class LineWidth extends OneArgFunction {
   private static final long a = 71155750255436L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (luaValue.isnumber()) {
         GL11.glLineWidth(luaValue.tofloat());
         return valueOf(true);
      } else {
         return valueOf(0);
      }
   }
}
