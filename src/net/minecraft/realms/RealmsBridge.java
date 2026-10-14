package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private GuiScreen previousScreen;

   public void switchToRealms(GuiScreen guiScreen) {
      this.previousScreen = guiScreen;

      try {
         Class class2 = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
         Constructor constructor = class2.getDeclaredConstructor(RealmsScreen.class);
         constructor.setAccessible(true);
         Object object = constructor.newInstance(this);
         Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen)object).getProxy());
      } catch (Exception exception) {
         LOGGER.error("Realms module missing", exception);
      }
   }

   @Override
   public void init() {
      Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
   }
}
