package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.settings.GameSettings;

public class TooltipProviderOptions {
   private GuiScreen guiScreen = null;
   private int lastMouseX = 0;
   private int lastMouseY = 0;
   private long mouseStillTime = 0L;

   public TooltipProviderOptions(GuiScreen guiScreen) {
      this.guiScreen = guiScreen;
   }

   public void drawTooltips(int i, int j, List list) {
      if (Math.abs(i - this.lastMouseX) <= 5 && Math.abs(j - this.lastMouseY) <= 5) {
         short s = 700;
         if (System.currentTimeMillis() >= this.mouseStillTime + s) {
            int k = this.guiScreen.width / 2 - 150;
            int l = this.guiScreen.height / 6 - 7;
            if (j <= l + 98) {
               l += 105;
            }

            int m = k + 150 + 150;
            int n = l + 84 + 10;
            GuiButton guiButton = this.getSelectedButton(i, j, list);
            if (guiButton instanceof IOptionControl) {
               IOptionControl iOptionControl = (IOptionControl)guiButton;
               GameSettings.Options options = iOptionControl.getOption();
               String[] strings = getTooltipLinesForOption(options);
               if (strings == null) {
                  return;
               }

               GuiVideoSettings.drawGradientRect(this.guiScreen, k, l, m, n, -536870912, -536870912);

               for (int i2 = 0; i2 < strings.length; i2++) {
                  String string = strings[i2];
                  int i3 = 14540253;
                  if (string.endsWith("!")) {
                     i3 = 16719904;
                  }

                  FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
                  fontRenderer.drawStringWithShadow(string, k + 5, l + 5 + i2 * 11, i3);
               }
            }
         }
      } else {
         this.lastMouseX = i;
         this.lastMouseY = j;
         this.mouseStillTime = System.currentTimeMillis();
      }
   }

   private GuiButton getSelectedButton(int i, int j, List list) {
      for (int k = 0; k < list.size(); k++) {
         GuiButton guiButton = (GuiButton)list.get(k);
         int l = GuiVideoSettings.getButtonWidth(guiButton);
         int m = GuiVideoSettings.getButtonHeight(guiButton);
         boolean bl = i >= guiButton.xPosition && j >= guiButton.yPosition && i < guiButton.xPosition + l && j < guiButton.yPosition + m;
         if (bl) {
            return guiButton;
         }
      }

      return null;
   }

   private static String[] getTooltipLinesForOption(GameSettings.Options options) {
      return getTooltipLines(options.getEnumString());
   }

   private static String[] getTooltipLines(String key) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < 10; i++) {
         String string = key + ".tooltip." + (i + 1);
         String string2 = Lang.get(string, (String)null);
         if (string2 == null) {
            break;
         }

         arrayList.add(string2);
      }

      return arrayList.size() <= 0 ? null : (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }
}
