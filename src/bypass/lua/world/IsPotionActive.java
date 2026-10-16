package bypass.lua.world;

import bypass.lua.WorldLibrary;
import bypass.module.hidden.HypixelGameTracker;
import bypass.module.hidden.PlayerPotionEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class IsPotionActive extends TwoArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      if (!luaValue.isnil() && luaValue.isnumber() && luaValue.isint() && !luaValue2.isnil() && luaValue2.isnumber() && luaValue2.isint()) {
         Entity entity = WorldLibrary.entitiesById.get(luaValue.checkint());
         if (entity instanceof EntityPlayer) {
            PlayerPotionEffects playerPotionEffects = HypixelGameTracker.playerEffects.get((EntityPlayer)entity);
            if (playerPotionEffects != null) {
               return valueOf(playerPotionEffects.effects.get(luaValue2.checkint()) != null);
            }
         }
      }

      return NIL;
   }
}
