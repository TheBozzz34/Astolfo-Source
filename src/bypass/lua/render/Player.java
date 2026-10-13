package bypass.lua.render;

import bypass.lua.WorldLibrary;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Player extends VarArgFunction {
   private static final long a = 18062544119377L;

   @Override
   public Varargs invoke(Varargs var1) {
      int var5 = 0;
      int var6 = 0;
      int var7 = 8;
      int var8 = -1;
      float var9 = 0.0F;
      float var10 = 0.0F;
      int var11 = 1;
      if (var1.narg() < 6) {
         return valueOf(false);
      }

      for (int var12 = var1.narg(); var11 <= var12; var11++) {
         LuaValue var13 = var1.checkvalue(var11);
         switch (var11) {
            case 1:
               if (var13.isnumber()) {
                  var5 = var13.checkint();
               }
               break;
            case 2:
               if (var13.isnumber()) {
                  var6 = var13.checkint();
               }
               break;
            case 3:
               if (var13.isnumber()) {
                  var7 = var13.checkint();
               }
               break;
            case 4:
               if (var13.isnumber()) {
                  var9 = var13.tofloat();
               }
               break;
            case 5:
               if (var13.isnumber()) {
                  var10 = var13.tofloat();
               }
               break;
            case 6:
               if (var13.isnumber()) {
                  var8 = var13.checkint();
               }
         }
      }

      Entity var14 = WorldLibrary.X.get(var8);
      if (var14 instanceof EntityLivingBase) {
         GuiInventory.g(var5, var6, var9, var10, var7, (EntityLivingBase)var14);
      }

      return valueOf(false);
   }
}
