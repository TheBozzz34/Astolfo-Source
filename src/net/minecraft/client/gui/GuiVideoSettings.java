package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.src.GuiAnimationSettingsOF;
import net.minecraft.src.GuiDetailSettingsOF;
import net.minecraft.src.GuiOptionButtonOF;
import net.minecraft.src.GuiOptionSliderOF;
import net.minecraft.src.GuiOtherSettingsOF;
import net.minecraft.src.GuiPerformanceSettingsOF;
import net.minecraft.src.GuiQualitySettingsOF;
import net.minecraft.src.Lang;
import net.minecraft.src.TooltipProviderOptions;
import shadersmod.client.GuiShaders;

public class GuiVideoSettings extends GuiScreen {
   private GuiScreen parentGuiScreen;
   public String screenTitle = "Video Settings";
   private GameSettings guiGameSettings;
   private static GameSettings.Options[] videoOptions = new GameSettings.Options[]{
      GameSettings.Options.GRAPHICS,
      GameSettings.Options.RENDER_DISTANCE,
      GameSettings.Options.AMBIENT_OCCLUSION,
      GameSettings.Options.FRAMERATE_LIMIT,
      GameSettings.Options.AO_LEVEL,
      GameSettings.Options.VIEW_BOBBING,
      GameSettings.Options.GUI_SCALE,
      GameSettings.Options.USE_VBO,
      GameSettings.Options.GAMMA,
      GameSettings.Options.BLOCK_ALTERNATIVES,
      GameSettings.Options.FOG_FANCY,
      GameSettings.Options.FOG_START
   };
   private TooltipProviderOptions optionsRowList = new TooltipProviderOptions(this);

   public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
      this.parentGuiScreen = parentScreenIn;
      this.guiGameSettings = gameSettingsIn;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("options.videoTitle");
      this.buttonList.clear();

      for (int i = 0; i < videoOptions.length; i++) {
         GameSettings.Options options2 = videoOptions[i];
         if (options2 != null) {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 + 21 * (i / 2) - 12;
            if (options2.getEnumFloat()) {
               this.buttonList.add(new GuiOptionSliderOF(options2.returnEnumOrdinal(), j, k, options2));
            } else {
               this.buttonList.add(new GuiOptionButtonOF(options2.returnEnumOrdinal(), j, k, options2, this.guiGameSettings.getKeyBinding(options2)));
            }
         }
      }

      int l = this.height / 6 + 21 * (videoOptions.length / 2) - 12;
      int m = 0;
      m = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(231, m, l, Lang.get("of.options.shaders")));
      m = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(202, m, l, Lang.get("of.options.quality")));
      l += 21;
      m = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(201, m, l, Lang.get("of.options.details")));
      m = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(212, m, l, Lang.get("of.options.performance")));
      l += 21;
      m = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(211, m, l, Lang.get("of.options.animations")));
      m = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(222, m, l, Lang.get("of.options.other")));
      l += 21;
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         int i = this.guiGameSettings.guiScale;
         if (button.id < 200 && button instanceof GuiOptionButton) {
            this.guiGameSettings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
            button.displayString = this.guiGameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
         }

         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentGuiScreen);
         }

         if (this.guiGameSettings.guiScale != i) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int j = scaledResolution.getScaledWidth();
            int k = scaledResolution.getScaledHeight();
            this.setWorldAndResolution(this.mc, j, k);
         }

         if (button.id == 201) {
            this.mc.gameSettings.saveOptions();
            GuiDetailSettingsOF guiDetailSettingsOF = new GuiDetailSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiDetailSettingsOF);
         }

         if (button.id == 202) {
            this.mc.gameSettings.saveOptions();
            GuiQualitySettingsOF guiQualitySettingsOF = new GuiQualitySettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiQualitySettingsOF);
         }

         if (button.id == 211) {
            this.mc.gameSettings.saveOptions();
            GuiAnimationSettingsOF guiAnimationSettingsOF = new GuiAnimationSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiAnimationSettingsOF);
         }

         if (button.id == 212) {
            this.mc.gameSettings.saveOptions();
            GuiPerformanceSettingsOF guiPerformanceSettingsOF = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiPerformanceSettingsOF);
         }

         if (button.id == 222) {
            this.mc.gameSettings.saveOptions();
            GuiOtherSettingsOF guiOtherSettingsOF = new GuiOtherSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiOtherSettingsOF);
         }

         if (button.id == 231) {
            if (Config.isAntialiasing() || Config.x()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
               return;
            }

            if (Config.isAnisotropicFiltering()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
               return;
            }

            if (Config.isFastRender()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.fr1"), Lang.get("of.message.shaders.fr2"));
               return;
            }

            this.mc.gameSettings.saveOptions();
            GuiShaders guiShaders = new GuiShaders(this, this.guiGameSettings);
            this.mc.displayGuiScreen(guiShaders);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
      String string = Config.getVersion();
      String string2 = "HD_U";
      if (string2.equals("HD")) {
         string = "OptiFine HD H8";
      }

      if (string2.equals("HD_U")) {
         string = "OptiFine HD H8 Ultra";
      }

      if (string2.equals("L")) {
         string = "OptiFine H8 Light";
      }

      this.drawString(this.fontRendererObj, string, 2, this.height - 10, 8421504);
      String string3 = "Minecraft 1.8.8";
      int i = this.fontRendererObj.getStringWidth(string3);
      this.drawString(this.fontRendererObj, string3, this.width - i - 2, this.height - 10, 8421504);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.optionsRowList.drawTooltips(mouseX, mouseY, this.buttonList);
   }

   public static int getButtonWidth(GuiButton guiButton) {
      return guiButton.width;
   }

   public static int getButtonHeight(GuiButton guiButton) {
      return guiButton.height;
   }

   public static void drawGradientRect(GuiScreen guiScreen, int i, int j, int k, int l, int m, int n) {
      guiScreen.drawGradientRect(i, j, k, l, m, n);
   }
}
