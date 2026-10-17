package net.minecraft.src;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

public class GuiScreenOF extends GuiScreen {
   public void actionPerformedRightClick(GuiButton button) throws IOException {
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 1) {
         GuiButton guiButton = getSelectedButton(this.buttonList, mouseX, mouseY);
         if (guiButton != null && guiButton.enabled) {
            guiButton.playPressSound(this.mc.getSoundHandler());
            this.actionPerformedRightClick(guiButton);
         }
      }
   }

   public static GuiButton getSelectedButton(List<GuiButton> list, int i, int j) {
      for (int k = 0; k < list.size(); k++) {
         GuiButton guiButton = (GuiButton)list.get(k);
         if (guiButton.visible) {
            int l = GuiVideoSettings.getButtonWidth(guiButton);
            int m = GuiVideoSettings.getButtonHeight(guiButton);
            if (i >= guiButton.xPosition && j >= guiButton.yPosition && i < guiButton.xPosition + l && j < guiButton.yPosition + m) {
               return guiButton;
            }
         }
      }

      return null;
   }
}
