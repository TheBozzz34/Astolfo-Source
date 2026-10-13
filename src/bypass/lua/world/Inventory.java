package bypass.lua.world;

import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class Inventory extends VarArgFunction {
   private static final long a = 74251697092318L;

   @Override
   public Varargs invoke(Varargs var1) {
      if (var1.arg1() != null && !var1.arg1().isnil() && var1.arg1().isnumber()) {
         Entity var5 = WorldLibrary.X.get(var1.arg1().checkint());
         if (var5 != null && var5 instanceof EntityLivingBase) {
            EntityLivingBase var6 = (EntityLivingBase)var5;
            LuaTable var7 = new LuaTable();
            ItemStack var8 = var6.getHeldItem();
            var7.set("hand", var8 == null ? "none" : var8.getUnlocalizedName());
            ItemStack[] var9 = var6.getInventory();
            if (var9 != null) {
               int var10 = 0;

               for (int var11 = var9.length; var10 < var11; var10++) {
                  ItemStack var12 = var9[var10];
                  if (var12 != null) {
                     int var13 = EntityLiving.getArmorPosition(var12);
                     switch (var13) {
                        case 1:
                           var7.set("boots", var12.getUnlocalizedName());
                           break;
                        case 2:
                           var7.set("leggings", var12.getUnlocalizedName());
                           break;
                        case 3:
                           var7.set("chestplate", var12.getUnlocalizedName());
                           break;
                        case 4:
                           var7.set("helmet", var12.getUnlocalizedName());
                     }
                  }
               }

               return LuaValue.varargsOf(new LuaValue[]{var7});
            }
         }
      }

      return NIL;
   }
}
