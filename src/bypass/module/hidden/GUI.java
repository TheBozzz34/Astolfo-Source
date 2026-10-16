package bypass.module.hidden;

import bypass.auth.DisconnectHandler;
import bypass.gui.clickgui.GuiMode;
import bypass.gui.clickgui.defaultgui.DefaultClickGuiScreen;
import bypass.gui.clickgui.sidebar.SidebarClickGuiScreen;
import bypass.gui.clickgui.transparent.TransparentClickGuiScreen;
import bypass.gui.screen.AuthReconnectScreen;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.visuals.HUD;

public final class GUI extends Module {
   public static SidebarClickGuiScreen sidebarGui = new SidebarClickGuiScreen();
   public static TransparentClickGuiScreen transparentGui = new TransparentClickGuiScreen();
   public static DefaultClickGuiScreen defaultGui = new DefaultClickGuiScreen();

   public GUI(Category category) {
      super(category);
      this.setKey(54);
   }

   @Override
   public void onEnable() {
      if (DisconnectHandler.disconnected) {
         mc.displayGuiScreen(new AuthReconnectScreen());
      } else {
         openClickGui();
      }

      super.onEnable();
      this.setEnabled(false);
   }

   public static void openClickGui() {
      switch ((GuiMode)HUD.gui.getSetting("Mode").getValue()) {
         case DEFAULT:
            mc.displayGuiScreen(defaultGui);
            break;
         case TRANSPARENT:
            mc.displayGuiScreen(transparentGui);
      }
   }
}
