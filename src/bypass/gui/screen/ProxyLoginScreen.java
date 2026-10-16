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
   public GuiTextField ipField;
   public GuiTextField portField;
   public int unusedSpacing = 12;
   public int nextFieldY = 40;
   public GuiButton submitButton;
   public GuiButton backButton;

   @Override
   public void initGui() {
      this.nextFieldY = 40;
      short s = 180;
      byte b = 20;
      byte b2 = 14;
      this.ipField = new GuiTextField(7, Minecraft.getMinecraft().fontRendererObj, this.width / 2 - s / 2, this.nextFieldY, s, b);
      this.nextFieldY += b2 + b;
      this.portField = new PasswordField(6, this.width / 2 - s / 2, this.nextFieldY + 10 - b, s, b);
      this.nextFieldY += b2 + b;
      this.ipField.setMaxStringLength(512);
      this.portField.setMaxStringLength(512);
      this.buttonList.add(this.submitButton = new GuiButton(0, this.width / 2 - 102, this.height - 24, 100, 20, "Submit"));
      this.buttonList.add(this.backButton = new GuiButton(1, this.width / 2 + 2, this.height - 24, 100, 20, "Back"));
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
      this.ipField.drawTextBox();
      this.portField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      Keyboard.enableRepeatEvents(true);
      this.ipField.textboxKeyTyped(typedChar, keyCode);
      this.portField.textboxKeyTyped(typedChar, keyCode);
      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.ipField.mouseClicked(mouseX, mouseY, mouseButton);
      this.portField.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            try {
               Astolfo.INSTANCE.setProxy(new Proxy(Type.SOCKS, new InetSocketAddress(this.ipField.getText(), Integer.parseInt(this.portField.getText()))));
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
