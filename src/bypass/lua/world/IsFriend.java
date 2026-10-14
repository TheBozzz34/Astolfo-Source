package bypass.lua.world;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class IsFriend extends OneArgFunction {
   private static final long a = 23998462244357L;

   @Override
   public LuaValue call(LuaValue luaValue) {
      long l = a ^ 56081814171565L;
      long m = l ^ 96333608762613L;
      int i = (int)((l ^ 96333608762613L) >>> 56);
      int j = (int)((l ^ 96333608762613L) << 8 >>> 32);
      int k = (int)(m << 40 >>> 40);
      if (!luaValue.isnil() && luaValue.isnumber() && luaValue.isint()) {
         Entity entity = WorldLibrary.X.get(luaValue.checkint());
         if (entity instanceof EntityLivingBase) {
            return valueOf(ClientAccess.client.getRelationManager().z(entity));
         }
      }

      return valueOf(false);
   }
}
