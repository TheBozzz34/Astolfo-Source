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
   private int N = 0;
   private int b = 0;
   private long i = 0L;
   private String screenName = null;
   private String screenText = null;
   private boolean changed = false;
   public static final String OPTION_PROFILE = "<profile>";
   public static final String OPTION_EMPTY = "<empty>";
   public static final String OPTION_REST = "*";
   private static final long c = 59075150039634L;

   public GuiShaderOptions(GuiScreen var1, GameSettings var2) {
      this.title = "Shader Options";
      this.prevScreen = var1;
      this.settings = var2;
   }

   public GuiShaderOptions(GuiScreen var1, GameSettings var2, String var3) {
      this(var1, var2);
      this.screenName = var3;
      if (var3 != null) {
         this.screenText = Shaders.translate("screen." + var3, var3);
      }
   }

   @Override
   public void initGui() {
      this.title = I18n.format("of.options.shaderOptionsTitle");
      byte var4 = 100;
      int var5 = 0;
      byte var6 = 30;
      byte var7 = 20;
      int var8 = this.width - 130;
      byte var9 = 120;
      byte var10 = 20;
      int var11 = 2;
      ShaderOption[] var12 = Shaders.getShaderPackOptions(this.screenName);
      if (var12 != null) {
         if (var12.length > 18) {
            var11 = var12.length / 9 + 1;
         }

         for (int var13 = 0; var13 < var12.length; var13++) {
            ShaderOption var14 = var12[var13];
            if (var14 != null && var14.V()) {
               int var15 = var13 % var11;
               int var16 = var13 / var11;
               int var17 = Math.min(this.width / var11, 200);
               var5 = (this.width - var17 * var11) / 2;
               int var18 = var15 * var17 + 5 + var5;
               int var19 = var6 + var16 * var7;
               int var20 = var17 - 10;
               String var21 = this.o(var14, var20);
               GuiButtonShaderOption var22 = new GuiButtonShaderOption(var4 + var13, var18, var19, var20, var10, var14, var21);
               var22.enabled = var14.isEnabled();
               this.buttonList.add(var22);
            }
         }
      }

      this.buttonList.add(new GuiButton(201, this.width / 2 - var9 - 20, this.height / 6 + 168 + 11, var9, var10, I18n.format("controls.reset")));
      this.buttonList.add(new GuiButton(200, this.width / 2 + 20, this.height / 6 + 168 + 11, var9, var10, I18n.format("gui.done")));
   }

   private String o(ShaderOption var1, int var2) {
      String var6 = var1.getNameText();
      if (var1 instanceof ShaderOptionScreen) {
         ShaderOptionScreen var10 = (ShaderOptionScreen)var1;
         return var6 + "...";
      }

      FontRenderer var7 = Config.getMinecraft().fontRendererObj;
      int var8 = var7.getStringWidth(": " + Lang.getOff()) + 5;

      while (var7.getStringWidth(var6) + var8 >= var2 && var6.length() > 0) {
         var6 = var6.substring(0, var6.length() - 1);
      }

      String var11 = var1.isChanged() ? var1.getValueColor(var1.getValue()) : "";
      String var9 = var1.getValueText(var1.getValue());
      return var6 + ": " + var11 + var9;
   }

   @Override
   public void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1.id < 200 && var1 instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption var5 = (GuiButtonShaderOption)var1;
            ShaderOption var6 = var5.getShaderOption();
            if (var6 instanceof ShaderOptionScreen) {
               String var11 = var6.getName();
               GuiShaderOptions var8 = new GuiShaderOptions(this, this.settings, var11);
               this.mc.displayGuiScreen(var8);
               return;
            }

            if (isShiftKeyDown()) {
               var6.resetValue();
            } else {
               var6.nextValue();
            }

            this.updateAllButtons();
            this.changed = true;
         }

         if (var1.id == 201) {
            ShaderOption[] var9 = Shaders.getChangedOptions(Shaders.getShaderPackOptions());

            for (int var10 = 0; var10 < var9.length; var10++) {
               ShaderOption var7 = var9[var10];
               var7.resetValue();
               this.changed = true;
            }

            this.updateAllButtons();
         }

         if (var1.id == 200) {
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
   public void actionPerformedRightClick(GuiButton var1) {
      if (var1 instanceof GuiButtonShaderOption) {
         GuiButtonShaderOption var5 = (GuiButtonShaderOption)var1;
         ShaderOption var6 = var5.getShaderOption();
         if (isShiftKeyDown()) {
            var6.resetValue();
         } else {
            var6.B();
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
      for (GuiButton var5 : this.buttonList) {
         if (var5 instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption var6 = (GuiButtonShaderOption)var5;
            ShaderOption var7 = var6.getShaderOption();
            if (var7 instanceof ShaderOptionProfile) {
               ShaderOptionProfile var8 = (ShaderOptionProfile)var7;
               var8.F();
            }

            var6.displayString = this.o(var7, var6.getButtonWidth());
         }
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      if (this.screenText != null) {
         this.d(this.fontRendererObj, this.screenText, this.width / 2, 15, 16777215);
      } else {
         this.d(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      }

      super.drawScreen(var1, var2, var3);
      if (Math.abs(var1 - this.N) <= 5 && Math.abs(var2 - this.b) <= 5) {
         this.a(var1, var2, this.buttonList);
      } else {
         this.N = var1;
         this.b = var2;
         this.i = System.currentTimeMillis();
      }
   }

   private void a(int var1, int var2, List var3) {
      short var7 = 700;
      if (System.currentTimeMillis() >= this.i + var7) {
         int var8 = this.width / 2 - 150;
         int var9 = this.height / 6 - 7;
         if (var2 <= var9 + 98) {
            var9 += 105;
         }

         int var10 = var8 + 150 + 150;
         int var11 = var9 + 84 + 10;
         GuiButton var12 = Y(var3, var1, var2);
         if (var12 instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption var13 = (GuiButtonShaderOption)var12;
            ShaderOption var14 = var13.getShaderOption();
            String[] var15 = this.T(var14, var10 - var8);
            if (var15 == null) {
               return;
            }

            this.drawGradientRect(var8, var9, var10, var11, -536870912, -536870912);

            for (int var16 = 0; var16 < var15.length; var16++) {
               String var17 = var15[var16];
               int var18 = 14540253;
               if (var17.endsWith("!")) {
                  var18 = 16719904;
               }

               this.fontRendererObj.drawStringWithShadow(var17, var8 + 5, var9 + 5 + var16 * 11, var18);
            }
         }
      }
   }

   private String[] T(ShaderOption var1, int var2) {
      if (var1 instanceof ShaderOptionProfile) {
         return null;
      }

      String var6 = var1.getNameText();
      String var7 = Config.normalize(var1.getDescriptionText()).trim();
      String[] var8 = this.K(var7);
      String var9 = null;
      if (!var6.equals(var1.getName()) && this.settings.advancedItemTooltips) {
         var9 = "§8" + Lang.get("of.general.id") + ": " + var1.getName();
      }

      String var10 = null;
      if (var1.getPaths() != null && this.settings.advancedItemTooltips) {
         var10 = "§8" + Lang.get("of.general.from") + ": " + Config.arrayToString(var1.getPaths());
      }

      String var11 = null;
      if (var1.getValueDefault() != null && this.settings.advancedItemTooltips) {
         String var12 = var1.isEnabled() ? var1.getValueText(var1.getValueDefault()) : Lang.get("of.general.ambiguous");
         var11 = "§8" + Lang.getDefault() + ": " + var12;
      }

      ArrayList var14 = new ArrayList();
      var14.add(var6);
      var14.addAll(Arrays.asList(var8));
      if (var9 != null) {
         var14.add(var9);
      }

      if (var10 != null) {
         var14.add(var10);
      }

      if (var11 != null) {
         var14.add(var11);
      }

      return this.L(var2, var14);
   }

   private String[] K(String var1) {
      if (var1.length() <= 0) {
         return new String[0];
      }

      var1 = StrUtils.removePrefix(var1, "//");
      String[] var5 = var1.split("\\. ");

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = "- " + var5[var6].trim();
         var5[var6] = StrUtils.removeSuffix(var5[var6], ".");
      }

      return var5;
   }

   private String[] L(int var1, List<String> var2) {
      FontRenderer var6 = Config.getMinecraft().fontRendererObj;
      ArrayList var7 = new ArrayList();

      for (int var8 = 0; var8 < var2.size(); var8++) {
         String var9 = (String)var2.get(var8);
         if (var9 != null && var9.length() > 0) {
            for (Object var11 : var6.listFormattedStringToWidth(var9, var1)) {
               var7.add((String)var11);
            }
         }
      }

      return (java.lang.String[])(var7.toArray(new String[var7.size()]));
   }
}
