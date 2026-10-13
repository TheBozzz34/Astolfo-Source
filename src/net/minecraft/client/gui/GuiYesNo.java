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
   private static final long e = 87499803608146L;

   public GuiYesNo(GuiYesNoCallback var1, String var2, String var3, int var4) {
      this.parentScreen = var1;
      this.messageLine1 = var2;
      this.messageLine2 = var3;
      this.parentButtonClickedId = var4;
      this.confirmButtonText = I18n.format("gui.yes");
      this.cancelButtonText = I18n.format("gui.no");
   }

   public GuiYesNo(GuiYesNoCallback var1, String var2, String var3, String var4, String var5, int var6) {
      this.parentScreen = var1;
      this.messageLine1 = var2;
      this.messageLine2 = var3;
      this.confirmButtonText = var4;
      this.cancelButtonText = var5;
      this.parentButtonClickedId = var6;
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText));
      this.field_175298_s.clear();
      this.field_175298_s.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      this.parentScreen.confirmClicked(var1.id == 0, this.parentButtonClickedId);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int var7 = 90;

      for (String var9 : this.field_175298_s) {
         this.d(this.fontRendererObj, var9, this.width / 2, var7, 16777215);
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
