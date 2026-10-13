package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;

public class GuiErrorScreen extends GuiScreen {
   private String field_146313_a;
   private String field_146312_f;
   private static final long a = 128519538463784L;

   public GuiErrorScreen(String var1, String var2) {
      this.field_146313_a = var1;
      this.field_146312_f = var2;
   }

   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel")));
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
      this.d(this.fontRendererObj, this.field_146313_a, this.width / 2, 90, 16777215);
      this.d(this.fontRendererObj, this.field_146312_f, this.width / 2, 110, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      this.mc.displayGuiScreen((GuiScreen)null);
   }
}
