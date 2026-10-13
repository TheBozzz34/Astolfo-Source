package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class GuiCustomizeSkin extends GuiScreen {
   private final GuiScreen parentScreen;
   private String title;
   private static final long a = 82280882754601L;

   public GuiCustomizeSkin(GuiScreen var1) {
      this.parentScreen = var1;
   }

   @Override
   public void initGui() {
      int var4 = 0;
      this.title = I18n.format("options.skinCustomisation.title");

      for (EnumPlayerModelParts var8 : EnumPlayerModelParts.values()) {
         this.buttonList
            .add(new GuiCustomizeSkin.ButtonPart(var8.getPartId(), this.width / 2 - 155 + var4 % 2 * 160, this.height / 6 + 24 * (var4 >> 1), 150, 20, var8));
         var4++;
      }

      if (var4 % 2 == 1) {
         var4++;
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (var4 >> 1), I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (var1 instanceof GuiCustomizeSkin.ButtonPart) {
            EnumPlayerModelParts var5 = ((GuiCustomizeSkin.ButtonPart)var1).playerModelParts;
            this.mc.gameSettings.switchModelPartEnabled(var5);
            var1.displayString = this.func_175358_a(var5);
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   private String func_175358_a(EnumPlayerModelParts var1) {
      String var5;
      if (this.mc.gameSettings.getModelParts().contains(var1)) {
         var5 = I18n.format("options.on");
      } else {
         var5 = I18n.format("options.off");
      }

      return var1.func_179326_d().getFormattedText() + ": " + var5;
   }

   public class ButtonPart extends GuiButton {
      private final EnumPlayerModelParts playerModelParts;

      public ButtonPart(int var2, int var3, int var4, int var5, int var6, EnumPlayerModelParts var7) {
         super(var2, var3, var4, var5, var6, GuiCustomizeSkin.this.func_175358_a(var7));
         this.playerModelParts = var7;
      }
   }
}
