package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry {
   private final Minecraft mc = Minecraft.getMinecraft();
   private static final long a = 119534852734267L;

   @Override
   public void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int var12 = var3 + var5 / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2;
      this.mc
         .fontRendererObj
         .D(
            I18n.format("lanServer.scanning"),
            this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(I18n.format("lanServer.scanning")) / 2,
            var12,
            16777215
         );
      String var13;
      switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
         case 0:
         default:
            var13 = "O o o";
            break;
         case 1:
         case 3:
            var13 = "o O o";
            break;
         case 2:
            var13 = "o o O";
      }

      this.mc
         .fontRendererObj
         .D(var13, this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(var13) / 2, var12 + this.mc.fontRendererObj.FONT_HEIGHT, 8421504);
   }

   @Override
   public void setSelected(int var1, int var2, int var3) {
   }

   @Override
   public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6) {
      return false;
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6) {
   }
}
