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
   public LuaValue call(LuaValue var1) {
      long var2 = a ^ 56081814171565L;
      long var10001 = var2 ^ 96333608762613L;
      int var4 = (int)((var2 ^ 96333608762613L) >>> 56);
      int var5 = (int)((var2 ^ 96333608762613L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (!var1.isnil() && var1.isnumber() && var1.isint()) {
         Entity var8 = WorldLibrary.X.get(var1.checkint());
         if (var8 instanceof EntityLivingBase) {
            return valueOf(ClientAccess.client.getRelationManager().z(var8));
         }
      }

      return valueOf(false);
   }
}
