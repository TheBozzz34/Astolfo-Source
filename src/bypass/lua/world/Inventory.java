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
   public Varargs invoke(Varargs varargs) {
      if (varargs.arg1() != null && !varargs.arg1().isnil() && varargs.arg1().isnumber()) {
         Entity entity = WorldLibrary.X.get(varargs.arg1().checkint());
         if (entity != null && entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            LuaTable luaTable = new LuaTable();
            ItemStack itemStack = entityLivingBase.getHeldItem();
            luaTable.set("hand", itemStack == null ? "none" : itemStack.getUnlocalizedName());
            ItemStack[] itemStacks = entityLivingBase.getInventory();
            if (itemStacks != null) {
               int i = 0;

               for (int j = itemStacks.length; i < j; i++) {
                  ItemStack itemStack2 = itemStacks[i];
                  if (itemStack2 != null) {
                     int k = EntityLiving.getArmorPosition(itemStack2);
                     switch (k) {
                        case 1:
                           luaTable.set("boots", itemStack2.getUnlocalizedName());
                           break;
                        case 2:
                           luaTable.set("leggings", itemStack2.getUnlocalizedName());
                           break;
                        case 3:
                           luaTable.set("chestplate", itemStack2.getUnlocalizedName());
                           break;
                        case 4:
                           luaTable.set("helmet", itemStack2.getUnlocalizedName());
                     }
                  }
               }

               return LuaValue.varargsOf(new LuaValue[]{luaTable});
            }
         }
      }

      return NIL;
   }
}
