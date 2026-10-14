package bypass.gui.screen;

import bypass.Astolfo;
import bypass.gui.PasswordField;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class ProxyLoginScreen extends GuiScreen {
   public GuiTextField u;
   public GuiTextField w;
   public int P = 12;
   public int l = 40;
   public GuiButton x;
   public GuiButton M;

   @Override
   public void initGui() {
      this.l = 40;
      short s = 180;
      byte b = 20;
      byte b2 = 14;
      this.u = new GuiTextField(7, Minecraft.getMinecraft().fontRendererObj, this.width / 2 - s / 2, this.l, s, b);
      this.l += b2 + b;
      this.w = new PasswordField(6, this.width / 2 - s / 2, this.l + 10 - b, s, b);
      this.l += b2 + b;
      this.u.setMaxStringLength(512);
      this.w.setMaxStringLength(512);
      this.buttonList.add(this.x = new GuiButton(0, this.width / 2 - 102, this.height - 24, 100, 20, "Submit"));
      this.buttonList.add(this.M = new GuiButton(1, this.width / 2 + 2, this.height - 24, 100, 20, "Back"));
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow("IP:", this.width / 2 - 90 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("IP:") - 3, 45.0F, -1);
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow("Port:", this.width / 2 - 90 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("Port:") - 3, 70.0F, -1);
      this.u.drawTextBox();
      this.w.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      Keyboard.enableRepeatEvents(true);
      this.u.textboxKeyTyped(typedChar, keyCode);
      this.w.textboxKeyTyped(typedChar, keyCode);
      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.u.mouseClicked(mouseX, mouseY, mouseButton);
      this.w.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            try {
               Astolfo.INSTANCE.setProxy(new Proxy(Type.SOCKS, new InetSocketAddress(this.u.getText(), Integer.parseInt(this.w.getText()))));
               Minecraft.getMinecraft().displayGuiScreen(new ProxyScreen());
            } catch (NumberFormatException numberFormatException) {
            }
            break;
         case 1:
            Minecraft.getMinecraft().displayGuiScreen(new ProxyScreen());
      }

      super.actionPerformed(button);
   }
}
