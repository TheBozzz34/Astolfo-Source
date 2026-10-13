package shadersmod.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.src.Lang;
import org.lwjgl.Sys;

public class GuiShaders extends GuiScreen {
   public GuiScreen parentGui;
   public String screenTitle = "Shaders";
   private int updateTimer = -1;
   private GuiShaders.zo shaderList;
   private boolean saved = false;
   private static float[] QUALITY_MULTIPLIERS = new float[]{0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F};
   private static String[] QUALITY_MULTIPLIER_NAMES = new String[]{"0.5x", "0.7x", "1x", "1.5x", "2x"};
   private static float[] HAND_DEPTH_VALUES = new float[]{0.0625F, 0.125F, 0.25F};
   private static String[] HAND_DEPTH_NAMES = new String[]{"0.5x", "1x", "2x"};
   public static final int EnumOS_UNKNOWN = 0;
   public static final int EnumOS_WINDOWS = 1;
   public static final int EnumOS_OSX = 2;
   public static final int EnumOS_SOLARIS = 3;
   public static final int EnumOS_LINUX = 4;
   private static final long a = 19177304600223L;

   public GuiShaders(GuiScreen var1, GameSettings var2) {
      this.parentGui = var1;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("of.options.shadersTitle");
      if (Shaders.shadersConfig == null) {
         Shaders.loadConfig();
      }

      byte var3 = 120;
      byte var4 = 20;
      int var5 = this.width - var3 - 10;
      byte var6 = 30;
      byte var7 = 20;
      int var8 = this.width - var3 - 20;
      this.shaderList = new GuiShaders.zo(this, var8, this.height, var6, this.height - 50, 16);
      this.shaderList.U(7, 8);
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, var5, 0 * var7 + var6, var3, var4));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, var5, 1 * var7 + var6, var3, var4));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, var5, 2 * var7 + var6, var3, var4));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, var5, 3 * var7 + var6, var3, var4));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, var5, 4 * var7 + var6, var3, var4));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, var5, 5 * var7 + var6, var3, var4));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_HAND_LIGHT, var5, 6 * var7 + var6, var3, var4));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, var5, 7 * var7 + var6, var3, var4));
      int var9 = Math.min(150, var8 / 2 - 10);
      this.buttonList.add(new GuiButton(201, var8 / 4 - var9 / 2, this.height - 25, var9, var4, Lang.get("of.options.shaders.shadersFolder")));
      this.buttonList.add(new GuiButton(202, var8 / 4 * 3 - var9 / 2, this.height - 25, var9, var4, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(203, var5, this.height - 25, var3, var4, Lang.get("of.options.shaders.shaderOptions")));
      this.updateButtons();
   }

   public void updateButtons() {
      boolean var4 = Config.isShaders();

      for (GuiButton var6 : this.buttonList) {
         if (var6.id != 201 && var6.id != 202 && var6.id != EnumShaderOption.ANTIALIASING.ordinal()) {
            var6.enabled = var4;
         }
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.shaderList.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton var1) {
      if (var1.enabled) {
         if (var1 instanceof GuiButtonEnumShaderOption) {
            GuiButtonEnumShaderOption var5 = (GuiButtonEnumShaderOption)var1;
            switch (var5.getEnumShaderOption()) {
               case ANTIALIASING:
                  Shaders.p();
                  Shaders.uninit();
                  break;
               case NORMAL_MAP:
                  Shaders.configNormalMap = !Shaders.configNormalMap;
                  this.mc.scheduleResourcesRefresh();
                  break;
               case SPECULAR_MAP:
                  Shaders.configSpecularMap = !Shaders.configSpecularMap;
                  this.mc.scheduleResourcesRefresh();
                  break;
               case RENDER_RES_MUL:
                  float var6 = Shaders.configRenderResMul;
                  float[] var7 = QUALITY_MULTIPLIERS;
                  String[] var8 = QUALITY_MULTIPLIER_NAMES;
                  int var9 = getValueIndex(var6, var7);
                  if (isShiftKeyDown()) {
                     if (--var9 < 0) {
                        var9 = var7.length - 1;
                     }
                  } else if (++var9 >= var7.length) {
                     var9 = 0;
                  }

                  Shaders.configRenderResMul = var7[var9];
                  Shaders.L();
                  break;
               case SHADOW_RES_MUL:
                  float var10 = Shaders.configShadowResMul;
                  float[] var11 = QUALITY_MULTIPLIERS;
                  String[] var12 = QUALITY_MULTIPLIER_NAMES;
                  int var13 = getValueIndex(var10, var11);
                  if (isShiftKeyDown()) {
                     if (--var13 < 0) {
                        var13 = var11.length - 1;
                     }
                  } else if (++var13 >= var11.length) {
                     var13 = 0;
                  }

                  Shaders.configShadowResMul = var11[var13];
                  Shaders.Ue();
                  break;
               case HAND_DEPTH_MUL:
                  float var14 = Shaders.configHandDepthMul;
                  float[] var15 = HAND_DEPTH_VALUES;
                  String[] var16 = HAND_DEPTH_NAMES;
                  int var17 = getValueIndex(var14, var15);
                  if (isShiftKeyDown()) {
                     if (--var17 < 0) {
                        var17 = var15.length - 1;
                     }
                  } else if (++var17 >= var15.length) {
                     var17 = 0;
                  }

                  Shaders.configHandDepthMul = var15[var17];
                  break;
               case CLOUD_SHADOW:
                  Shaders.configCloudShadow = !Shaders.configCloudShadow;
                  break;
               case OLD_HAND_LIGHT:
                  Shaders.configOldHandLight.N();
                  break;
               case OLD_LIGHTING:
                  Shaders.configOldLighting.N();
                  Shaders.updateBlockLightLevel();
                  this.mc.scheduleResourcesRefresh();
                  break;
               case TWEAK_BLOCK_DAMAGE:
                  Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                  break;
               case TEX_MIN_FIL_B:
                  Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                  Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                  var1.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case TEX_MAG_FIL_N:
                  Shaders.ub = (Shaders.ub + 1) % 2;
                  var1.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.ub];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case TEX_MAG_FIL_S:
                  Shaders.L = (Shaders.L + 1) % 2;
                  var1.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.L];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case SHADOW_CLIP_FRUSTRUM:
                  Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                  var1.displayString = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                  ShadersTex.updateTextureMinMagFilter();
            }

            var5.updateButtonText();
         } else {
            switch (var1.id) {
               case 201:
                  switch (getOSType()) {
                     case 1:
                        String var21 = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());

                        try {
                           Runtime.getRuntime().exec(var21);
                           return;
                        } catch (IOException var20) {
                           var20.printStackTrace();
                           break;
                        }
                     case 2:
                        try {
                           Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath()});
                           return;
                        } catch (IOException var19) {
                           var19.printStackTrace();
                        }
                  }

                  boolean var22 = false;

                  try {
                     Class var24 = Class.forName("java.awt.Desktop");
                     Object var25 = var24.getMethod("getDesktop").invoke(null);
                     var24.getMethod("browse", URI.class).invoke(var25, new File(this.mc.mcDataDir, Shaders.shaderpacksdirname).toURI());
                  } catch (Throwable var18) {
                     var18.printStackTrace();
                     var22 = true;
                  }

                  if (var22) {
                     Config.dbg("Opening via system class!");
                     Sys.openURL("file://" + Shaders.shaderpacksdir.getAbsolutePath());
                  }
                  break;
               case 202:
                  new File(Shaders.shadersdir, "current.cfg");
                  Shaders.storeConfig();
                  this.saved = true;
                  this.mc.displayGuiScreen(this.parentGui);
                  break;
               case 203:
                  GuiShaderOptions var23 = new GuiShaderOptions(this, Config.getGameSettings());
                  Config.getMinecraft().displayGuiScreen(var23);
                  break;
               default:
                  this.shaderList.actionPerformed(var1);
            }
         }
      }
   }

   @Override
   public void onGuiClosed() {
      super.onGuiClosed();
      if (!this.saved) {
         Shaders.storeConfig();
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.shaderList.drawScreen(var1, var2, var3);
      if (this.updateTimer <= 0) {
         this.shaderList.updateList();
         this.updateTimer += 20;
      }

      this.d(this.fontRendererObj, this.screenTitle + " ", this.width / 2, 15, 16777215);
      String var7 = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
      int var8 = this.fontRendererObj.getStringWidth(var7);
      if (var8 < this.width - 5) {
         this.d(this.fontRendererObj, var7, this.width / 2, this.height - 40, 8421504);
      } else {
         this.I(this.fontRendererObj, var7, 5, this.height - 40, 8421504);
      }

      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.updateTimer--;
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public void drawCenteredString(String var1, int var2, int var3, int var4) {
      this.d(this.fontRendererObj, var1, var2, var3, var4);
   }

   public static String toStringOnOff(boolean var0) {
      String var3 = Lang.getOn();
      String var4 = Lang.getOff();
      return var0 ? var3 : var4;
   }

   public static String toStringAa(int var0) {
      return var0 == 2 ? "FXAA 2x" : (var0 == 4 ? "FXAA 4x" : Lang.getOff());
   }

   public static String toStringValue(float var0, float[] var1, String[] var2) {
      int var3 = getValueIndex(var0, var1);
      return var2[var3];
   }

   public static int getValueIndex(float var0, float[] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         float var6 = var1[var5];
         if (var6 >= var0) {
            return var5;
         }
      }

      return var1.length - 1;
   }

   public static String toStringQuality(float var0) {
      return toStringValue(var0, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
   }

   public static String toStringHandDepth(float var0) {
      return toStringValue(var0, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
   }

   public static int getOSType() {
      String var3 = System.getProperty("os.name").toLowerCase();
      return var3.contains("win")
         ? 1
         : (
            var3.contains("mac")
               ? 2
               : (var3.contains("solaris") ? 3 : (var3.contains("sunos") ? 3 : (var3.contains("linux") ? 4 : (var3.contains("unix") ? 4 : 0))))
         );
   }

   static {
      long var20 = a ^ 56502878983207L;
      long var10000 = var20 ^ 9079170252082270759L;
      var10000 = var20 ^ 1104758489604619360L;
      var10000 = var20 ^ 1227990452596629803L;
   }

   public static class zo extends GuiSlot {
      private ArrayList shaderslist;
      private int selectedIndex;
      private long lastClickedCached = 0L;
      public final GuiShaders shadersGui;
      private static final long c = 119638189874796L;

      public zo(GuiShaders var1, int var2, int var3, int var4, int var5, int var6) {
         super(var1.getMc(), var2, var3, var4, var5, var6);
         this.shadersGui = var1;
         this.updateList();
         this.amountScrolled = 0.0F;
         int var9 = this.selectedIndex * var6;
         int var10 = (var5 - var4) / 2;
         if (var9 > var10) {
            this.scrollBy(var9 - var10);
         }
      }

      @Override
      public int getListWidth() {
         return this.width - 20;
      }

      public void updateList() {
         this.shaderslist = Shaders.listOfShaders();
         this.selectedIndex = 0;
         int var4 = 0;

         for (int var5 = this.shaderslist.size(); var4 < var5; var4++) {
            if (((String)this.shaderslist.get(var4)).equals(Shaders.currentshadername)) {
               this.selectedIndex = var4;
               break;
            }
         }
      }

      @Override
      public int getSize() {
         return this.shaderslist.size();
      }

      @Override
      public void elementClicked(int var1, boolean var2, int var3, int var4) {
         if (var1 != this.selectedIndex || this.lastClicked != this.lastClickedCached) {
            this.selectedIndex = var1;
            this.lastClickedCached = this.lastClicked;
            Shaders.setShaderPack((String)this.shaderslist.get(var1));
            Shaders.uninit();
            this.shadersGui.updateButtons();
         }
      }

      @Override
      public boolean isSelected(int var1) {
         return var1 == this.selectedIndex;
      }

      @Override
      public int getScrollBarX() {
         return this.width - 6;
      }

      @Override
      public int getContentHeight() {
         return this.getSize() * 18;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6) {
         String var10 = (String)this.shaderslist.get(var1);
         if (var10.equals(Shaders.packNameNone)) {
            var10 = Lang.get("of.options.shaders.packNone");
         } else if (var10.equals(Shaders.packNameDefault)) {
            var10 = Lang.get("of.options.shaders.packDefault");
         }

         this.shadersGui.drawCenteredString(var10, this.width / 2, var3 + 1, 16777215);
      }

      public int getSelectedIndex() {
         return this.selectedIndex;
      }
   }
}
