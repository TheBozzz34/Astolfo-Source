package net.minecraft.src;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

public class GuiScreenOF extends GuiScreen {
   private static final long a = 12410068435826L;

   public void actionPerformedRightClick(GuiButton var1) throws IOException {
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      if (var3 == 1) {
         GuiButton var7 = Y(this.buttonList, var1, var2);
         if (var7 != null && var7.enabled) {
            var7.playPressSound(this.mc.getSoundHandler());
            this.actionPerformedRightClick(var7);
         }
      }
   }

   public static GuiButton Y(List<GuiButton> var0, int var1, int var2) {
      for (int var6 = 0; var6 < var0.size(); var6++) {
         GuiButton var7 = (GuiButton)var0.get(var6);
         if (var7.visible) {
            int var8 = GuiVideoSettings.j(var7);
            int var9 = GuiVideoSettings.V(var7);
            if (var1 >= var7.xPosition && var2 >= var7.yPosition && var1 < var7.xPosition + var8 && var2 < var7.yPosition + var9) {
               return var7;
            }
         }
      }

      return null;
   }
}
