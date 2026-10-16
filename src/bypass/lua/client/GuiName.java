package bypass.lua.client;

import bypass.ClientAccess;
import bypass.gui.clickgui.defaultgui.DefaultClickGuiScreen;
import bypass.gui.clickgui.transparent.TransparentClickGuiScreen;
import bypass.module.exploits.HideWindow;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class GuiName extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      if (ClientAccess.mc.currentScreen != null) {
         if (ClientAccess.mc.currentScreen instanceof GuiContainer && !(ClientAccess.mc.currentScreen instanceof GuiInventory)) {
            return valueOf("chest");
         } else if (ClientAccess.mc.currentScreen instanceof GuiInventory) {
            return valueOf("inventory");
         } else if (ClientAccess.mc.currentScreen instanceof GuiChat) {
            return valueOf("chat");
         } else {
            return !(ClientAccess.mc.currentScreen instanceof TransparentClickGuiScreen) && !(ClientAccess.mc.currentScreen instanceof DefaultClickGuiScreen)
               ? valueOf("unknown")
               : valueOf("astolfo");
         }
      } else {
         return ClientAccess.client.getModuleManager().get(HideWindow.class).isEnabled()
               && HideWindow.hiddenChest != null
               && HideWindow.hiddenWindowTitle != null
               && HideWindow.hiddenWindowTitle.getFormattedText() != null
            ? valueOf(HideWindow.hiddenWindowTitle.getFormattedText())
            : valueOf("none");
      }
   }
}
