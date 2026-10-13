package net.minecraft.src;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiMessage extends GuiScreen {
   private GuiScreen parentScreen;
   private String messageLine1;
   private String messageLine2;
   private final List listLines2 = Lists.newArrayList();
   public String confirmButtonText;
   private int ticksUntilEnable;
   private static final long a = 77574471849270L;

   public GuiMessage(GuiScreen var1, String var2, String var3) {
      this.parentScreen = var1;
      this.messageLine1 = var2;
      this.messageLine2 = var3;
      this.confirmButtonText = I18n.format("gui.done");
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 74, this.height / 6 + 96, this.confirmButtonText));
      this.listLines2.clear();
      this.listLines2.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      Config.getMinecraft().displayGuiScreen(this.parentScreen);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int var7 = 90;

      for (Object var9 : this.listLines2) {
         this.d(this.fontRendererObj, (String)var9, this.width / 2, var7, 16777215);
         var7 += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(var1, var2, var3);
   }

   public void setButtonDelay(int var1) {
      this.ticksUntilEnable = var1;

      for (GuiButton var6 : this.buttonList) {
         var6.enabled = false;
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (--this.ticksUntilEnable == 0) {
         for (GuiButton var5 : this.buttonList) {
            var5.enabled = true;
         }
      }
   }
}
