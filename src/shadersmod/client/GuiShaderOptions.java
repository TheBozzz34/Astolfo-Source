package shadersmod.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.src.GuiScreenOF;
import net.minecraft.src.Lang;
import net.minecraft.src.StrUtils;

public class GuiShaderOptions extends GuiScreenOF {
   private GuiScreen prevScreen;
   public String title;
   private GameSettings settings;
   private int lastMouseX = 0;
   private int lastMouseY = 0;
   private long mouseStillTime = 0L;
   private String screenName = null;
   private String screenText = null;
   private boolean changed = false;
   public static final String OPTION_PROFILE = "<profile>";
   public static final String OPTION_EMPTY = "<empty>";
   public static final String OPTION_REST = "*";

   public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings) {
      this.title = "Shader Options";
      this.prevScreen = guiscreen;
      this.settings = gamesettings;
   }

   public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings, String screenName) {
      this(guiscreen, gamesettings);
      this.screenName = screenName;
      if (screenName != null) {
         this.screenText = Shaders.translate("screen." + screenName, screenName);
      }
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.shaderOptionsTitle");
      byte b = 100;
      int i = 0;
      byte b2 = 30;
      byte b3 = 20;
      int j = this.width - 130;
      byte b4 = 120;
      byte b5 = 20;
      int k = 2;
      ShaderOption[] shaderOptions = Shaders.getShaderPackOptions(this.screenName);
      if (shaderOptions != null) {
         if (shaderOptions.length > 18) {
            k = shaderOptions.length / 9 + 1;
         }

         for (int l = 0; l < shaderOptions.length; l++) {
            ShaderOption shaderOption = shaderOptions[l];
            if (shaderOption != null && shaderOption.isVisible()) {
               int m = l % k;
               int n = l / k;
               int i2 = Math.min(this.width / k, 200);
               i = (this.width - i2 * k) / 2;
               int i3 = m * i2 + 5 + i;
               int i4 = b2 + n * b3;
               int i5 = i2 - 10;
               String string = this.getButtonText(shaderOption, i5);
               GuiButtonShaderOption guiButtonShaderOption = new GuiButtonShaderOption(b + l, i3, i4, i5, b5, shaderOption, string);
               guiButtonShaderOption.enabled = shaderOption.isEnabled();
               this.buttonList.add(guiButtonShaderOption);
            }
         }
      }

      this.buttonList.add(new GuiButton(201, this.width / 2 - b4 - 20, this.height / 6 + 168 + 11, b4, b5, I18n.format("controls.reset")));
      this.buttonList.add(new GuiButton(200, this.width / 2 + 20, this.height / 6 + 168 + 11, b4, b5, I18n.format("gui.done")));
   }

   private String getButtonText(ShaderOption shaderOption, int i) {
      String string = shaderOption.getNameText();
      if (shaderOption instanceof ShaderOptionScreen) {
         ShaderOptionScreen shaderOptionScreen = (ShaderOptionScreen)shaderOption;
         return string + "...";
      }

      FontRenderer fontRenderer = Config.getMinecraft().fontRendererObj;
      int j = fontRenderer.getStringWidth(": " + Lang.getOff()) + 5;

      while (fontRenderer.getStringWidth(string) + j >= i && string.length() > 0) {
         string = string.substring(0, string.length() - 1);
      }

      String string2 = shaderOption.isChanged() ? shaderOption.getValueColor(shaderOption.getValue()) : "";
      String string3 = shaderOption.getValueText(shaderOption.getValue());
      return string + ": " + string2 + string3;
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button.id < 200 && button instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)button;
            ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
            if (shaderOption instanceof ShaderOptionScreen) {
               String string = shaderOption.getName();
               GuiShaderOptions guiShaderOptions = new GuiShaderOptions(this, this.settings, string);
               this.mc.displayGuiScreen(guiShaderOptions);
               return;
            }

            if (isShiftKeyDown()) {
               shaderOption.resetValue();
            } else {
               shaderOption.nextValue();
            }

            this.updateAllButtons();
            this.changed = true;
         }

         if (button.id == 201) {
            ShaderOption[] shaderOptions = Shaders.getChangedOptions(Shaders.getShaderPackOptions());

            for (int i = 0; i < shaderOptions.length; i++) {
               ShaderOption shaderOption2 = shaderOptions[i];
               shaderOption2.resetValue();
               this.changed = true;
            }

            this.updateAllButtons();
         }

         if (button.id == 200) {
            if (this.changed) {
               Shaders.saveShaderPackOptions();
               this.changed = false;
               Shaders.uninit();
            }

            this.mc.displayGuiScreen(this.prevScreen);
         }
      }
   }

   @Override
   public void actionPerformedRightClick(GuiButton btn) {
      if (btn instanceof GuiButtonShaderOption) {
         GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)btn;
         ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
         if (isShiftKeyDown()) {
            shaderOption.resetValue();
         } else {
            shaderOption.prevValue();
         }

         this.updateAllButtons();
         this.changed = true;
      }
   }

   @Override
   public void onGuiClosed() {
      super.onGuiClosed();
      if (this.changed) {
         Shaders.saveShaderPackOptions();
         this.changed = false;
         Shaders.uninit();
      }
   }

   private void updateAllButtons() {
      for (GuiButton guiButton : this.buttonList) {
         if (guiButton instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)guiButton;
            ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
            if (shaderOption instanceof ShaderOptionProfile) {
               ShaderOptionProfile shaderOptionProfile = (ShaderOptionProfile)shaderOption;
               shaderOptionProfile.updateProfile();
            }

            guiButtonShaderOption.displayString = this.getButtonText(shaderOption, guiButtonShaderOption.getButtonWidth());
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      if (this.screenText != null) {
         this.drawCenteredString(this.fontRendererObj, this.screenText, this.width / 2, 15, 16777215);
      } else {
         this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      if (Math.abs(mouseX - this.lastMouseX) <= 5 && Math.abs(mouseY - this.lastMouseY) <= 5) {
         this.drawTooltips(mouseX, mouseY, this.buttonList);
      } else {
         this.lastMouseX = mouseX;
         this.lastMouseY = mouseY;
         this.mouseStillTime = System.currentTimeMillis();
      }
   }

   private void drawTooltips(int i, int j, List list) {
      short s = 700;
      if (System.currentTimeMillis() >= this.mouseStillTime + s) {
         int k = this.width / 2 - 150;
         int l = this.height / 6 - 7;
         if (j <= l + 98) {
            l += 105;
         }

         int m = k + 150 + 150;
         int n = l + 84 + 10;
         GuiButton guiButton = getSelectedButton(list, i, j);
         if (guiButton instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)guiButton;
            ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
            String[] strings = this.makeTooltipLines(shaderOption, m - k);
            if (strings == null) {
               return;
            }

            this.drawGradientRect(k, l, m, n, -536870912, -536870912);

            for (int i2 = 0; i2 < strings.length; i2++) {
               String string = strings[i2];
               int i3 = 14540253;
               if (string.endsWith("!")) {
                  i3 = 16719904;
               }

               this.fontRendererObj.drawStringWithShadow(string, k + 5, l + 5 + i2 * 11, i3);
            }
         }
      }
   }

   private String[] makeTooltipLines(ShaderOption shaderOption, int i) {
      if (shaderOption instanceof ShaderOptionProfile) {
         return null;
      }

      String string = shaderOption.getNameText();
      String string2 = Config.normalize(shaderOption.getDescriptionText()).trim();
      String[] strings = this.splitDescription(string2);
      String string3 = null;
      if (!string.equals(shaderOption.getName()) && this.settings.advancedItemTooltips) {
         string3 = "§8" + Lang.get("of.general.id") + ": " + shaderOption.getName();
      }

      String string4 = null;
      if (shaderOption.getPaths() != null && this.settings.advancedItemTooltips) {
         string4 = "§8" + Lang.get("of.general.from") + ": " + Config.arrayToString(shaderOption.getPaths());
      }

      String string5 = null;
      if (shaderOption.getValueDefault() != null && this.settings.advancedItemTooltips) {
         String string6 = shaderOption.isEnabled() ? shaderOption.getValueText(shaderOption.getValueDefault()) : Lang.get("of.general.ambiguous");
         string5 = "§8" + Lang.getDefault() + ": " + string6;
      }

      ArrayList arrayList = new ArrayList();
      arrayList.add(string);
      arrayList.addAll(Arrays.asList(strings));
      if (string3 != null) {
         arrayList.add(string3);
      }

      if (string4 != null) {
         arrayList.add(string4);
      }

      if (string5 != null) {
         arrayList.add(string5);
      }

      return this.wrapTooltipLines(i, arrayList);
   }

   private String[] splitDescription(String string) {
      if (string.length() <= 0) {
         return new String[0];
      }

      string = StrUtils.removePrefix(string, "//");
      String[] strings = string.split("\\. ");

      for (int i = 0; i < strings.length; i++) {
         strings[i] = "- " + strings[i].trim();
         strings[i] = StrUtils.removeSuffix(strings[i], ".");
      }

      return strings;
   }

   private String[] wrapTooltipLines(int i, List<String> list) {
      FontRenderer fontRenderer = Config.getMinecraft().fontRendererObj;
      ArrayList arrayList = new ArrayList();

      for (int j = 0; j < list.size(); j++) {
         String string = (String)list.get(j);
         if (string != null && string.length() > 0) {
            for (Object object : fontRenderer.listFormattedStringToWidth(string, i)) {
               arrayList.add((String)object);
            }
         }
      }

      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }
}
