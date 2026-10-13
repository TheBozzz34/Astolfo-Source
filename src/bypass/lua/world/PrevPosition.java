package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class PrevPosition extends VarArgFunction {
   private static final long a = 66802772767264L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      LuaValue var5 = var1.arg1();
      if (var5 != null && !var5.isnil() && var5.isnumber()) {
         Entity var6 = WorldLibrary.X.get(var5.checkint());
         if (var6 != null) {
            return LuaValue.varargsOf(
               new LuaValue[]{LuaValue.valueOf(var6.lastTickPosX), LuaValue.valueOf(var6.lastTickPosY), LuaValue.valueOf(var6.lastTickPosZ)}
            );
         }
      }

      return NIL;
   }
}
