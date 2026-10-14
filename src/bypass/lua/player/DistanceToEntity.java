package bypass.lua.player;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class DistanceToEntity extends OneArgFunction {
   private static final long a = 135844402878793L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (ClientAccess.mc.thePlayer != null && luaValue.isint()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity != null) {
            return valueOf(ClientAccess.mc.thePlayer.getDistanceToEntity(entity));
         }
      }

      return valueOf(false);
   }
}
