package bypass.lua.world;

import bypass.lua.WorldLibrary;
import bypass.module.hidden.BotDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsBot extends OneArgFunction {
   private static final long a = 35785352051806L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isnil() && var1.isnumber() && var1.isint()) {
         Entity var5 = WorldLibrary.X.get(var1.checkint());
         if (var5 instanceof EntityLivingBase) {
            return valueOf(BotDetector.w.contains(var5));
         }
      }

      return NIL;
   }
}
