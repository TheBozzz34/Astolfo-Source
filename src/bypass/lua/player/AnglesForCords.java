package bypass.lua.player;

import bypass.ClientAccess;
import bypass.lua.PlayerLibrary;
import bypass.util.Rotation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vector3d;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class AnglesForCords extends VarArgFunction {
   @Override
   public Varargs onInvoke(Varargs varargs) {
      if (varargs.narg() == 3) {
         Vector3d vector3d = new Vector3d(
            ClientAccess.mc.thePlayer.posX, ClientAccess.mc.thePlayer.posY + ClientAccess.mc.thePlayer.getEyeHeight(), ClientAccess.mc.thePlayer.posZ
         );
         double d = 0.0;
         double e = 0.0;
         double d2 = 0.0;
         int i = 1;

         for (int j = varargs.narg(); i <= j; i++) {
            LuaValue luaValue = varargs.checkvalue(i);
            switch (i) {
               case 1:
                  if (luaValue.isnumber()) {
                     d = luaValue.checkdouble();
                  }
                  break;
               case 2:
                  if (luaValue.isnumber()) {
                     e = luaValue.checkdouble();
                  }
                  break;
               case 3:
                  if (luaValue.isnumber()) {
                     d2 = luaValue.checkdouble();
                  }
            }
         }

         Vector3d vector3d2 = new Vector3d(d, e, d2);
         Rotation rotation = PlayerLibrary.K().rotationTo(vector3d2, vector3d);
         float f = MathHelper.wrapAngleTo180_float(rotation.getYaw() - ClientAccess.mc.thePlayer.rotationYaw);
         float g = ClientAccess.mc.thePlayer.rotationYaw + f;
         return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(g), LuaValue.valueOf(rotation.getPitch())});
      } else {
         return NIL;
      }
   }
}
