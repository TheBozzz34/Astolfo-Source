package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class TicksExisted extends OneArgFunction {
   private static final long a = 23632937331599L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!luaValue.isnil() && luaValue.isnumber() && luaValue.isint()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity != null) {
            return valueOf(entity.ticksExisted);
         }
      }

      return NIL;
   }
}
