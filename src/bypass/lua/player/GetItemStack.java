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
   @Override
   public Varargs onInvoke(Varargs varargs) {
      if (ClientAccess.mc.thePlayer != null) {
         LuaValue luaValue = varargs.arg1();
         if (luaValue.isnumber()) {
            if (ClientAccess.mc.thePlayer.openContainer != null) {
               net.minecraft.inventory.Slot slot = ClientAccess.mc.thePlayer.openContainer.getSlot(luaValue.checkint());
               if (slot != null && slot.getHasStack()) {
                  ItemStack itemStack = slot.getStack();
                  LuaTable luaTable = new LuaTable();
                  Map map = EnchantmentHelper.getEnchantments(itemStack);

                  for (Object object : map.entrySet()) {
                     Entry entry = (Entry)object;
                     luaTable.set(String.valueOf(entry.getKey()), (Integer)entry.getValue());
                  }

                  return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(itemStack.stackSize)});
               }
            } else {
               net.minecraft.inventory.Slot slot2 = ClientAccess.mc.thePlayer.inventoryContainer.getSlot(luaValue.checkint());
               if (slot2 != null && slot2.getHasStack()) {
                  ItemStack itemStack2 = slot2.getStack();
                  return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(itemStack2.stackSize)});
               }
            }
         }
      }

      return NIL;
   }
}
