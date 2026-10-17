package bypass.gui.screen;

import bypass.auth.DisconnectHandler;
import bypass.module.hidden.GUI;
import bypass.render.RenderUtil;
import bypass.util.Stopwatch;
import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AuthReconnectScreen extends GuiScreen {
   private GuiButton reconnectButton;
   private boolean unusedFlag;
   private Stopwatch reconnectTimer = new Stopwatch();
   public boolean unusedPublicFlag;
   private boolean reconnecting;

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(this.reconnectButton = new GuiButton(0, this.width / 2 - 50, this.height / 2 - this.height / 12, 100, 20, "Reconnect"));
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (!DisconnectHandler.disconnected) {
         GUI.openClickGui();
      }

      if (this.reconnectTimer.hasElapsed(2000.0F) && this.reconnecting) {
         this.reconnecting = false;
      }

      this.reconnectButton.enabled = !this.reconnecting;
      drawRect(0, 0, this.width, this.height, new Color(1, 1, 1, 120).getRGB());
      RenderUtil.setColor(-1);
      int i = this.width / 2 - 38;
      int j = this.height / 2 - this.height / 3;
      GL11.glEnable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("client/logo.png"));
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      drawModalRectWithCustomSizedTexture(i, j, 0.0F, 0.0F, 76, 75, 76.0F, 75.0F);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0 && !this.reconnecting) {
         this.mc
            .mouseHelper
            .getAuthSession()
            .getEndpoint()
            .getConnection()
            .connect(this.mc.mouseHelper.getAuthSession().getEndpoint().getHost(), this.mc.mouseHelper.getAuthSession().getEndpoint().getPort().getPort());
         this.reconnecting = true;
         this.reconnectTimer.reset();
      }

      super.actionPerformed(button);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
