package bypass.lua.render;

import bypass.ClientAccess;
import java.awt.Color;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class StringFunction extends VarArgFunction {
   private static final long a = 300361218307L;

   @Override
   public Varargs invoke(Varargs var1) {
      String var5 = null;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int var10 = 0;
      int var11 = 1;

      for (int var12 = var1.narg(); var11 <= var12; var11++) {
         LuaValue var13 = var1.checkvalue(var11);
         switch (var11) {
            case 1:
               if (var13.isstring()) {
                  var5 = var13.checkjstring();
               }
               break;
            case 2:
               if (var13.isnumber()) {
                  var6 = var13.toint();
               }
               break;
            case 3:
               if (var13.isnumber()) {
                  var7 = var13.toint();
               }
               break;
            case 4:
               if (var13.isnumber()) {
                  var8 = var13.toint();
               }
               break;
            case 5:
               if (var13.isnumber()) {
                  var9 = var13.toint();
               }
               break;
            case 6:
               if (var13.isnumber()) {
                  var10 = var13.toint();
               }
         }
      }

      if (var5 != null) {
         ClientAccess.mc.fontRendererObj.D(var5, var6, var7, new Color(var8, var9, var10).getRGB());
         return valueOf(false);
      } else {
         return valueOf(true);
      }
   }
}
