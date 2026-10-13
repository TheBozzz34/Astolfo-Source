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
   public Varargs invoke(Varargs var1) {
      float var5 = 0.0F;
      float var6 = 0.0F;
      float var7 = 0.0F;
      float var8 = 0.0F;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;
      float var13 = 1.0F;
      int var14 = 1;
      if (var1.narg() < (int)b) {
         return valueOf(false);
      }

      for (int var15 = var1.narg(); var14 <= var15; var14++) {
         LuaValue var16 = var1.checkvalue(var14);
         switch (var14) {
            case 1:
               if (var16.isnumber()) {
                  var5 = var16.tofloat();
               }
               break;
            case 2:
               if (var16.isnumber()) {
                  var6 = var16.tofloat();
               }
               break;
            case 3:
               if (var16.isnumber()) {
                  var7 = var16.tofloat();
               }
               break;
            case 4:
               if (var16.isnumber()) {
                  var8 = var16.tofloat();
               }
               break;
            case 5:
               if (var16.isnumber()) {
                  var13 = var16.tofloat();
               }
               break;
            case 6:
               if (var16.isnumber()) {
                  var9 = var16.toint();
               }
               break;
            case 7:
               if (var16.isnumber()) {
                  var10 = var16.toint();
               }
               break;
            case 8:
               if (var16.isnumber()) {
                  var11 = var16.toint();
               }
               break;
            case 9:
               if (var16.isnumber()) {
                  var12 = var16.toint();
               }
         }
      }

      RenderUtil.drawLine(var13, var5, var6, var7, var8, new Color(var9, var10, var11, var12).getRGB());
      return valueOf(true);
   }
}
