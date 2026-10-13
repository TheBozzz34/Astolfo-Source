package bypass.lua.world;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Remove extends OneArgFunction {
   private static final long a = 101475076665625L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isnil() && var1.isnumber() && var1.isint()) {
         Entity var5 = WorldLibrary.X.get(var1.checkint());
         if (var5 != null) {
            ClientAccess.mc.theWorld.removeEntity(var5);
            return valueOf(true);
         }
      }

      return valueOf(false);
   }
}
