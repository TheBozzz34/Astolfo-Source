package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.I18n;

public class GuiYesNo extends GuiScreen {
   public GuiYesNoCallback parentScreen;
   public String messageLine1;
   private String messageLine2;
   private final List<String> field_175298_s = Lists.newArrayList();
   public String confirmButtonText;
   public String cancelButtonText;
   public int parentButtonClickedId;
   private int ticksUntilEnable;

   public GuiYesNo(GuiYesNoCallback guiYesNoCallback, String string, String string2, int i) {
      this.parentScreen = guiYesNoCallback;
      this.messageLine1 = string;
      this.messageLine2 = string2;
      this.parentButtonClickedId = i;
      this.confirmButtonText = I18n.format("gui.yes");
      this.cancelButtonText = I18n.format("gui.no");
   }

   public GuiYesNo(GuiYesNoCallback guiYesNoCallback, String string, String string2, String string3, String string4, int i) {
      this.parentScreen = guiYesNoCallback;
      this.messageLine1 = string;
      this.messageLine2 = string2;
      this.confirmButtonText = string3;
      this.cancelButtonText = string4;
      this.parentButtonClickedId = i;
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText));
      this.field_175298_s.clear();
      this.field_175298_s.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int i = 90;

      for (String string : this.field_175298_s) {
         this.drawCenteredString(this.fontRendererObj, string, this.width / 2, i, 16777215);
         i += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void setButtonDelay(int i) {
      this.ticksUntilEnable = i;

      for (GuiButton guiButton : this.buttonList) {
         guiButton.enabled = false;
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (--this.ticksUntilEnable == 0) {
         for (GuiButton guiButton : this.buttonList) {
            guiButton.enabled = true;
         }
      }
   }
}
