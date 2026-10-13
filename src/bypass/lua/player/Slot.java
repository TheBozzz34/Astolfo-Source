package bypass.lua.player;

import bypass.ClientAccess;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public final class Slot extends OneArgFunction {
   private static final long a = 82964650317896L;

   @Override
   public LuaValue call(LuaValue var1) {
      if (ClientAccess.mc.thePlayer != null && var1.isnumber()) {
         net.minecraft.inventory.Slot var5 = ClientAccess.mc.thePlayer.inventoryContainer.getSlot(var1.checkint());
         if (var5 != null && var5.getHasStack()) {
            return valueOf(var5.getStack().getDisplayName());
         }
      }

      return NIL;
   }
}
