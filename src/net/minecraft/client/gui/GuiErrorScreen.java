package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;

public class GuiErrorScreen extends GuiScreen {
   private String field_146313_a;
   private String field_146312_f;

   public GuiErrorScreen(String string, String string2) {
      this.field_146313_a = string;
      this.field_146312_f = string2;
   }

   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel")));
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
      this.drawCenteredString(this.fontRendererObj, this.field_146313_a, this.width / 2, 90, 16777215);
      this.drawCenteredString(this.fontRendererObj, this.field_146312_f, this.width / 2, 110, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      this.mc.displayGuiScreen((GuiScreen)null);
   }
}
