package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class MaxHealth extends OneArgFunction {
   private static final long a = 129865163244450L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      if (!luaValue.isnil() && luaValue.isnumber() && luaValue.isint()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            return valueOf(entityLivingBase.getMaxHealth());
         }
      }

      return NIL;
   }
}
