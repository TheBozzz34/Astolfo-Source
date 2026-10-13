package bypass.lua.player;

import bypass.ClientAccess;
import net.minecraft.item.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class HeldItem extends ZeroArgFunction {
   private static final long a = 89229962484318L;

   @Override
   public LuaValue call() {
      if (ClientAccess.mc.thePlayer != null) {
         ItemStack var4 = ClientAccess.mc.thePlayer.getCurrentEquippedItem();
         if (var4 != null) {
            return valueOf(var4.getDisplayName());
         }
      }

      return NIL;
   }
}
