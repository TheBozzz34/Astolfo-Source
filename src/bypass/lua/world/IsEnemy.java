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
   public LuaValue call(LuaValue var1) {
      long var2 = a ^ 19865010246312L;
      long var10001 = var2 ^ 116395548553990L;
      int var4 = (int)((var2 ^ 116395548553990L) >>> 56);
      int var5 = (int)((var2 ^ 116395548553990L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (!var1.isnil() && var1.isnumber() && var1.isint()) {
         Entity var8 = WorldLibrary.X.get(var1.checkint());
         if (var8 instanceof EntityLivingBase) {
            return valueOf(ClientAccess.client.getRelationManager().D(var8));
         }
      }

      return valueOf(false);
   }
}
