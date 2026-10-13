package bypass.gui;

import bypass.Astolfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Checkbox extends Gui {
   public String d;
   public int H;
   public int x;
   private boolean a;
   private static final long b = 13228480642808L;

   public Checkbox(String var1, int var2, int var3, boolean var4) {
      this.d = var1;
      this.H = var2;
      this.x = var3;
      this.a = var4;
   }

   public void b(int var1, int var2, int var3) {
      if (this.b(var1, var2)) {
         this.a = !this.a;
      }
   }

   public void E(int var1, int var2) {
      if (this.b(var1, var2)) {
         drawRect(this.H, this.x, this.H + 15, this.x + 15, -6250336);
         drawRect(this.H + 1, this.x + 1, this.H + 14, this.x + 14, -16777216);
         float var10002 = this.H + 18;
         float var10003 = this.x + 4;
         Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.d, var10002, var10003, 16777215);
      } else {
         drawRect(this.H, this.x, this.H + 15, this.x + 15, -8947849);
         drawRect(this.H + 1, this.x + 1, this.H + 14, this.x + 14, -16777216);
         float var3 = this.H + 18;
         float var4 = this.x + 4;
         Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.d, var3, var4, 13421772);
      }

      if (this.a) {
         drawRect(this.H + 2, this.x + 2, this.H + 13, this.x + 13, Astolfo.INSTANCE.getColorSetting().getValue().getRGB());
      }
   }

   public boolean b(int var1, int var2) {
      return var1 >= this.H && var1 <= this.H + 20 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.d) && var2 >= this.x && var2 <= this.x + 15;
   }

   public void K(boolean var1) {
      this.a = var1;
   }

   public boolean a() {
      return this.a;
   }
}
