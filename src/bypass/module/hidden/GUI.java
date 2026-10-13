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
   public static SidebarClickGuiScreen P = new SidebarClickGuiScreen();
   public static TransparentClickGuiScreen K = new TransparentClickGuiScreen();
   public static DefaultClickGuiScreen w = new DefaultClickGuiScreen();
   private static final long c = 80246500206791L;
   private static final long e = 4290029586848153654L;

   public GUI(Category var1) {
      super(var1);
      this.setKey((int)e);
   }

   @Override
   public void onEnable() {
      if (DisconnectHandler.disconnected) {
         mc.displayGuiScreen(new AuthReconnectScreen());
      } else {
         y();
      }

      super.onEnable();
      this.setEnabled(false);
   }

   public static void y() {
      switch ((GuiMode)HUD.gui.getSetting("Mode").getValue()) {
         case DEFAULT:
            mc.displayGuiScreen(w);
            break;
         case TRANSPARENT:
            mc.displayGuiScreen(K);
      }
   }
}
