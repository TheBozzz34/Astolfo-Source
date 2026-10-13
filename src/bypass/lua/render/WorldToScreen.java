package bypass.lua.render;

import bypass.module.hidden.PlayerListTracker;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class WorldToScreen extends VarArgFunction {
   private static final long a = 82273072128268L;

   @Override
   public Varargs invoke(Varargs var1) {
      float var5 = 0.0F;
      float var6 = 0.0F;
      float var7 = 0.0F;
      if (var1.narg() < 3) {
         return valueOf(false);
      }

      int var8 = 1;

      for (int var9 = var1.narg(); var8 <= var9; var8++) {
         LuaValue var10 = var1.checkvalue(var8);
         switch (var8) {
            case 1:
               if (var10.isnumber()) {
                  var5 = var10.tofloat();
               }
               break;
            case 2:
               if (var10.isnumber()) {
                  var6 = var10.tofloat();
               }
               break;
            case 3:
               if (var10.isnumber()) {
                  var7 = var10.tofloat();
               }
         }
      }

      float[] var11 = PlayerListTracker.project(var5, var6, var7);
      return var11 != null ? LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(var11[0]), LuaValue.valueOf(var11[1]), LuaValue.valueOf(var11[2])}) : NIL;
   }
}
