package bypass.gui;

import bypass.Astolfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Checkbox extends Gui {
   public String d;
   public int H;
   public int x;
   private boolean a;

   public Checkbox(String string, int i, int j, boolean bl) {
      this.d = string;
      this.H = i;
      this.x = j;
      this.a = bl;
   }

   public void b(int i, int j, int k) {
      if (this.b(i, j)) {
         this.a = !this.a;
      }
   }

   public void E(int i, int j) {
      if (this.b(i, j)) {
         drawRect(this.H, this.x, this.H + 15, this.x + 15, -6250336);
         drawRect(this.H + 1, this.x + 1, this.H + 14, this.x + 14, -16777216);
         float f = this.H + 18;
         float g = this.x + 4;
         Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.d, f, g, 16777215);
      } else {
         drawRect(this.H, this.x, this.H + 15, this.x + 15, -8947849);
         drawRect(this.H + 1, this.x + 1, this.H + 14, this.x + 14, -16777216);
         float h = this.H + 18;
         float f2 = this.x + 4;
         Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.d, h, f2, 13421772);
      }

      if (this.a) {
         drawRect(this.H + 2, this.x + 2, this.H + 13, this.x + 13, Astolfo.INSTANCE.getColorSetting().getValue().getRGB());
      }
   }

   public boolean b(int i, int j) {
      return i >= this.H && i <= this.H + 20 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.d) && j >= this.x && j <= this.x + 15;
   }

   public void K(boolean bl) {
      this.a = bl;
   }

   public boolean a() {
      return this.a;
   }
}
