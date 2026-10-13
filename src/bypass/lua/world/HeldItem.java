package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class HeldItem extends OneArgFunction {
   private static final long a = 94856513588092L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (!var1.isnil() && var1.isnumber() && var1.isint()) {
         Entity var5 = WorldLibrary.X.get(var1.checkint());
         if (var5 instanceof EntityLivingBase) {
            EntityLivingBase var6 = (EntityLivingBase)var5;
            ItemStack var7 = var6.getHeldItem();
            if (var7 != null) {
               return valueOf(var6.getHeldItem().getDisplayName());
            }

            return valueOf("hand");
         }
      }

      return NIL;
   }
}
