package net.minecraft.src;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiOtherSettingsOF extends GuiScreen implements GuiYesNoCallback {
   private GuiScreen prevScreen;
   public String title;
   private GameSettings settings;
   private static GameSettings.Options[] enumOptions = new GameSettings.Options[]{
      GameSettings.Options.LAGOMETER,
      GameSettings.Options.PROFILER,
      GameSettings.Options.WEATHER,
      GameSettings.Options.TIME,
      GameSettings.Options.USE_FULLSCREEN,
      GameSettings.Options.FULLSCREEN_MODE,
      GameSettings.Options.SHOW_FPS,
      GameSettings.Options.AUTOSAVE_TICKS,
      GameSettings.Options.ANAGLYPH
   };
   private TooltipProviderOptions N = new TooltipProviderOptions(this);
   private static final long a = 140386729176360L;

   public GuiOtherSettingsOF(GuiScreen var1, GameSettings var2) {
      this.prevScreen = var1;
      this.settings = var2;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.otherTitle");
      this.buttonList.clear();

      for (int var4 = 0; var4 < enumOptions.length; var4++) {
         GameSettings.Options var5 = enumOptions[var4];
         int var6 = this.width / 2 - 155 + var4 % 2 * 160;
         int var7 = this.height / 6 + 21 * (var4 / 2) - 12;
         if (!var5.getEnumFloat()) {
            this.buttonList.add(new GuiOptionButtonOF(var5.returnEnumOrdinal(), var6, var7, var5, this.settings.getKeyBinding(var5)));
         } else {
            this.buttonList.add(new GuiOptionSliderOF(var5.returnEnumOrdinal(), var6, var7, var5));
         }
      }

      this.buttonList.add(new GuiButton(210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44, I18n.format("of.options.other.reset")));
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id < 200 && var1 instanceof GuiOptionButton) {
            this.settings.setOptionValue(((GuiOptionButton)var1).returnEnumOptions(), 1);
            var1.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(var1.id));
         }

         if (var1.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.prevScreen);
         }

         if (var1.id == 210) {
            this.mc.gameSettings.saveOptions();
            GuiYesNo var5 = new GuiYesNo(this, I18n.format("of.message.other.reset"), "", 9999);
            this.mc.displayGuiScreen(var5);
         }
      }
   }

   @Override
   public void confirmClicked(boolean var1, int var2) {
      if (var1) {
         this.mc.gameSettings.resetSettings();
      }

      this.mc.displayGuiScreen(this);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(var1, var2, var3);
      this.N.U(var1, var2, this.buttonList);
   }
}
