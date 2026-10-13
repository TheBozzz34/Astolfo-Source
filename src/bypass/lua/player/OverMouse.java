package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class OverMouse extends VarArgFunction {
   private static final long a = 34921107760939L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      if (ClientAccess.mc.objectMouseOver == null) {
         return LuaValue.varargsOf(new LuaValue[]{NIL});
      }

      switch (MovingObjectTypeSwitch.$SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType[ClientAccess.mc.objectMouseOver.typeOfHit.ordinal()]) {
         case 1:
            return LuaValue.varargsOf(
               new LuaValue[]{
                  valueOf(ClientAccess.mc.objectMouseOver.typeOfHit.ordinal() + 1), valueOf(ClientAccess.mc.objectMouseOver.entityHit.getEntityId())
               }
            );
         case 2:
            return LuaValue.varargsOf(new LuaValue[]{valueOf(ClientAccess.mc.objectMouseOver.typeOfHit.ordinal() + 1)});
         case 3:
            return LuaValue.varargsOf(
               new LuaValue[]{
                  valueOf(ClientAccess.mc.objectMouseOver.typeOfHit.ordinal() + 1),
                  valueOf(ClientAccess.mc.objectMouseOver.sideHit.ordinal() + 1),
                  valueOf(ClientAccess.mc.objectMouseOver.getBlockPos().getX()),
                  valueOf(ClientAccess.mc.objectMouseOver.getBlockPos().getY()),
                  valueOf(ClientAccess.mc.objectMouseOver.getBlockPos().getZ()),
                  valueOf(ClientAccess.mc.objectMouseOver.hitVec.xCoord),
                  valueOf(ClientAccess.mc.objectMouseOver.hitVec.yCoord),
                  valueOf(ClientAccess.mc.objectMouseOver.hitVec.zCoord)
               }
            );
         default:
            return LuaValue.varargsOf(new LuaValue[]{valueOf(ClientAccess.mc.objectMouseOver.typeOfHit.ordinal() + 1)});
      }
   }
}
