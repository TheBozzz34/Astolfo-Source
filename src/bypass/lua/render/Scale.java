package bypass.lua.render;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.lwjgl.opengl.GL11;

public final class Scale extends OneArgFunction {
   private static final long a = 85170327832780L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (var1.isnumber()) {
         GL11.glScalef(var1.tofloat(), var1.tofloat(), var1.tofloat());
         return valueOf(true);
      } else {
         return valueOf(0);
      }
   }
}
