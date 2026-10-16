package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsPlayer extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!luaValue.isnil() && luaValue.isnumber() && luaValue.isint()) {
         Entity entity = WorldLibrary.entitiesById.get(luaValue.checkint());
         if (entity != null) {
            return valueOf(entity instanceof EntityPlayer);
         }
      }

      return NIL;
   }
}
