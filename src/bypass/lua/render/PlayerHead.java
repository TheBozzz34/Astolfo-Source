package bypass.lua.render;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class PlayerHead extends VarArgFunction {
   private static final long a = 115125512759867L;

   @Override
   public Varargs invoke(Varargs varargs) {
      int i = 0;
      int j = 0;
      int k = 8;
      int l = -1;
      int m = 1;

      for (int n = varargs.narg(); m <= n; m++) {
         LuaValue luaValue = varargs.checkvalue(m);
         switch (m) {
            case 1:
               if (luaValue.isnumber()) {
                  i = luaValue.checkint();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  j = luaValue.checkint();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  k = luaValue.checkint();
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  l = luaValue.checkint();
               }
         }
      }

      Entity entity = WorldLibrary.X.get(l);
      if (entity instanceof EntityPlayer) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entity;
         ClientAccess.mc.getTextureManager().bindTexture(abstractClientPlayer.getPlayerInfo().c());
         Gui.drawScaledCustomSizeModalRect(i, j, 8.0F, 8.0F, 8, 8, k, k, 64.0F, 64.0F);
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
