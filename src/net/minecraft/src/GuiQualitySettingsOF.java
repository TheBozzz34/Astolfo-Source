package net.minecraft.src;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiQualitySettingsOF extends GuiScreen {
   private GuiScreen prevScreen;
   public String title;
   private GameSettings settings;
   private static GameSettings.Options[] enumOptions = new GameSettings.Options[]{
      GameSettings.Options.MIPMAP_LEVELS,
      GameSettings.Options.MIPMAP_TYPE,
      GameSettings.Options.AF_LEVEL,
      GameSettings.Options.AA_LEVEL,
      GameSettings.Options.CLEAR_WATER,
      GameSettings.Options.RANDOM_MOBS,
      GameSettings.Options.BETTER_GRASS,
      GameSettings.Options.BETTER_SNOW,
      GameSettings.Options.CUSTOM_FONTS,
      GameSettings.Options.CUSTOM_COLORS,
      GameSettings.Options.SWAMP_COLORS,
      GameSettings.Options.SMOOTH_BIOMES,
      GameSettings.Options.CONNECTED_TEXTURES,
      GameSettings.Options.NATURAL_TEXTURES,
      GameSettings.Options.CUSTOM_SKY,
      GameSettings.Options.CUSTOM_ITEMS,
      GameSettings.Options.DYNAMIC_LIGHTS
   };
   private TooltipProviderOptions tooltipManager = new TooltipProviderOptions(this);

   public GuiQualitySettingsOF(GuiScreen guiscreen, GameSettings gamesettings) {
      this.prevScreen = guiscreen;
      this.settings = gamesettings;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.qualityTitle");
      this.buttonList.clear();

      for (int i = 0; i < enumOptions.length; i++) {
         GameSettings.Options options2 = enumOptions[i];
         int j = this.width / 2 - 155 + i % 2 * 160;
         int k = this.height / 6 + 21 * (i / 2) - 12;
         if (!options2.getEnumFloat()) {
            this.buttonList.add(new GuiOptionButtonOF(options2.returnEnumOrdinal(), j, k, options2, this.settings.getKeyBinding(options2)));
         } else {
            this.buttonList.add(new GuiOptionSliderOF(options2.returnEnumOrdinal(), j, k, options2));
         }
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button.id < 200 && button instanceof GuiOptionButton) {
            this.settings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
            button.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
         }

         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.prevScreen);
         }

         if (button.id != GameSettings.Options.AA_LEVEL.ordinal()) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            this.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
   }
}
