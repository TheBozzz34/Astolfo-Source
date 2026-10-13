package bypass.lua.render;

import bypass.ClientAccess;
import bypass.lua.RenderLibrary;
import bypass.script.ScriptManager;
import net.minecraft.client.gui.Gui;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class DrawImage extends VarArgFunction {
   private static final long a = 22279957277211L;
   private static final long b = 5467930788752261126L;

   @Override
   public Varargs invoke(Varargs var1) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      String var5 = "";
      float var6 = 0.0F;
      float var7 = 0.0F;
      int var8 = 0;
      int var9 = 0;
      boolean var10 = false;
      int var11 = 1;
      if (var1.narg() < 5 && var1.narg() > (int)b) {
         return valueOf(false);
      }

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
                  var6 = var13.tofloat();
               }
               break;
            case 3:
               if (var13.isnumber()) {
                  var7 = var13.tofloat();
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
               if (var13.checkboolean()) {
                  var10 = var13.checkboolean();
               }
         }
      }

      ClientAccess.mc.getTextureManager().bindTexture(RenderLibrary.m(var5, var10));
      Gui.drawModalRectWithCustomSizedTexture((int)var6, (int)var7, 0.0F, 0.0F, var8, var9, var8, var9);
      return valueOf(true);
   }
}
