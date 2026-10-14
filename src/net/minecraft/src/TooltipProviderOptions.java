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
   private GuiScreen t = null;
   private int Q = 0;
   private int r = 0;
   private long c = 0L;

   public TooltipProviderOptions(GuiScreen guiScreen) {
      this.t = guiScreen;
   }

   public void U(int i, int j, List list) {
      if (Math.abs(i - this.Q) <= 5 && Math.abs(j - this.r) <= 5) {
         short s = 700;
         if (System.currentTimeMillis() >= this.c + s) {
            int k = this.t.width / 2 - 150;
            int l = this.t.height / 6 - 7;
            if (j <= l + 98) {
               l += 105;
            }

            int m = k + 150 + 150;
            int n = l + 84 + 10;
            GuiButton guiButton = this.w(i, j, list);
            if (guiButton instanceof IOptionControl) {
               IOptionControl iOptionControl = (IOptionControl)guiButton;
               GameSettings.Options options = iOptionControl.getOption();
               String[] strings = i(options);
               if (strings == null) {
                  return;
               }

               GuiVideoSettings.drawGradientRect(this.t, k, l, m, n, -536870912, -536870912);

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
         this.Q = i;
         this.r = j;
         this.c = System.currentTimeMillis();
      }
   }

   private GuiButton w(int i, int j, List list) {
      for (int k = 0; k < list.size(); k++) {
         GuiButton guiButton = (GuiButton)list.get(k);
         int l = GuiVideoSettings.j(guiButton);
         int m = GuiVideoSettings.V(guiButton);
         boolean bl = i >= guiButton.xPosition && j >= guiButton.yPosition && i < guiButton.xPosition + l && j < guiButton.yPosition + m;
         if (bl) {
            return guiButton;
         }
      }

      return null;
   }

   private static String[] i(GameSettings.Options options) {
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
