package bypass.lua.render;

import bypass.ClientAccess;
import java.awt.Color;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class StringShadow extends VarArgFunction {
   private static final long a = 14038003223301L;

   @Override
   public Varargs invoke(Varargs varargs) {
      String string = null;
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;
      int m = 0;
      int n = 1;

      for (int i2 = varargs.narg(); n <= i2; n++) {
         LuaValue luaValue = varargs.checkvalue(n);
         switch (n) {
            case 1:
               if (luaValue.isstring()) {
                  string = luaValue.checkjstring();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  i = luaValue.toint();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  j = luaValue.toint();
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  k = luaValue.toint();
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  l = luaValue.toint();
               }
               break;
            case 6:
               if (luaValue.isnumber()) {
                  m = luaValue.toint();
               }
         }
      }

      if (string != null) {
         ClientAccess.mc.fontRendererObj.drawStringWithShadow(string, i, j, new Color(k, l, m).getRGB());
         return valueOf(false);
      } else {
         return valueOf(true);
      }
   }
}
