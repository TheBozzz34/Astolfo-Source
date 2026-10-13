package net.minecraft.src;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiDetailSettingsOF extends GuiScreen {
   private GuiScreen prevScreen;
   public String title;
   private GameSettings settings;
   private static GameSettings.Options[] enumOptions = new GameSettings.Options[]{
      GameSettings.Options.CLOUDS,
      GameSettings.Options.CLOUD_HEIGHT,
      GameSettings.Options.TREES,
      GameSettings.Options.RAIN,
      GameSettings.Options.SKY,
      GameSettings.Options.STARS,
      GameSettings.Options.SUN_MOON,
      GameSettings.Options.SHOW_CAPES,
      GameSettings.Options.TRANSLUCENT_BLOCKS,
      GameSettings.Options.HELD_ITEM_TOOLTIPS,
      GameSettings.Options.DROPPED_ITEMS,
      GameSettings.Options.ENTITY_SHADOWS,
      GameSettings.Options.VIGNETTE,
      GameSettings.Options.DYNAMIC_FOV
   };
   private TooltipProviderOptions O = new TooltipProviderOptions(this);
   private static final long a = 109500055905928L;

   public GuiDetailSettingsOF(GuiScreen var1, GameSettings var2) {
      this.prevScreen = var1;
      this.settings = var2;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.detailsTitle");
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
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(var1, var2, var3);
      this.O.U(var1, var2, this.buttonList);
   }
}
