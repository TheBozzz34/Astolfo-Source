package bypass.lua.player;

import bypass.lua.PlayerLibrary;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class ConvertSpeed extends TwoArgFunction {
   private static final long a = 31235114793141L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      if (var1 != null && !var1.isnil() && var1.istable() && var2 != null && !var2.isnil() && var2.isnumber()) {
         double[] var6 = PlayerLibrary.U(var2.tofloat());
         if (var6 != null) {
            LuaValue var7 = var1.get("x");
            if (!var7.isnil() && var7.isnumber()) {
               var1.set("x", var6[0]);
            }

            LuaValue var8 = var1.get("z");
            if (!var8.isnil() && var8.isnumber()) {
               var1.set("z", var6[1]);
            }

            return var1;
         }
      }

      return NIL;
   }
}
