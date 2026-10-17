package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class GuiCustomizeSkin extends GuiScreen {
   private final GuiScreen parentScreen;
   private String title;

   public GuiCustomizeSkin(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   @Override
   public void initGui() {
      int i = 0;
      this.title = I18n.format("options.skinCustomisation.title");

      for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
         this.buttonList
            .add(new GuiCustomizeSkin.ButtonPart(enumPlayerModelParts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumPlayerModelParts));
         i++;
      }

      if (i % 2 == 1) {
         i++;
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button instanceof GuiCustomizeSkin.ButtonPart) {
            EnumPlayerModelParts enumPlayerModelParts = ((GuiCustomizeSkin.ButtonPart)button).playerModelParts;
            this.mc.gameSettings.switchModelPartEnabled(enumPlayerModelParts);
            button.displayString = this.func_175358_a(enumPlayerModelParts);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private String func_175358_a(EnumPlayerModelParts playerModelParts) {
      String string;
      if (this.mc.gameSettings.getModelParts().contains(playerModelParts)) {
         string = I18n.format("options.on");
      } else {
         string = I18n.format("options.off");
      }

      return playerModelParts.func_179326_d().getFormattedText() + ": " + string;
   }

   public class ButtonPart extends GuiButton {
      private final EnumPlayerModelParts playerModelParts;

      public ButtonPart(int i, int j, int k, int l, int m, EnumPlayerModelParts playerModelParts) {
         super(i, j, k, l, m, GuiCustomizeSkin.this.func_175358_a(playerModelParts));
         this.playerModelParts = playerModelParts;
      }
   }
}
