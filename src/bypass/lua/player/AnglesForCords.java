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
   private static final long a = 65882888098918L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      if (var1.narg() == 3) {
         Vector3d var5 = new Vector3d(
            ClientAccess.mc.thePlayer.posX, ClientAccess.mc.thePlayer.posY + ClientAccess.mc.thePlayer.getEyeHeight(), ClientAccess.mc.thePlayer.posZ
         );
         double var6 = 0.0;
         double var8 = 0.0;
         double var10 = 0.0;
         int var12 = 1;

         for (int var13 = var1.narg(); var12 <= var13; var12++) {
            LuaValue var14 = var1.checkvalue(var12);
            switch (var12) {
               case 1:
                  if (var14.isnumber()) {
                     var6 = var14.checkdouble();
                  }
                  break;
               case 2:
                  if (var14.isnumber()) {
                     var8 = var14.checkdouble();
                  }
                  break;
               case 3:
                  if (var14.isnumber()) {
                     var10 = var14.checkdouble();
                  }
            }
         }

         Vector3d var17 = new Vector3d(var6, var8, var10);
         Rotation var18 = PlayerLibrary.K().rotationTo(var17, var5);
         float var15 = MathHelper.wrapAngleTo180_float(var18.getYaw() - ClientAccess.mc.thePlayer.rotationYaw);
         float var16 = ClientAccess.mc.thePlayer.rotationYaw + var15;
         return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(var16), LuaValue.valueOf(var18.getPitch())});
      } else {
         return NIL;
      }
   }
}
