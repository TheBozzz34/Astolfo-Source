package bypass.lua.player;

import bypass.util.GameUtil;
import net.minecraft.util.MovingObjectPosition;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class RayCast extends VarArgFunction {
   private static final long a = 7586746421673L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      if (var1.narg() < 2) {
         return LuaValue.varargsOf(new LuaValue[]{NIL});
      }

      float var5 = 0.0F;
      float var6 = 0.0F;
      double var7 = 4.5;
      int var9 = 1;

      for (int var10 = var1.narg(); var9 <= var10; var9++) {
         LuaValue var11 = var1.checkvalue(var9);
         switch (var9) {
            case 1:
               if (var11.isnumber()) {
                  var5 = var11.tofloat();
               }
               break;
            case 2:
               if (var11.isnumber()) {
                  var6 = var11.tofloat();
               }
               break;
            case 3:
               if (var11.isnumber()) {
                  var7 = var11.checkdouble();
               }
         }
      }

      MovingObjectPosition var12 = GameUtil.A(var5, var6, var7);
      if (var12 != null) {
         switch (MovingObjectTypeSwitch.$SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType[var12.typeOfHit.ordinal()]) {
            case 1:
               return LuaValue.varargsOf(new LuaValue[]{valueOf(var12.typeOfHit.ordinal() + 1), valueOf(var12.entityHit.getEntityId())});
            case 2:
               return LuaValue.varargsOf(new LuaValue[]{valueOf(var12.typeOfHit.ordinal() + 1)});
            case 3:
               return LuaValue.varargsOf(
                  new LuaValue[]{
                     valueOf(var12.typeOfHit.ordinal() + 1),
                     valueOf(var12.sideHit.ordinal() + 1),
                     valueOf(var12.getBlockPos().getX()),
                     valueOf(var12.getBlockPos().getY()),
                     valueOf(var12.getBlockPos().getZ()),
                     valueOf(var12.hitVec.xCoord),
                     valueOf(var12.hitVec.yCoord),
                     valueOf(var12.hitVec.zCoord)
                  }
               );
            default:
               return LuaValue.varargsOf(new LuaValue[]{valueOf(var12.typeOfHit.ordinal() + 1)});
         }
      } else {
         return LuaValue.varargsOf(new LuaValue[]{NIL});
      }
   }
}
