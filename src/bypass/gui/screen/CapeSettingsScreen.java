package bypass.gui.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import java.awt.Desktop;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class CapeSettingsScreen extends GuiScreen {
   private GuiScreen parentScreen;

   public CapeSettingsScreen(GuiScreen guiScreen) {
      this.parentScreen = guiScreen;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.getButtonY(), 155, 20, "Open Cape Editor"));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.getButtonY(), 155, 20, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) {
      switch (button.id) {
         case 0:
            this.openCapeEditor();
            break;
         case 1:
            this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   private void openCapeEditor() {
      try {
         GameProfile gameProfile = Minecraft.getMinecraft().thePlayer.getGameProfile();
         String string = gameProfile.getName();
         String string2 = gameProfile.getId().toString().replace("-", "");
         String string3 = Minecraft.getMinecraft().getSession().getToken();
         Random random = new Random();
         Random random2 = new Random(System.identityHashCode(new Object()));
         BigInteger bigInteger = new BigInteger(128, random);
         BigInteger bigInteger2 = new BigInteger(128, random2);
         BigInteger bigInteger3 = bigInteger.xor(bigInteger2);
         String string4 = bigInteger3.toString(16);
         Minecraft.getMinecraft().getSessionService().joinServer(gameProfile, string3, string4);
         String string5 = "https://optifine.net/capeChange?u=" + string2 + "&n=" + string + "&s=" + string4;
         Desktop.getDesktop().browse(new URL(string5).toURI());
      } catch (URISyntaxException | IOException | AuthenticationException value) {
         value.printStackTrace();
      }
   }

   private int getButtonY() {
      return 147;
   }
}
