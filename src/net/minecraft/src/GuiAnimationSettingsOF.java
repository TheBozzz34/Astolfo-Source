package net.minecraft.src;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiAnimationSettingsOF extends GuiScreen {
   private GuiScreen prevScreen;
   public String title;
   private GameSettings settings;
   private static GameSettings.Options[] enumOptions = new GameSettings.Options[]{
      GameSettings.Options.ANIMATED_WATER,
      GameSettings.Options.ANIMATED_LAVA,
      GameSettings.Options.ANIMATED_FIRE,
      GameSettings.Options.ANIMATED_PORTAL,
      GameSettings.Options.ANIMATED_REDSTONE,
      GameSettings.Options.ANIMATED_EXPLOSION,
      GameSettings.Options.ANIMATED_FLAME,
      GameSettings.Options.ANIMATED_SMOKE,
      GameSettings.Options.VOID_PARTICLES,
      GameSettings.Options.WATER_PARTICLES,
      GameSettings.Options.RAIN_SPLASH,
      GameSettings.Options.PORTAL_PARTICLES,
      GameSettings.Options.POTION_PARTICLES,
      GameSettings.Options.DRIPPING_WATER_LAVA,
      GameSettings.Options.ANIMATED_TERRAIN,
      GameSettings.Options.ANIMATED_TEXTURES,
      GameSettings.Options.FIREWORK_PARTICLES,
      GameSettings.Options.PARTICLES
   };
   private static final long a = 63046687236957L;

   public GuiAnimationSettingsOF(GuiScreen var1, GameSettings var2) {
      this.prevScreen = var1;
      this.settings = var2;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.animationsTitle");
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

      this.buttonList.add(new GuiButton(210, this.width / 2 - 155, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOn")));
      this.buttonList.add(new GuiButton(211, this.width / 2 - 155 + 80, this.height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOff")));
      this.buttonList.add(new GuiOptionButton(200, this.width / 2 + 5, this.height / 6 + 168 + 11, I18n.format("gui.done")));
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
            this.mc.gameSettings.setAllAnimations(true);
         }

         if (var1.id == 211) {
            this.mc.gameSettings.setAllAnimations(false);
         }

         ScaledResolution var5 = new ScaledResolution(this.mc);
         this.setWorldAndResolution(this.mc, var5.getScaledWidth(), var5.getScaledHeight());
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(var1, var2, var3);
   }
}
