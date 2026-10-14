package bypass.lua.render;

import bypass.render.RenderUtil;
import java.awt.Color;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Circle extends VarArgFunction {
   private static final long a = 47825080443389L;
   private static final long b = 3972618498512781320L;

   @Override
   public Varargs invoke(Varargs varargs) {
      double d = 0.0;
      double e = 0.0;
      int i = 0;
      int j = 0;
      double d2 = 0.0;
      int k = 0;
      int l = 0;
      int m = 0;
      int n = 0;
      int i2 = 1;
      if (varargs.narg() < (int)b) {
         return valueOf(false);
      }

      for (int i3 = varargs.narg(); i2 <= i3; i2++) {
         LuaValue luaValue = varargs.checkvalue(i2);
         switch (i2) {
            case 1:
               if (luaValue.isnumber()) {
                  d = luaValue.checkdouble();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  e = luaValue.checkdouble();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  i = luaValue.toint();
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  j = luaValue.toint();
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  d2 = luaValue.checkdouble();
               }
               break;
            case 6:
               if (luaValue.isnumber()) {
                  k = luaValue.toint();
               }
               break;
            case 7:
               if (luaValue.isnumber()) {
                  l = luaValue.toint();
               }
               break;
            case 8:
               if (luaValue.isnumber()) {
                  m = luaValue.toint();
               }
               break;
            case 9:
               if (luaValue.isnumber()) {
                  n = luaValue.toint();
               }
         }
      }

      RenderUtil.drawArc(d, e, i, j, d2, new Color(k, l, m, n).getRGB());
      return valueOf(true);
   }
}
