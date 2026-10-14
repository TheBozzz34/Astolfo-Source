package bypass.lua.render;

import bypass.render.RenderUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Rect extends VarArgFunction {
   @Override
   public Varargs invoke(Varargs varargs) {
      double d = 0.0;
      double e = 0.0;
      double d2 = 0.0;
      double d3 = 0.0;
      int i = 0;
      int k = 0;
      int l = 0;
      int m = 0;
      int n = 1;
      if (varargs.narg() < 8) {
         return valueOf(false);
      }

      for (int i2 = varargs.narg(); n <= i2; n++) {
         LuaValue luaValue = varargs.checkvalue(n);
         switch (n) {
            case 1:
               if (luaValue.isnumber()) {
                  d = luaValue.todouble();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  e = luaValue.todouble();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  d2 = luaValue.todouble();
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  d3 = luaValue.todouble();
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  i = luaValue.toint();
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
         }
      }

      RenderUtil.j(d, e, d2, d3, i, k, l, m);
      return valueOf(true);
   }
}
