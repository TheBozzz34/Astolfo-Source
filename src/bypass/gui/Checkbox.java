package bypass.gui;

import bypass.Astolfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Checkbox extends Gui {
   public String label;
   public int posX;
   public int posY;
   private boolean checked;

   public Checkbox(String string, int i, int j, boolean bl) {
      this.label = string;
      this.posX = i;
      this.posY = j;
      this.checked = bl;
   }

   public void handleMouseClick(int i, int j, int k) {
      if (this.isHovered(i, j)) {
         this.checked = !this.checked;
      }
   }

   public void drawCheckbox(int i, int j) {
      if (this.isHovered(i, j)) {
         drawRect(this.posX, this.posY, this.posX + 15, this.posY + 15, -6250336);
         drawRect(this.posX + 1, this.posY + 1, this.posX + 14, this.posY + 14, -16777216);
         float f = this.posX + 18;
         float g = this.posY + 4;
         Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.label, f, g, 16777215);
      } else {
         drawRect(this.posX, this.posY, this.posX + 15, this.posY + 15, -8947849);
         drawRect(this.posX + 1, this.posY + 1, this.posX + 14, this.posY + 14, -16777216);
         float h = this.posX + 18;
         float f2 = this.posY + 4;
         Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.label, h, f2, 13421772);
      }

      if (this.checked) {
         drawRect(this.posX + 2, this.posY + 2, this.posX + 13, this.posY + 13, Astolfo.INSTANCE.getColorSetting().getValue().getRGB());
      }
   }

   public boolean isHovered(int i, int j) {
      return i >= this.posX
         && i <= this.posX + 20 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.label)
         && j >= this.posY
         && j <= this.posY + 15;
   }

   public void setChecked(boolean bl) {
      this.checked = bl;
   }

   public boolean isChecked() {
      return this.checked;
   }
}
