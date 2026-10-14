package bypass.lua.render;

import bypass.render.RenderUtil;
import java.awt.Color;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Line extends VarArgFunction {
   private static final long a = 122233959833589L;
   private static final long b = 1874111062668738569L;

   @Override
   public Varargs invoke(Varargs varargs) {
      float f = 0.0F;
      float g = 0.0F;
      float h = 0.0F;
      float f2 = 0.0F;
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;
      float f3 = 1.0F;
      int m = 1;
      if (varargs.narg() < (int)b) {
         return valueOf(false);
      }

      for (int n = varargs.narg(); m <= n; m++) {
         LuaValue luaValue = varargs.checkvalue(m);
         switch (m) {
            case 1:
               if (luaValue.isnumber()) {
                  f = luaValue.tofloat();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  g = luaValue.tofloat();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  h = luaValue.tofloat();
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  f2 = luaValue.tofloat();
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  f3 = luaValue.tofloat();
               }
               break;
            case 6:
               if (luaValue.isnumber()) {
                  i = luaValue.toint();
               }
               break;
            case 7:
               if (luaValue.isnumber()) {
                  j = luaValue.toint();
               }
               break;
            case 8:
               if (luaValue.isnumber()) {
                  k = luaValue.toint();
               }
               break;
            case 9:
               if (luaValue.isnumber()) {
                  l = luaValue.toint();
               }
         }
      }

      RenderUtil.drawLine(f3, f, g, h, f2, new Color(i, j, k, l).getRGB());
      return valueOf(true);
   }
}
