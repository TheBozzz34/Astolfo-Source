package bypass.lua.render;

import bypass.render.RenderUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Rect extends VarArgFunction {
   private static final long a = 126434909090325L;
   private static final long b = -4796755897219022840L;

   @Override
   public Varargs invoke(Varargs var1) {
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = 0.0;
      double var11 = 0.0;
      int var13 = 0;
      int var14 = 0;
      int var15 = 0;
      int var16 = 0;
      int var17 = 1;
      if (var1.narg() < (int)b) {
         return valueOf(false);
      }

      for (int var18 = var1.narg(); var17 <= var18; var17++) {
         LuaValue var19 = var1.checkvalue(var17);
         switch (var17) {
            case 1:
               if (var19.isnumber()) {
                  var5 = var19.todouble();
               }
               break;
            case 2:
               if (var19.isnumber()) {
                  var7 = var19.todouble();
               }
               break;
            case 3:
               if (var19.isnumber()) {
                  var9 = var19.todouble();
               }
               break;
            case 4:
               if (var19.isnumber()) {
                  var11 = var19.todouble();
               }
               break;
            case 5:
               if (var19.isnumber()) {
                  var13 = var19.toint();
               }
               break;
            case 6:
               if (var19.isnumber()) {
                  var14 = var19.toint();
               }
               break;
            case 7:
               if (var19.isnumber()) {
                  var15 = var19.toint();
               }
               break;
            case 8:
               if (var19.isnumber()) {
                  var16 = var19.toint();
               }
         }
      }

      RenderUtil.j(var5, var7, var9, var11, var13, var14, var15, var16);
      return valueOf(true);
   }
}
