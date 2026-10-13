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
   private static final long a = 101166152679118L;

   public GuiVideoSettings(GuiScreen var1, GameSettings var2) {
      this.parentGuiScreen = var1;
      this.guiGameSettings = var2;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("options.videoTitle");
      this.buttonList.clear();

      for (int var4 = 0; var4 < videoOptions.length; var4++) {
         GameSettings.Options var5 = videoOptions[var4];
         if (var5 != null) {
            int var6 = this.width / 2 - 155 + var4 % 2 * 160;
            int var7 = this.height / 6 + 21 * (var4 / 2) - 12;
            if (var5.getEnumFloat()) {
               this.buttonList.add(new GuiOptionSliderOF(var5.returnEnumOrdinal(), var6, var7, var5));
            } else {
               this.buttonList.add(new GuiOptionButtonOF(var5.returnEnumOrdinal(), var6, var7, var5, this.guiGameSettings.getKeyBinding(var5)));
            }
         }
      }

      int var8 = this.height / 6 + 21 * (videoOptions.length / 2) - 12;
      int var12 = 0;
      var12 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(231, var12, var8, Lang.get("of.options.shaders")));
      var12 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(202, var12, var8, Lang.get("of.options.quality")));
      var8 += 21;
      var12 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(201, var12, var8, Lang.get("of.options.details")));
      var12 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(212, var12, var8, Lang.get("of.options.performance")));
      var8 += 21;
      var12 = this.width / 2 - 155 + 0;
      this.buttonList.add(new GuiOptionButton(211, var12, var8, Lang.get("of.options.animations")));
      var12 = this.width / 2 - 155 + 160;
      this.buttonList.add(new GuiOptionButton(222, var12, var8, Lang.get("of.options.other")));
      var8 += 21;
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         int var5 = this.guiGameSettings.guiScale;
         if (var1.id < 200 && var1 instanceof GuiOptionButton) {
            this.guiGameSettings.setOptionValue(((GuiOptionButton)var1).returnEnumOptions(), 1);
            var1.displayString = this.guiGameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(var1.id));
         }

         if (var1.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentGuiScreen);
         }

         if (this.guiGameSettings.guiScale != var5) {
            ScaledResolution var6 = new ScaledResolution(this.mc);
            int var7 = var6.getScaledWidth();
            int var8 = var6.getScaledHeight();
            this.setWorldAndResolution(this.mc, var7, var8);
         }

         if (var1.id == 201) {
            this.mc.gameSettings.saveOptions();
            GuiDetailSettingsOF var9 = new GuiDetailSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(var9);
         }

         if (var1.id == 202) {
            this.mc.gameSettings.saveOptions();
            GuiQualitySettingsOF var10 = new GuiQualitySettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(var10);
         }

         if (var1.id == 211) {
            this.mc.gameSettings.saveOptions();
            GuiAnimationSettingsOF var11 = new GuiAnimationSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(var11);
         }

         if (var1.id == 212) {
            this.mc.gameSettings.saveOptions();
            GuiPerformanceSettingsOF var12 = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(var12);
         }

         if (var1.id == 222) {
            this.mc.gameSettings.saveOptions();
            GuiOtherSettingsOF var13 = new GuiOtherSettingsOF(this, this.guiGameSettings);
            this.mc.displayGuiScreen(var13);
         }

         if (var1.id == 231) {
            if (Config.isAntialiasing() || Config.x()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
               return;
            }

            if (Config.pL()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
               return;
            }

            if (Config.isFastRender()) {
               Config.showGuiMessage(Lang.get("of.message.shaders.fr1"), Lang.get("of.message.shaders.fr2"));
               return;
            }

            this.mc.gameSettings.saveOptions();
            GuiShaders var14 = new GuiShaders(this, this.guiGameSettings);
            this.mc.displayGuiScreen(var14);
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
      String var7 = Config.getVersion();
      String var8 = "HD_U";
      if (var8.equals("HD")) {
         var7 = "OptiFine HD H8";
      }

      if (var8.equals("HD_U")) {
         var7 = "OptiFine HD H8 Ultra";
      }

      if (var8.equals("L")) {
         var7 = "OptiFine H8 Light";
      }

      this.I(this.fontRendererObj, var7, 2, this.height - 10, 8421504);
      String var9 = "Minecraft 1.8.8";
      int var10 = this.fontRendererObj.getStringWidth(var9);
      this.I(this.fontRendererObj, var9, this.width - var10 - 2, this.height - 10, 8421504);
      super.drawScreen(var1, var2, var3);
      this.optionsRowList.U(var1, var2, this.buttonList);
   }

   public static int j(GuiButton var0) {
      return var0.width;
   }

   public static int V(GuiButton var0) {
      return var0.height;
   }

   public static void drawGradientRect(GuiScreen var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      var0.drawGradientRect(var1, var2, var3, var4, var5, var6);
   }
}
