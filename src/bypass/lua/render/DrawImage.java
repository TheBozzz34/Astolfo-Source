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
   public Varargs invoke(Varargs varargs) {
      if (!ScriptManager.N.t()) {
         return valueOf(false);
      }

      String string = "";
      float f = 0.0F;
      float g = 0.0F;
      int i = 0;
      int j = 0;
      boolean bl = false;
      int k = 1;
      if (varargs.narg() < 5 && varargs.narg() > (int)b) {
         return valueOf(false);
      }

      for (int l = varargs.narg(); k <= l; k++) {
         LuaValue luaValue = varargs.checkvalue(k);
         switch (k) {
            case 1:
               if (luaValue.isstring()) {
                  string = luaValue.checkjstring();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  f = luaValue.tofloat();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  g = luaValue.tofloat();
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  i = luaValue.toint();
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  j = luaValue.toint();
               }
               break;
            case 6:
               if (luaValue.checkboolean()) {
                  bl = luaValue.checkboolean();
               }
         }
      }

      ClientAccess.mc.getTextureManager().bindTexture(RenderLibrary.m(string, bl));
      Gui.drawModalRectWithCustomSizedTexture((int)f, (int)g, 0.0F, 0.0F, i, j, i, j);
      return valueOf(true);
   }
}
