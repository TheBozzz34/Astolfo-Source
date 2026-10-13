package bypass.lua.world;

import bypass.lua.WorldLibrary;
import bypass.module.hidden.HypixelGameTracker;
import bypass.module.hidden.PlayerPotionEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public final class IsPotionActive extends TwoArgFunction {
   private static final long a = 33455840051243L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      if (!var1.isnil() && var1.isnumber() && var1.isint() && !var2.isnil() && var2.isnumber() && var2.isint()) {
         Entity var6 = WorldLibrary.X.get(var1.checkint());
         if (var6 instanceof EntityPlayer) {
            PlayerPotionEffects var7 = HypixelGameTracker.playerEffects.get((EntityPlayer)var6);
            if (var7 != null) {
               return valueOf(var7.effects.get(var2.checkint()) != null);
            }
         }
      }

      return NIL;
   }
}
