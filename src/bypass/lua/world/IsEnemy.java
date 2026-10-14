package bypass.lua.world;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsEnemy extends OneArgFunction {
   private static final long a = 11554342307059L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      long l = a ^ 19865010246312L;
      long m = l ^ 116395548553990L;
      int i = (int)((l ^ 116395548553990L) >>> 56);
      int j = (int)((l ^ 116395548553990L) << 8 >>> 32);
      int k = (int)(m << 40 >>> 40);
      if (!luaValue.isnil() && luaValue.isnumber() && luaValue.isint()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity instanceof EntityLivingBase) {
            return valueOf(ClientAccess.client.getRelationManager().D(entity));
         }
      }

      return valueOf(false);
   }
}
