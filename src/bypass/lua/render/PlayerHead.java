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
   public Varargs invoke(Varargs var1) {
      int var5 = 0;
      int var6 = 0;
      int var7 = 8;
      int var8 = -1;
      int var9 = 1;

      for (int var10 = var1.narg(); var9 <= var10; var9++) {
         LuaValue var11 = var1.checkvalue(var9);
         switch (var9) {
            case 1:
               if (var11.isnumber()) {
                  var5 = var11.checkint();
               }
               break;
            case 2:
               if (var11.isnumber()) {
                  var6 = var11.checkint();
               }
               break;
            case 3:
               if (var11.isnumber()) {
                  var7 = var11.checkint();
               }
               break;
            case 4:
               if (var11.isnumber()) {
                  var8 = var11.checkint();
               }
         }
      }

      Entity var12 = WorldLibrary.X.get(var8);
      if (var12 instanceof EntityPlayer) {
         AbstractClientPlayer var13 = (AbstractClientPlayer)var12;
         ClientAccess.mc.getTextureManager().bindTexture(var13.getPlayerInfo().c());
         Gui.drawScaledCustomSizeModalRect(var5, var6, 8.0F, 8.0F, 8, 8, var7, var7, 64.0F, 64.0F);
         return valueOf(true);
      } else {
         return valueOf(false);
      }
   }
}
