package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry {
   private final Minecraft mc = Minecraft.getMinecraft();

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      int i = y + slotHeight / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2;
      this.mc
         .fontRendererObj
         .drawStringNoShadow(
            I18n.format("lanServer.scanning"),
            this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(I18n.format("lanServer.scanning")) / 2,
            i,
            16777215
         );
      String string;
      switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
         case 0:
         default:
            string = "O o o";
            break;
         case 1:
         case 3:
            string = "o O o";
            break;
         case 2:
            string = "o o O";
      }

      this.mc
         .fontRendererObj
         .drawStringNoShadow(
            string, this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(string) / 2, i + this.mc.fontRendererObj.FONT_HEIGHT, 8421504
         );
   }

   @Override
   public void setSelected(int i, int j, int k) {
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
      return false;
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }
}
