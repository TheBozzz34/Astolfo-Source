package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Sprinting extends OneArgFunction {
   private static final long a = 6781278460007L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isnil() && var1.isnumber() && var1.isint()) {
         Entity var5 = WorldLibrary.X.get(var1.checkint());
         if (var5 instanceof EntityLivingBase) {
            EntityLivingBase var6 = (EntityLivingBase)var5;
            return valueOf(var6.isSprinting());
         }
      }

      return NIL;
   }
}
