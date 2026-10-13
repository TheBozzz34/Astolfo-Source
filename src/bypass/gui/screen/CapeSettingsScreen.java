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
   private GuiScreen m;
   private static final long a = 53481734187291L;

   public CapeSettingsScreen(GuiScreen var1) {
      this.m = var1;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.l(), 155, 20, "Open Cape Editor"));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.l(), 155, 20, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton var1) {
      switch (var1.id) {
         case 0:
            this.W();
            break;
         case 1:
            this.mc.displayGuiScreen(this.m);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      super.drawDefaultBackground();
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   private void W() {
      try {
         GameProfile var3 = Minecraft.getMinecraft().thePlayer.getGameProfile();
         String var4 = var3.getName();
         String var5 = var3.getId().toString().replace("-", "");
         String var6 = Minecraft.getMinecraft().getSession().getToken();
         Random var7 = new Random();
         Random var8 = new Random(System.identityHashCode(new Object()));
         BigInteger var9 = new BigInteger(128, var7);
         BigInteger var10 = new BigInteger(128, var8);
         BigInteger var11 = var9.xor(var10);
         String var12 = var11.toString(16);
         Minecraft.getMinecraft().getSessionService().joinServer(var3, var6, var12);
         String var13 = "https://optifine.net/capeChange?u=" + var5 + "&n=" + var4 + "&s=" + var12;
         Desktop.getDesktop().browse(new URL(var13).toURI());
      } catch (URISyntaxException | IOException | AuthenticationException var14) {
         var14.printStackTrace();
      }
   }

   private int l() {
      return 147;
   }
}
