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

   public GuiShaders(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
      this.parentGui = par1GuiScreen;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("of.options.shadersTitle");
      if (Shaders.shadersConfig == null) {
         Shaders.loadConfig();
      }

      byte b = 120;
      byte b2 = 20;
      int i = this.width - b - 10;
      byte b3 = 30;
      byte b4 = 20;
      int j = this.width - b - 20;
      this.shaderList = new GuiShaders.zo(this, j, this.height, b3, this.height - 50, 16);
      this.shaderList.registerScrollButtons(7, 8);
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, i, 0 * b4 + b3, b, b2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, i, 1 * b4 + b3, b, b2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, i, 2 * b4 + b3, b, b2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, i, 3 * b4 + b3, b, b2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, i, 4 * b4 + b3, b, b2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, i, 5 * b4 + b3, b, b2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_HAND_LIGHT, i, 6 * b4 + b3, b, b2));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, i, 7 * b4 + b3, b, b2));
      int k = Math.min(150, j / 2 - 10);
      this.buttonList.add(new GuiButton(201, j / 4 - k / 2, this.height - 25, k, b2, Lang.get("of.options.shaders.shadersFolder")));
      this.buttonList.add(new GuiButton(202, j / 4 * 3 - k / 2, this.height - 25, k, b2, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(203, i, this.height - 25, b, b2, Lang.get("of.options.shaders.shaderOptions")));
      this.updateButtons();
   }

   public void updateButtons() {
      boolean bl = Config.isShaders();

      for (GuiButton guiButton : this.buttonList) {
         if (guiButton.id != 201 && guiButton.id != 202 && guiButton.id != EnumShaderOption.ANTIALIASING.ordinal()) {
            guiButton.enabled = bl;
         }
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.shaderList.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button instanceof GuiButtonEnumShaderOption) {
            GuiButtonEnumShaderOption guiButtonEnumShaderOption = (GuiButtonEnumShaderOption)button;
            switch (guiButtonEnumShaderOption.getEnumShaderOption()) {
               case ANTIALIASING:
                  Shaders.nextAntialiasingLevel();
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
                  float f = Shaders.configRenderResMul;
                  float[] floats = QUALITY_MULTIPLIERS;
                  String[] strings = QUALITY_MULTIPLIER_NAMES;
                  int i = getValueIndex(f, floats);
                  if (isShiftKeyDown()) {
                     if (--i < 0) {
                        i = floats.length - 1;
                     }
                  } else if (++i >= floats.length) {
                     i = 0;
                  }

                  Shaders.configRenderResMul = floats[i];
                  Shaders.scheduleResize();
                  break;
               case SHADOW_RES_MUL:
                  float g = Shaders.configShadowResMul;
                  float[] floats2 = QUALITY_MULTIPLIERS;
                  String[] strings2 = QUALITY_MULTIPLIER_NAMES;
                  int j = getValueIndex(g, floats2);
                  if (isShiftKeyDown()) {
                     if (--j < 0) {
                        j = floats2.length - 1;
                     }
                  } else if (++j >= floats2.length) {
                     j = 0;
                  }

                  Shaders.configShadowResMul = floats2[j];
                  Shaders.scheduleResizeShadow();
                  break;
               case HAND_DEPTH_MUL:
                  float h = Shaders.configHandDepthMul;
                  float[] floats3 = HAND_DEPTH_VALUES;
                  String[] strings3 = HAND_DEPTH_NAMES;
                  int k = getValueIndex(h, floats3);
                  if (isShiftKeyDown()) {
                     if (--k < 0) {
                        k = floats3.length - 1;
                     }
                  } else if (++k >= floats3.length) {
                     k = 0;
                  }

                  Shaders.configHandDepthMul = floats3[k];
                  break;
               case CLOUD_SHADOW:
                  Shaders.configCloudShadow = !Shaders.configCloudShadow;
                  break;
               case OLD_HAND_LIGHT:
                  Shaders.configOldHandLight.nextValue();
                  break;
               case OLD_LIGHTING:
                  Shaders.configOldLighting.nextValue();
                  Shaders.updateBlockLightLevel();
                  this.mc.scheduleResourcesRefresh();
                  break;
               case TWEAK_BLOCK_DAMAGE:
                  Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                  break;
               case TEX_MIN_FIL_B:
                  Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                  Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                  button.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case TEX_MAG_FIL_N:
                  Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                  button.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case TEX_MAG_FIL_S:
                  Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                  button.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case SHADOW_CLIP_FRUSTRUM:
                  Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                  button.displayString = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                  ShadersTex.updateTextureMinMagFilter();
            }

            guiButtonEnumShaderOption.updateButtonText();
         } else {
            switch (button.id) {
               case 201:
                  switch (getOSType()) {
                     case 1:
                        String string = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());

                        try {
                           Runtime.getRuntime().exec(string);
                           return;
                        } catch (IOException ioException) {
                           ioException.printStackTrace();
                           break;
                        }
                     case 2:
                        try {
                           Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath()});
                           return;
                        } catch (IOException ioException2) {
                           ioException2.printStackTrace();
                        }
                  }

                  boolean bl = false;

                  try {
                     Class class2 = Class.forName("java.awt.Desktop");
                     Object object = class2.getMethod("getDesktop").invoke(null);
                     class2.getMethod("browse", URI.class).invoke(object, new File(this.mc.mcDataDir, Shaders.shaderpacksdirname).toURI());
                  } catch (Throwable throwable) {
                     throwable.printStackTrace();
                     bl = true;
                  }

                  if (bl) {
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
                  GuiShaderOptions guiShaderOptions = new GuiShaderOptions(this, Config.getGameSettings());
                  Config.getMinecraft().displayGuiScreen(guiShaderOptions);
                  break;
               default:
                  this.shaderList.actionPerformed(button);
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
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.shaderList.drawScreen(mouseX, mouseY, partialTicks);
      if (this.updateTimer <= 0) {
         this.shaderList.updateList();
         this.updateTimer += 20;
      }

      this.drawCenteredString(this.fontRendererObj, this.screenTitle + " ", this.width / 2, 15, 16777215);
      String string = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
      int i = this.fontRendererObj.getStringWidth(string);
      if (i < this.width - 5) {
         this.drawCenteredString(this.fontRendererObj, string, this.width / 2, this.height - 40, 8421504);
      } else {
         this.drawString(this.fontRendererObj, string, 5, this.height - 40, 8421504);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.updateTimer--;
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public void drawCenteredString(String text, int x, int y, int color) {
      this.drawCenteredString(this.fontRendererObj, text, x, y, color);
   }

   public static String toStringOnOff(boolean value) {
      String string = Lang.getOn();
      String string2 = Lang.getOff();
      return value ? string : string2;
   }

   public static String toStringAa(int value) {
      return value == 2 ? "FXAA 2x" : (value == 4 ? "FXAA 4x" : Lang.getOff());
   }

   public static String toStringValue(float val, float[] values, String[] names) {
      int index = getValueIndex(val, values);
      return names[index];
   }

   public static int getValueIndex(float val, float[] values) {
      for (int i = 0; i < values.length; i++) {
         float f = values[i];
         if (f >= val) {
            return i;
         }
      }

      return values.length - 1;
   }

   public static String toStringQuality(float val) {
      return toStringValue(val, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
   }

   public static String toStringHandDepth(float val) {
      return toStringValue(val, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
   }

   public static int getOSType() {
      String string = System.getProperty("os.name").toLowerCase();
      return string.contains("win")
         ? 1
         : (
            string.contains("mac")
               ? 2
               : (string.contains("solaris") ? 3 : (string.contains("sunos") ? 3 : (string.contains("linux") ? 4 : (string.contains("unix") ? 4 : 0))))
         );
   }

   public static class zo extends GuiSlot {
      private ArrayList shaderslist;
      private int selectedIndex;
      private long lastClickedCached = 0L;
      public final GuiShaders shadersGui;

      public zo(GuiShaders guiShaders, int i, int j, int k, int l, int m) {
         super(guiShaders.getMc(), i, j, k, l, m);
         this.shadersGui = guiShaders;
         this.updateList();
         this.amountScrolled = 0.0F;
         int n = this.selectedIndex * m;
         int i2 = (l - k) / 2;
         if (n > i2) {
            this.scrollBy(n - i2);
         }
      }

      @Override
      public int getListWidth() {
         return this.width - 20;
      }

      public void updateList() {
         this.shaderslist = Shaders.listOfShaders();
         this.selectedIndex = 0;
         int i = 0;

         for (int j = this.shaderslist.size(); i < j; i++) {
            if (((String)this.shaderslist.get(i)).equals(Shaders.currentshadername)) {
               this.selectedIndex = i;
               break;
            }
         }
      }

      @Override
      public int getSize() {
         return this.shaderslist.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         if (slotIndex != this.selectedIndex || this.lastClicked != this.lastClickedCached) {
            this.selectedIndex = slotIndex;
            this.lastClickedCached = this.lastClicked;
            Shaders.setShaderPack((String)this.shaderslist.get(slotIndex));
            Shaders.uninit();
            this.shadersGui.updateButtons();
         }
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedIndex;
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
      public void drawSlot(int entryID, int i, int j, int k, int mouseXIn, int mouseYIn) {
         String string = (String)this.shaderslist.get(entryID);
         if (string.equals(Shaders.packNameNone)) {
            string = Lang.get("of.options.shaders.packNone");
         } else if (string.equals(Shaders.packNameDefault)) {
            string = Lang.get("of.options.shaders.packDefault");
         }

         this.shadersGui.drawCenteredString(string, this.width / 2, j + 1, 16777215);
      }

      public int getSelectedIndex() {
         return this.selectedIndex;
      }
   }
}
