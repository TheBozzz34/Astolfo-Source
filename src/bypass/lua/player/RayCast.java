package bypass.lua.player;

import bypass.util.GameUtil;
import net.minecraft.util.MovingObjectPosition;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class RayCast extends VarArgFunction {
   @Override
   public Varargs onInvoke(Varargs varargs) {
      if (varargs.narg() < 2) {
         return LuaValue.varargsOf(new LuaValue[]{NIL});
      }

      float f = 0.0F;
      float g = 0.0F;
      double d = 4.5;
      int i = 1;

      for (int j = varargs.narg(); i <= j; i++) {
         LuaValue luaValue = varargs.checkvalue(i);
         switch (i) {
            case 1:
               if (luaValue.isnumber()) {
                  f = luaValue.tofloat();
               }
               break;
            case 2:
               if (luaValue.isnumber()) {
                  g = luaValue.tofloat();
               }
               break;
            case 3:
               if (luaValue.isnumber()) {
                  d = luaValue.checkdouble();
               }
         }
      }

      MovingObjectPosition movingObjectPosition = GameUtil.A(f, g, d);
      if (movingObjectPosition != null) {
         switch (MovingObjectTypeSwitch.$SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType[movingObjectPosition.typeOfHit.ordinal()]) {
            case 1:
               return LuaValue.varargsOf(new LuaValue[]{valueOf(movingObjectPosition.typeOfHit.ordinal() + 1), valueOf(movingObjectPosition.entityHit.getEntityId())});
            case 2:
               return LuaValue.varargsOf(new LuaValue[]{valueOf(movingObjectPosition.typeOfHit.ordinal() + 1)});
            case 3:
               return LuaValue.varargsOf(
                  new LuaValue[]{
                     valueOf(movingObjectPosition.typeOfHit.ordinal() + 1),
                     valueOf(movingObjectPosition.sideHit.ordinal() + 1),
                     valueOf(movingObjectPosition.getBlockPos().getX()),
                     valueOf(movingObjectPosition.getBlockPos().getY()),
                     valueOf(movingObjectPosition.getBlockPos().getZ()),
                     valueOf(movingObjectPosition.hitVec.xCoord),
                     valueOf(movingObjectPosition.hitVec.yCoord),
                     valueOf(movingObjectPosition.hitVec.zCoord)
                  }
               );
            default:
               return LuaValue.varargsOf(new LuaValue[]{valueOf(movingObjectPosition.typeOfHit.ordinal() + 1)});
         }
      } else {
         return LuaValue.varargsOf(new LuaValue[]{NIL});
      }
   }
}
