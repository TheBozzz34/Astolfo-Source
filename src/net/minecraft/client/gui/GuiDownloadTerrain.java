package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;

public class GuiDownloadTerrain extends GuiScreen {
   private NetHandlerPlayClient netHandlerPlayClient;
   private int progress;
   private static final long a = 55460821054387L;

   public GuiDownloadTerrain(NetHandlerPlayClient var1) {
      this.netHandlerPlayClient = var1;
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
   }

   @Override
   public void updateScreen() {
      this.progress++;
      if (this.progress % 20 == 0) {
         this.netHandlerPlayClient.addToSendQueue(new C00PacketKeepAlive());
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawBackground(0);
      this.d(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
