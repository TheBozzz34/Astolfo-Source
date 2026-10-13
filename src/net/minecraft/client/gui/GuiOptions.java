package net.minecraft.client.gui;

import bypass.gui.screen.CapeSettingsScreen;
import bypass.gui.screen.CustomResourcePacksScreen;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class GuiOptions extends GuiScreen implements GuiYesNoCallback {
   private static final GameSettings.Options[] field_146440_f = new GameSettings.Options[]{GameSettings.Options.FOV};
   private final GuiScreen field_146441_g;
   private final GameSettings game_settings_1;
   private GuiButton field_175357_i;
   private GuiButton e;
   private GuiLockIconButton field_175356_r;
   public String field_146442_a = "Options";
   private static final long a = 21498478000360L;

   public GuiOptions(GuiScreen var1, GameSettings var2) {
      this.field_146441_g = var1;
      this.game_settings_1 = var2;
   }

   @Override
   public void initGui() {
      int var5 = 0;
      this.field_146442_a = I18n.format("options.title");

      for (GameSettings.Options var9 : field_146440_f) {
         if (var9.getEnumFloat()) {
            this.buttonList
               .add(new GuiOptionSlider(var9.returnEnumOrdinal(), this.width / 2 - 155 + var5 % 2 * 160, this.height / 6 - 12 + 24 * (var5 >> 1), var9));
         } else {
            GuiOptionButton var10 = new GuiOptionButton(
               var9.returnEnumOrdinal(),
               this.width / 2 - 155 + var5 % 2 * 160,
               this.height / 6 - 12 + 24 * (var5 >> 1),
               var9,
               this.game_settings_1.getKeyBinding(var9)
            );
            this.buttonList.add(var10);
         }

         var5++;
      }

      if (this.mc.theWorld != null) {
         EnumDifficulty var11 = this.mc.theWorld.getDifficulty();
         this.field_175357_i = new GuiButton(
            108, this.width / 2 - 155 + var5 % 2 * 160, this.height / 6 - 12 + 24 * (var5 >> 1), 150, 20, this.func_175355_a(var11)
         );
         this.buttonList.add(this.field_175357_i);
         if (this.mc.isSingleplayer() && !this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            this.field_175357_i.setWidth(this.field_175357_i.getButtonWidth() - 20);
            this.field_175356_r = new GuiLockIconButton(
               109, this.field_175357_i.xPosition + this.field_175357_i.getButtonWidth(), this.field_175357_i.yPosition
            );
            this.buttonList.add(this.field_175356_r);
            this.field_175356_r.func_175229_b(this.mc.theWorld.getWorldInfo().isDifficultyLocked());
            this.field_175356_r.enabled = !this.field_175356_r.func_175230_c();
            this.field_175357_i.enabled = !this.field_175356_r.func_175230_c();
         } else {
            this.field_175357_i.enabled = false;
         }
      }

      this.buttonList.add(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")));
      this.buttonList
         .add(
            new GuiButton(8675309, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Super Secret Settings...") {
               private static final long c = 18288522768106L;

               @Override
               public void playPressSound(SoundHandler var1) {
                  SoundEventAccessorComposite var4 = var1.getRandomSoundFromCategories(
                     SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER
                  );
                  if (var4 != null) {
                     var1.playSound(PositionedSoundRecord.create(var4.getSoundEventLocation(), 0.5F));
                  }
               }
            }
         );
      this.buttonList.add(new GuiButton(106, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")));
      this.buttonList.add(new GuiButton(107, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.stream")));
      this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.video")));
      this.buttonList.add(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.controls")));
      this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.language")));
      this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.chat.title")));
      this.buttonList.add(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.resourcepack")));
      this.buttonList.add(new GuiButton(104, this.width / 2 + 5, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.snooper.view")));
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
      this.buttonList.add(this.e = new GuiButton(62727568, 2, this.height - 20, 100, 20, "Optifine Cape"));
      if (Minecraft.getMinecraft().theWorld == null) {
         this.e.visible = false;
      }
   }

   public String func_175355_a(EnumDifficulty var1) {
      ChatComponentText var4 = new ChatComponentText("");
      var4.appendSibling(new ChatComponentTranslation("options.difficulty"));
      var4.appendText(": ");
      var4.appendSibling(new ChatComponentTranslation(var1.getDifficultyResourceKey()));
      return var4.getFormattedText();
   }

   @Override
   public void confirmClicked(boolean var1, int var2) {
      this.mc.displayGuiScreen(this);
      if (var2 == 109 && var1 && this.mc.theWorld != null) {
         this.mc.theWorld.getWorldInfo().setDifficultyLocked(true);
         this.field_175356_r.func_175229_b(true);
         this.field_175356_r.enabled = false;
         this.field_175357_i.enabled = false;
      }
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id < 100 && var1 instanceof GuiOptionButton) {
            GameSettings.Options var5 = ((GuiOptionButton)var1).returnEnumOptions();
            this.game_settings_1.setOptionValue(var5, 1);
            var1.displayString = this.game_settings_1.getKeyBinding(GameSettings.Options.getEnumOptions(var1.id));
         }

         if (var1.id == 108) {
            this.mc.theWorld.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(this.mc.theWorld.getDifficulty().getDifficultyId() + 1));
            this.field_175357_i.displayString = this.func_175355_a(this.mc.theWorld.getDifficulty());
         }

         if (var1.id == 109) {
            this.mc
               .displayGuiScreen(
                  new GuiYesNo(
                     this,
                     new ChatComponentTranslation("difficulty.lock.title").getFormattedText(),
                     new ChatComponentTranslation(
                           "difficulty.lock.question", new ChatComponentTranslation(this.mc.theWorld.getWorldInfo().getDifficulty().getDifficultyResourceKey())
                        )
                        .getFormattedText(),
                     109
                  )
               );
         }

         if (var1.id == 110) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
         }

         if (var1.id == 8675309) {
            this.mc.entityRenderer.D();
         }

         if (var1.id == 101) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiVideoSettings(this, this.game_settings_1));
         }

         if (var1.id == 100) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiControls(this, this.game_settings_1));
         }

         if (var1.id == 102) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiLanguage(this, this.game_settings_1, this.mc.getLanguageManager()));
         }

         if (var1.id == 103) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new ScreenChatOptions(this, this.game_settings_1));
         }

         if (var1.id == 104) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiSnooper(this, this.game_settings_1));
         }

         if (var1.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.field_146441_g);
         }

         if (var1.id == 105) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new CustomResourcePacksScreen(this));
         }

         if (var1.id == 106) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiScreenOptionsSounds(this, this.game_settings_1));
         }
      }

      if (var1.id == 62727568 && this instanceof GuiOptions) {
         Minecraft.getMinecraft().displayGuiScreen(new CapeSettingsScreen(this));
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.field_146442_a, this.width / 2, 15, 16777215);
      super.drawScreen(var1, var2, var3);
   }
}
