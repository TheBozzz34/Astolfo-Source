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
   private static final long a = 52888000244283L;

   @Override
   public void initGui() {
      this.l = 40;
      short var4 = 180;
      byte var5 = 20;
      byte var6 = 14;
      this.u = new GuiTextField(7, Minecraft.getMinecraft().fontRendererObj, this.width / 2 - var4 / 2, this.l, var4, var5);
      this.l += var6 + var5;
      this.w = new PasswordField(6, this.width / 2 - var4 / 2, this.l + 10 - var5, var4, var5);
      this.l += var6 + var5;
      this.u.setMaxStringLength(512);
      this.w.setMaxStringLength(512);
      this.buttonList.add(this.x = new GuiButton(0, this.width / 2 - 102, this.height - 24, 100, 20, "Submit"));
      this.buttonList.add(this.M = new GuiButton(1, this.width / 2 + 2, this.height - 24, 100, 20, "Back"));
      super.initGui();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow("IP:", this.width / 2 - 90 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("IP:") - 3, 45.0F, -1);
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow("Port:", this.width / 2 - 90 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("Port:") - 3, 70.0F, -1);
      this.u.drawTextBox();
      this.w.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      Keyboard.enableRepeatEvents(true);
      this.u.textboxKeyTyped(var1, var2);
      this.w.textboxKeyTyped(var1, var2);
      super.keyTyped(var1, var2);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      this.u.mouseClicked(var1, var2, var3);
      this.w.mouseClicked(var1, var2, var3);
      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      switch (var1.id) {
         case 0:
            try {
               Astolfo.INSTANCE.setProxy(new Proxy(Type.SOCKS, new InetSocketAddress(this.u.getText(), Integer.parseInt(this.w.getText()))));
               Minecraft.getMinecraft().displayGuiScreen(new ProxyScreen());
            } catch (NumberFormatException var6) {
            }
            break;
         case 1:
            Minecraft.getMinecraft().displayGuiScreen(new ProxyScreen());
      }

      super.actionPerformed(var1);
   }
}
