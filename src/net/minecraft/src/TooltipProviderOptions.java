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
   private static final long a = 65311671946324L;

   public TooltipProviderOptions(GuiScreen var1) {
      this.t = var1;
   }

   public void U(int var1, int var2, List var3) {
      if (Math.abs(var1 - this.Q) <= 5 && Math.abs(var2 - this.r) <= 5) {
         short var7 = 700;
         if (System.currentTimeMillis() >= this.c + var7) {
            int var8 = this.t.width / 2 - 150;
            int var9 = this.t.height / 6 - 7;
            if (var2 <= var9 + 98) {
               var9 += 105;
            }

            int var10 = var8 + 150 + 150;
            int var11 = var9 + 84 + 10;
            GuiButton var12 = this.w(var1, var2, var3);
            if (var12 instanceof IOptionControl) {
               IOptionControl var13 = (IOptionControl)var12;
               GameSettings.Options var14 = var13.getOption();
               String[] var15 = i(var14);
               if (var15 == null) {
                  return;
               }

               GuiVideoSettings.drawGradientRect(this.t, var8, var9, var10, var11, -536870912, -536870912);

               for (int var16 = 0; var16 < var15.length; var16++) {
                  String var17 = var15[var16];
                  int var18 = 14540253;
                  if (var17.endsWith("!")) {
                     var18 = 16719904;
                  }

                  FontRenderer var19 = Minecraft.getMinecraft().fontRendererObj;
                  var19.drawStringWithShadow(var17, var8 + 5, var9 + 5 + var16 * 11, var18);
               }
            }
         }
      } else {
         this.Q = var1;
         this.r = var2;
         this.c = System.currentTimeMillis();
      }
   }

   private GuiButton w(int var1, int var2, List var3) {
      for (int var7 = 0; var7 < var3.size(); var7++) {
         GuiButton var8 = (GuiButton)var3.get(var7);
         int var9 = GuiVideoSettings.j(var8);
         int var10 = GuiVideoSettings.V(var8);
         boolean var11 = var1 >= var8.xPosition && var2 >= var8.yPosition && var1 < var8.xPosition + var9 && var2 < var8.yPosition + var10;
         if (var11) {
            return var8;
         }
      }

      return null;
   }

   private static String[] i(GameSettings.Options var0) {
      return getTooltipLines(var0.getEnumString());
   }

   private static String[] getTooltipLines(String var0) {
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < 10; var5++) {
         String var6 = var0 + ".tooltip." + (var5 + 1);
         String var7 = Lang.get(var6, (String)null);
         if (var7 == null) {
            break;
         }

         var4.add(var7);
      }

      return var4.size() <= 0 ? null : (java.lang.String[])(var4.toArray(new String[var4.size()]));
   }
}
