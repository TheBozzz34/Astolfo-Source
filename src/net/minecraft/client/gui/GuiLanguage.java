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

   public GuiLanguage(GuiScreen screen, GameSettings gameSettingsObj, LanguageManager manager) {
      this.parentScreen = screen;
      this.game_settings_3 = gameSettingsObj;
      this.languageManager = manager;
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
      this.list.registerScrollButtons(7, 8);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.list.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         switch (button.id) {
            case 5:
               break;
            case 6:
               this.mc.displayGuiScreen(this.parentScreen);
               break;
            case 100:
               if (button instanceof GuiOptionButton) {
                  this.game_settings_3.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
                  button.displayString = this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                  ScaledResolution scaledResolution = new ScaledResolution(this.mc);
                  int i = scaledResolution.getScaledWidth();
                  int j = scaledResolution.getScaledHeight();
                  this.setWorldAndResolution(this.mc, i, j);
               }
               break;
            default:
               this.list.actionPerformed(button);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.list.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, I18n.format("options.language"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRendererObj, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public class List extends GuiSlot {
      private final java.util.List<String> langCodeList = Lists.newArrayList();
      private final Map<String, Language> languageMap = Maps.newHashMap();

      public List(Minecraft mcIn) {
         super(mcIn, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

         for (Language language : GuiLanguage.this.languageManager.getLanguages()) {
            this.languageMap.put(language.getLanguageCode(), language);
            this.langCodeList.add(language.getLanguageCode());
         }
      }

      @Override
      public int getSize() {
         return this.langCodeList.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         Language language2 = this.languageMap.get(this.langCodeList.get(slotIndex));
         GuiLanguage.this.languageManager.setCurrentLanguage(language2);
         GuiLanguage.this.game_settings_3.language = language2.getLanguageCode();
         this.mc.refreshResources();
         GuiLanguage.this.fontRendererObj
            .setUnicodeFlag(GuiLanguage.this.languageManager.isCurrentLocaleUnicode() || GuiLanguage.this.game_settings_3.forceUnicodeFont);
         GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
         GuiLanguage.this.confirmSettingsBtn.displayString = I18n.format("gui.done");
         GuiLanguage.this.forceUnicodeFontBtn.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
         GuiLanguage.this.game_settings_3.saveOptions();
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return this.langCodeList.get(slotIndex).equals(GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode());
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
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         GuiLanguage.this.fontRendererObj.setBidiFlag(true);
         GuiLanguage.this.drawCenteredString(
            GuiLanguage.this.fontRendererObj, this.languageMap.get(this.langCodeList.get(entryID)).toString(), this.width / 2, j + 1, 16777215
         );
         GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional());
      }
   }
}
