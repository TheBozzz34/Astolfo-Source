package bypass.lua.client;

import bypass.ClientAccess;
import net.minecraft.client.gui.inventory.GuiChest;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class ChestName extends ZeroArgFunction {
   private static final long a = 88559595944663L;

   @Override
   public LuaValue call() {
      if (ClientAccess.mc.currentScreen != null && ClientAccess.mc.currentScreen instanceof GuiChest) {
         GuiChest var4 = (GuiChest)ClientAccess.mc.currentScreen;
         return valueOf(var4.lowerChestInventory.getDisplayName().getUnformattedText());
      } else {
         return valueOf("none");
      }
   }
}
