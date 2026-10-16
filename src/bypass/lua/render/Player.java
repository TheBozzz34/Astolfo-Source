package bypass.lua.render;

import bypass.lua.WorldLibrary;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Player extends VarArgFunction {
   @Override
   public Varargs invoke(Varargs varargs) {
      int i = 0;
      int j = 0;
      int k = 8;
      int l = -1;
      float f = 0.0F;
      float h = 0.0F;
      int m = 1;
      if (varargs.narg() < 6) {
         return valueOf(false);
      }

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
                  f = luaValue.tofloat();
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  h = luaValue.tofloat();
               }
               break;
            case 6:
               if (luaValue.isnumber()) {
                  l = luaValue.checkint();
               }
         }
      }

      Entity entity = WorldLibrary.entitiesById.get(l);
      if (entity instanceof EntityLivingBase) {
         GuiInventory.g(i, j, f, h, k, (EntityLivingBase)entity);
      }

      return valueOf(false);
   }
}
