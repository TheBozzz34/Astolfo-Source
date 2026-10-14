package bypass.lua.player;

import bypass.ClientAccess;
import bypass.util.InventoryUtil;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class ItemInformation extends VarArgFunction {
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

                  return LuaValue.varargsOf(
                     new LuaValue[]{
                        LuaValue.valueOf(itemStack.getUnlocalizedName()),
                        LuaValue.valueOf(itemStack.getDisplayName()),
                        LuaValue.valueOf(InventoryUtil.getDamageScore(itemStack)),
                        LuaValue.valueOf(InventoryUtil.getToolScore(itemStack)),
                        luaTable
                     }
                  );
               }
            } else {
               net.minecraft.inventory.Slot slot2 = ClientAccess.mc.thePlayer.inventoryContainer.getSlot(luaValue.checkint());
               if (slot2 != null && slot2.getHasStack()) {
                  ItemStack itemStack2 = slot2.getStack();
                  LuaTable luaTable2 = new LuaTable();
                  Map map2 = EnchantmentHelper.getEnchantments(itemStack2);

                  for (Object object2 : map2.entrySet()) {
                     Entry entry2 = (Entry)object2;
                     luaTable2.set(String.valueOf(entry2.getKey()), (Integer)entry2.getValue());
                  }

                  return LuaValue.varargsOf(
                     new LuaValue[]{
                        LuaValue.valueOf(itemStack2.getUnlocalizedName()),
                        LuaValue.valueOf(itemStack2.getDisplayName()),
                        LuaValue.valueOf(InventoryUtil.getDamageScore(itemStack2)),
                        LuaValue.valueOf(InventoryUtil.getToolScore(itemStack2)),
                        luaTable2
                     }
                  );
               }
            }
         }
      }

      return NIL;
   }
}
