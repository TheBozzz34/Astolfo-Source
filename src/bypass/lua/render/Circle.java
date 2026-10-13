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
   public Varargs invoke(Varargs var1) {
      double var5 = 0.0;
      double var7 = 0.0;
      int var9 = 0;
      int var10 = 0;
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
                  var5 = var19.checkdouble();
               }
               break;
            case 2:
               if (var19.isnumber()) {
                  var7 = var19.checkdouble();
               }
               break;
            case 3:
               if (var19.isnumber()) {
                  var9 = var19.toint();
               }
               break;
            case 4:
               if (var19.isnumber()) {
                  var10 = var19.toint();
               }
               break;
            case 5:
               if (var19.isnumber()) {
                  var11 = var19.checkdouble();
               }
               break;
            case 6:
               if (var19.isnumber()) {
                  var13 = var19.toint();
               }
               break;
            case 7:
               if (var19.isnumber()) {
                  var14 = var19.toint();
               }
               break;
            case 8:
               if (var19.isnumber()) {
                  var15 = var19.toint();
               }
               break;
            case 9:
               if (var19.isnumber()) {
                  var16 = var19.toint();
               }
         }
      }

      RenderUtil.drawArc(var5, var7, var9, var10, var11, new Color(var13, var14, var15, var16).getRGB());
      return valueOf(true);
   }
}
