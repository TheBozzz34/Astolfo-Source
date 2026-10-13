package bypass.lua.player;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class DistanceToEntity extends OneArgFunction {
   private static final long a = 135844402878793L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (ClientAccess.mc.thePlayer != null && var1.isint()) {
         Entity var5 = WorldLibrary.X.get(var1.checkint());
         if (var5 != null) {
            return valueOf(ClientAccess.mc.thePlayer.getDistanceToEntity(var5));
         }
      }

      return valueOf(false);
   }
}
