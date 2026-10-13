package bypass.lua.player;

import bypass.ClientAccess;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class PlaceBlock extends VarArgFunction {
   private static final long a = 118458972782194L;
   private static final long b = 3340367993640058888L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      if (var1.narg() < (int)b) {
         return LuaValue.varargsOf(new LuaValue[]{NIL});
      }

      int var5 = 0;
      int var6 = 0;
      double var7 = 0.0;
      double var9 = 0.0;
      double var11 = 0.0;
      double var13 = 0.0;
      double var15 = 0.0;
      double var17 = 0.0;
      int var19 = 1;

      for (int var20 = var1.narg(); var19 <= var20; var19++) {
         LuaValue var21 = var1.checkvalue(var19);
         switch (var19) {
            case 1:
               if (var21.isnumber()) {
                  var5 = var21.checkint();
               }
               break;
            case 2:
               if (var21.isnumber()) {
                  var7 = var21.checkdouble();
               }
               break;
            case 3:
               if (var21.isnumber()) {
                  var9 = var21.checkdouble();
               }
               break;
            case 4:
               if (var21.isnumber()) {
                  var11 = var21.checkdouble();
               }
               break;
            case 5:
               if (var21.isnumber()) {
                  var6 = var21.checkint();
               }
               break;
            case 6:
               if (var21.isnumber()) {
                  var13 = var21.checkint();
               }
               break;
            case 7:
               if (var21.isnumber()) {
                  var15 = var21.checkint();
               }
               break;
            case 8:
               if (var21.isnumber()) {
                  var17 = var21.checkint();
               }
         }
      }

      boolean var22 = ClientAccess.mc
         .playerController
         .onPlayerRightClick(
            ClientAccess.mc.thePlayer,
            ClientAccess.mc.theWorld,
            ClientAccess.mc.thePlayer.inventory.getStackInSlot(var5 - 1),
            new BlockPos(var7, var9, var11),
            EnumFacing.values()[var6 - 1],
            new Vec3(var13, var15, var17)
         );
      return valueOf(var22);
   }
}
