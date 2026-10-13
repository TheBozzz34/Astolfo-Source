package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsSneaking extends OneArgFunction {
   private static final long a = 35393747757801L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isnil() && var1.isnumber() && var1.isint()) {
         Entity var5 = WorldLibrary.X.get(var1.checkint());
         if (var5 != null) {
            return valueOf(var5.isSneaking());
         }
      }

      return NIL;
   }
}
