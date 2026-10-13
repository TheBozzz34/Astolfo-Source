package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private GuiScreen previousScreen;
   private static final long a = 107535919443928L;

   public void switchToRealms(GuiScreen var1) {
      this.previousScreen = var1;

      try {
         Class var4 = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
         Constructor var5 = var4.getDeclaredConstructor(RealmsScreen.class);
         var5.setAccessible(true);
         Object var6 = var5.newInstance(this);
         Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen)var6).getProxy());
      } catch (Exception var7) {
         LOGGER.error("Realms module missing", var7);
      }
   }

   @Override
   public void init() {
      Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
   }
}
