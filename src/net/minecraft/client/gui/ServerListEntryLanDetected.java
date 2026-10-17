package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry {
   private final GuiMultiplayer field_148292_c;
   public final Minecraft mc;
   public final LanServerDetector.LanServer field_148291_b;
   private long field_148290_d = 0L;

   public ServerListEntryLanDetected(GuiMultiplayer guiMultiplayer, LanServerDetector.LanServer lanServer) {
      this.field_148292_c = guiMultiplayer;
      this.field_148291_b = lanServer;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      this.mc.fontRendererObj.drawStringNoShadow(I18n.format("lanServer.title"), x + 32 + 3, y + 1, 16777215);
      this.mc.fontRendererObj.drawStringNoShadow(this.field_148291_b.getServerMotd(), x + 32 + 3, y + 12, 8421504);
      if (this.mc.gameSettings.hideServerAddress) {
         this.mc.fontRendererObj.drawStringNoShadow(I18n.format("selectServer.hiddenAddress"), x + 32 + 3, y + 12 + 11, 3158064);
      } else {
         this.mc.fontRendererObj.drawStringNoShadow(this.field_148291_b.getServerIpPort(), x + 32 + 3, y + 12 + 11, 3158064);
      }
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
      this.field_148292_c.selectServer(slotIndex);
      if (Minecraft.getSystemTime() - this.field_148290_d < 250L) {
         this.field_148292_c.connectToSelected();
      }

      this.field_148290_d = Minecraft.getSystemTime();
      return false;
   }

   @Override
   public void setSelected(int i, int j, int k) {
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }

   public LanServerDetector.LanServer getLanServer() {
      return this.field_148291_b;
   }
}
