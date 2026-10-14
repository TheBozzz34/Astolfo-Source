package bypass.lua.render;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.opengl.GL11;

public final class Scale extends OneArgFunction {
   private static final long a = 85170327832780L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (luaValue.isnumber()) {
         GL11.glScalef(luaValue.tofloat(), luaValue.tofloat(), luaValue.tofloat());
         return valueOf(true);
      } else {
         return valueOf(0);
      }
   }
}
