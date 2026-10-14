package bypass.lua.player;

import bypass.ClientAccess;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class PlaceBlock extends VarArgFunction {
   @Override
   public Varargs onInvoke(Varargs varargs) {
      if (varargs.narg() < 8) {
         return LuaValue.varargsOf(new LuaValue[]{NIL});
      }

      int i = 0;
      int j = 0;
      double d = 0.0;
      double e = 0.0;
      double d2 = 0.0;
      double d3 = 0.0;
      double d4 = 0.0;
      double d5 = 0.0;
      int k = 1;

      for (int l = varargs.narg(); k <= l; k++) {
         LuaValue luaValue = varargs.checkvalue(k);
         switch (k) {
            case 1:
               if (luaValue.isnumber()) {
                  i = luaValue.checkint();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  d = luaValue.checkdouble();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  e = luaValue.checkdouble();
               }
               break;
            case 4:
               if (luaValue.isnumber()) {
                  d2 = luaValue.checkdouble();
               }
               break;
            case 5:
               if (luaValue.isnumber()) {
                  j = luaValue.checkint();
               }
               break;
            case 6:
               if (luaValue.isnumber()) {
                  d3 = luaValue.checkint();
               }
               break;
            case 7:
               if (luaValue.isnumber()) {
                  d4 = luaValue.checkint();
               }
               break;
            case 8:
               if (luaValue.isnumber()) {
                  d5 = luaValue.checkint();
               }
         }
      }

      boolean bl = ClientAccess.mc
         .playerController
         .onPlayerRightClick(
            ClientAccess.mc.thePlayer,
            ClientAccess.mc.theWorld,
            ClientAccess.mc.thePlayer.inventory.getStackInSlot(i - 1),
            new BlockPos(d, e, d2),
            EnumFacing.values()[j - 1],
            new Vec3(d3, d4, d5)
         );
      return valueOf(bl);
   }
}
