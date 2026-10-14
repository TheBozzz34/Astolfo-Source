package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Slot extends OneArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue) {
      if (ClientAccess.mc.thePlayer != null && luaValue.isnumber()) {
         net.minecraft.inventory.Slot slot = ClientAccess.mc.thePlayer.inventoryContainer.getSlot(luaValue.checkint());
         if (slot != null && slot.getHasStack()) {
            return valueOf(slot.getStack().getDisplayName());
         }
      }

      return NIL;
   }
}
