package bypass.lua.player;

import bypass.ClientAccess;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class GetItemStack extends VarArgFunction {
   private static final long a = 86032223164463L;

   @Override
   public Varargs onInvoke(Varargs var1) {
      if (ClientAccess.mc.thePlayer != null) {
         LuaValue var5 = var1.arg1();
         if (var5.isnumber()) {
            if (ClientAccess.mc.thePlayer.openContainer != null) {
               net.minecraft.inventory.Slot var6 = ClientAccess.mc.thePlayer.openContainer.getSlot(var5.checkint());
               if (var6 != null && var6.getHasStack()) {
                  ItemStack var7 = var6.getStack();
                  LuaTable var8 = new LuaTable();
                  Map var9 = EnchantmentHelper.getEnchantments(var7);

                  for (Object var11$raw : var9.entrySet()) {
                     Entry var11 = (Entry)var11$raw;
                     var8.set(String.valueOf(var11.getKey()), (Integer)var11.getValue());
                  }

                  return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(var7.stackSize)});
               }
            } else {
               net.minecraft.inventory.Slot var12 = ClientAccess.mc.thePlayer.inventoryContainer.getSlot(var5.checkint());
               if (var12 != null && var12.getHasStack()) {
                  ItemStack var13 = var12.getStack();
                  return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(var13.stackSize)});
               }
            }
         }
      }

      return NIL;
   }
}
