package bypass.lua.player;

import bypass.ClientAccess;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class HeldItem extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      if (ClientAccess.mc.thePlayer != null) {
         ItemStack itemStack = ClientAccess.mc.thePlayer.getCurrentEquippedItem();
         if (itemStack != null) {
            return valueOf(itemStack.getDisplayName());
         }
      }

      return NIL;
   }
}
