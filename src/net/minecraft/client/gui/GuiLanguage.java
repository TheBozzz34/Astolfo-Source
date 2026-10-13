package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;

public class GuiLanguage extends GuiScreen {
   public GuiScreen parentScreen;
   private GuiLanguage.List list;
   private final GameSettings game_settings_3;
   private final LanguageManager languageManager;
   private GuiOptionButton forceUnicodeFontBtn;
   private GuiOptionButton confirmSettingsBtn;
   private static final long a = 42616695162694L;

   public GuiLanguage(GuiScreen var1, GameSettings var2, LanguageManager var3) {
      this.parentScreen = var1;
      this.game_settings_3 = var2;
      this.languageManager = var3;
   }

   @Override
   public void initGui() {
      this.buttonList
         .add(
            this.forceUnicodeFontBtn = new GuiOptionButton(
               100,
               this.width / 2 - 155,
               this.height - 38,
               GameSettings.Options.FORCE_UNICODE_FONT,
               this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)
            )
         );
      this.buttonList.add(this.confirmSettingsBtn = new GuiOptionButton(6, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")));
      this.list = new GuiLanguage.List(this.mc);
      this.list.U(7, 8);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.list.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         switch (var1.id) {
            case 5:
               break;
            case 6:
               this.mc.displayGuiScreen(this.parentScreen);
               break;
            case 100:
               if (var1 instanceof GuiOptionButton) {
                  this.game_settings_3.setOptionValue(((GuiOptionButton)var1).returnEnumOptions(), 1);
                  var1.displayString = this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                  ScaledResolution var5 = new ScaledResolution(this.mc);
                  int var6 = var5.getScaledWidth();
                  int var7 = var5.getScaledHeight();
                  this.setWorldAndResolution(this.mc, var6, var7);
               }
               break;
            default:
               this.list.actionPerformed(var1);
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.list.drawScreen(var1, var2, var3);
      this.d(this.fontRendererObj, I18n.format("options.language"), this.width / 2, 16, 16777215);
      this.d(this.fontRendererObj, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.drawScreen(var1, var2, var3);
   }

   public class List extends GuiSlot {
      private final java.util.List<String> langCodeList = Lists.newArrayList();
      private final Map<String, Language> languageMap = Maps.newHashMap();
      private static final long c = 126436463148379L;

      public List(Minecraft var2) {
         super(var2, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

         for (Language var7 : GuiLanguage.this.languageManager.getLanguages()) {
            this.languageMap.put(var7.getLanguageCode(), var7);
            this.langCodeList.add(var7.getLanguageCode());
         }
      }

      @Override
      public int getSize() {
         return this.langCodeList.size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
         Language var8 = this.languageMap.get(this.langCodeList.get(var1));
         GuiLanguage.this.languageManager.setCurrentLanguage(var8);
         GuiLanguage.this.game_settings_3.language = var8.getLanguageCode();
         this.mc.refreshResources();
         GuiLanguage.this.fontRendererObj
            .setUnicodeFlag(GuiLanguage.this.languageManager.isCurrentLocaleUnicode() || GuiLanguage.this.game_settings_3.forceUnicodeFont);
         GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
         GuiLanguage.this.confirmSettingsBtn.displayString = I18n.format("gui.done");
         GuiLanguage.this.forceUnicodeFontBtn.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
         GuiLanguage.this.game_settings_3.saveOptions();
      }

      @Override
      public boolean isSelected(int var1) {
         return this.langCodeList.get(var1).equals(GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode());
      }

      @Override
      public int getContentHeight() {
         return this.getSize() * 18;
      }

      @Override
      public void drawBackground() {
         GuiLanguage.this.drawDefaultBackground();
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         GuiLanguage.this.fontRendererObj.setBidiFlag(true);
         GuiLanguage.this.d(GuiLanguage.this.fontRendererObj, this.languageMap.get(this.langCodeList.get(var1)).toString(), this.width / 2, var3 + 1, 16777215);
         GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional());
      }
   }
}
