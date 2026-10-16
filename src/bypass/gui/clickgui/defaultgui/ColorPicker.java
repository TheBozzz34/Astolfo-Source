package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Container;
import bypass.module.Category;
import bypass.module.hidden.HypixelPacketHandler;
import bypass.render.RenderUtil;
import bypass.setting.ColorMode;
import bypass.setting.ColorSetting;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ColorPicker extends SettingContainer {
   private final Category category;
   private final Container ownerContainer;
   private final int handleColor = new Color(37, 37, 37, 150).getRGB();
   private int hueOffset;
   private int saturationOffset;
   private int brightnessOffset;
   private int alphaOffset;
   private boolean unusedFlag;
   private static final int HUE_SEGMENT_SIZE = 9;

   public ColorPicker(Category category, Container container, Setting<?> setting, int i, int j) {
      super(container, setting, i, j);
      this.category = category;
      this.ownerContainer = container;
   }

   public int getPickerHeight() {
      return this.getHeight() - 16;
   }

   public float getPickerY() {
      return super.getYPos() + 10.0F;
   }

   @Override
   public void onMouseClicked(int i, int j, int k) {
      switch (i) {
         case 0:
            if (Keyboard.isKeyDown(29)) {
               DefaultClickGuiScreen.copiedColor = new CopiedColor(
                  this.getColorSetting().mode.ordinal(),
                  this.getColorSetting().getValue(),
                  this.saturationOffset,
                  this.brightnessOffset,
                  this.hueOffset,
                  this.alphaOffset
               );
               return;
            }

            if (Keyboard.isKeyDown(42)) {
               ColorMode[] colorModes = ColorMode.values();
               int l = this.getColorModeIndex();
               l++;
               if (l >= colorModes.length || l < 0) {
                  l = 0;
               }

               this.getColorSetting().mode = colorModes[l];
               return;
            }

            if (this.isMouseInRect((int)this.getX(), (int)this.getYPos(), this.getWidth(), 10, j, k)) {
               this.setExpanded(!this.isExpanded());
            }
            break;
         case 1:
            if (Keyboard.isKeyDown(29) && DefaultClickGuiScreen.copiedColor != null) {
               this.getColorSetting().setValue(DefaultClickGuiScreen.copiedColor.getColor());
               this.getColorSetting().mode = ColorMode.values()[DefaultClickGuiScreen.copiedColor.getModeOrdinal()];
               this.saturationOffset = DefaultClickGuiScreen.copiedColor.getSaturationOffset();
               this.brightnessOffset = DefaultClickGuiScreen.copiedColor.getBrightnessOffset();
               this.hueOffset = DefaultClickGuiScreen.copiedColor.getHueOffset();
               this.alphaOffset = DefaultClickGuiScreen.copiedColor.getAlphaOffset();
            }
      }
   }

   @Override
   public void onClick(int i) {
   }

   @Override
   public int getHeight() {
      return this.isExpanded() ? super.getHeight() : 10;
   }

   @Override
   public void render(int i, int j) {
      if (!this.isExpanded()) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
         Gui.drawModalRectWithCustomSizedTexture(
            (int)this.getX() + this.getWidth() - 10, (int)this.getYPos(), 0.0F, 0.0F, 10, this.getHeight(), this.getWidth(), this.getHeight() * 10
         );
         RenderUtil.drawRect(
            this.getX() + this.getWidth() - 10.0F,
            this.getYPos(),
            this.getX() + this.getWidth(),
            this.getYPos() + this.getHeight(),
            this.getColorSetting().getValue().getRGB()
         );
         GuiMainMenu.m.drawString(this.getSetting().getName(), this.getX() + 3.0F, this.getYPos(), -1);
      } else {
         GL11.glShadeModel(7425);
         boolean bl = this.getColorSetting().mode == ColorMode.RAINBOW;
         if (bl) {
            this.hueOffset = HypixelPacketHandler.tickCycle;
         }

         Color color = this.drawHueSlider(3);
         Color color2 = this.drawSaturationBrightnessBox(color);
         int k = this.drawAlphaSlider(color2, 3);
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
         Gui.drawModalRectWithCustomSizedTexture(
            (int)this.getX(), (int)this.getPickerY() + this.getHeight() - 14, 0.0F, 0.0F, this.getWidth(), 5, this.getWidth(), this.getHeight() * 2
         );
         int l = color2.getRed();
         if (l >= 210) {
            l = 255;
         }

         int n = color2.getGreen();
         if (n >= 210) {
            n = 255;
         }

         int i2 = color2.getBlue();
         if (i2 >= 210) {
            i2 = 255;
         }

         Color color3 = this.brightnessOffset > 48 ? new Color(0, 0, 0, k) : new Color(l, n, i2, k);
         this.getColorSetting().setValue(color3);
         RenderUtil.drawRect(
            this.getX(),
            this.getPickerY() + this.getHeight() - 14.0F,
            this.getX() + this.getWidth(),
            this.getPickerY() + this.getHeight() - 9.0F,
            bl ? color.getRGB() : color3.getRGB()
         );
         if (Mouse.isButtonDown(0)) {
            int i3 = (int)this.getX();
            int i4 = (int)this.getPickerY();
            int i5 = j - i4;
            if (this.isMouseInRect(i3, i4, 7, this.getPickerHeight(), i, j)) {
               this.hueOffset = i5;
            }

            if (this.isMouseInRect(i3 + 11, i4, this.getWidth() - 26, this.getPickerHeight(), i, j)) {
               int i6 = i - i3 - 11;
               this.brightnessOffset = i5;
               this.saturationOffset = i6;
            }

            if (this.isMouseInRect(i3 + this.getWidth() - 7, i4, 7, this.getPickerHeight(), i, j)) {
               this.alphaOffset = i5;
            }
         }
      }
   }

   private int getColorModeIndex() {
      ColorMode[] colorModes = ColorMode.values();

      for (int i = 0; i < colorModes.length; i++) {
         if (colorModes[i] == this.getColorSetting().mode) {
            return i;
         }
      }

      return -1;
   }

   public Color drawHueSlider(int i) {
      byte b = 7;
      RenderUtil.drawGradientRect(
         this.getX(),
         this.getPickerY(),
         this.getX() + b,
         this.getPickerY() + this.getPickerHeight() / 6,
         new Color(255, 0, 0).getRGB(),
         new Color(255, 0, 255).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.getX(),
         this.getPickerY() + this.getPickerHeight() / 6,
         this.getX() + b,
         this.getPickerY() + this.getPickerHeight() / 6 * 2,
         new Color(255, 0, 255).getRGB(),
         new Color(0, 0, 255).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.getX(),
         this.getPickerY() + this.getPickerHeight() / 6 * 2,
         this.getX() + b,
         this.getPickerY() + this.getPickerHeight() / 6 * 3,
         new Color(0, 0, 255).getRGB(),
         new Color(0, 255, 255).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.getX(),
         this.getPickerY() + this.getPickerHeight() / 6 * 3,
         this.getX() + b,
         this.getPickerY() + this.getPickerHeight() / 6 * 4,
         new Color(0, 255, 255).getRGB(),
         new Color(0, 255, 0).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.getX(),
         this.getPickerY() + this.getPickerHeight() / 6 * 4,
         this.getX() + b,
         this.getPickerY() + this.getPickerHeight() / 6 * 5,
         new Color(0, 255, 0).getRGB(),
         new Color(255, 255, 0).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.getX(),
         this.getPickerY() + this.getPickerHeight() / 6 * 5,
         this.getX() + b,
         this.getPickerY() + this.getPickerHeight(),
         new Color(255, 255, 0).getRGB(),
         new Color(255, 0, 0).getRGB()
      );
      RenderUtil.drawRect(
         this.getX(),
         this.getPickerY() + this.hueOffset + i > this.getPickerY() + this.getPickerHeight()
            ? this.getPickerY() + this.getPickerHeight() - i
            : this.getPickerY() + this.hueOffset,
         this.getX() + b,
         this.getPickerY() + this.hueOffset + i,
         this.handleColor
      );
      return getHueColor(this.hueOffset);
   }

   public Color drawSaturationBrightnessBox(Color color) {
      byte b = 7;
      byte b2 = 7;
      byte b3 = 5;
      RenderUtil.drawFourColorRect(
         this.getX() + b + b3,
         this.getPickerY(),
         this.getX() + this.getWidth() - b2 - b3,
         this.getPickerY() + this.getPickerHeight(),
         Color.white.getRGB(),
         color.getRGB(),
         Color.white.getRGB(),
         color.getRGB()
      );
      RenderUtil.drawFourColorRect(
         this.getX() + b + b3,
         this.getPickerY(),
         this.getX() + this.getWidth() - b2 - b3,
         this.getPickerY() + this.getPickerHeight(),
         new Color(0, 0, 0, 0).getRGB(),
         new Color(0, 0, 0, 0).getRGB(),
         Color.black.getRGB(),
         Color.black.getRGB()
      );
      RenderUtil.drawRect(
         this.getX() + 12.0F + this.saturationOffset - 1.0F,
         this.getPickerY() + this.brightnessOffset - 1.0F,
         this.getX() + 13.0F + this.saturationOffset,
         this.getPickerY() + this.brightnessOffset + 1.0F,
         Color.black.getRGB()
      );
      Color color2 = this.applySaturation(color, this.saturationOffset, 99 - b2 - b3 * 2 - b);
      Color color3 = this.applyBrightness(color2, this.brightnessOffset);
      RenderUtil.drawRect(
         this.getX() + 12.0F, this.getYPos(), this.getX() + this.getWidth() - 12.0F, this.getYPos() + 9.0F, new Color(50, 50, 50, 200).getRGB()
      );
      GuiMainMenu.m
         .drawString(
            "Done", this.getX() + this.getWidth() / 2 - GuiMainMenu.m.getStringWidth("Done", true) / 2, this.getYPos(), new Color(200, 200, 200, 255).getRGB()
         );
      return new Color(color3.getRed(), color3.getGreen(), color3.getBlue());
   }

   public int drawAlphaSlider(Color color, int i) {
      byte b = 7;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
      Gui.drawModalRectWithCustomSizedTexture(
         (int)this.getX() + this.getWidth() - b,
         (int)this.getPickerY(),
         0.0F,
         0.0F,
         b,
         this.getPickerHeight(),
         this.getWidth(),
         this.getPickerHeight() * 2
      );
      RenderUtil.drawGradientRect(
         this.getX() + this.getWidth() - b,
         this.getPickerY(),
         this.getX() + this.getWidth(),
         this.getPickerY() + this.getPickerHeight(),
         color.getRGB(),
         new Color(color.getRed(), color.getGreen(), color.getBlue(), 0).getRGB()
      );
      RenderUtil.drawRect(
         this.getX() + this.getWidth() - b,
         this.getPickerY() + this.alphaOffset + i > this.getPickerY() + this.getPickerHeight()
            ? this.getPickerY() + this.getPickerHeight() - i
            : this.getPickerY() + this.alphaOffset,
         this.getX() + this.getWidth(),
         this.getPickerY() + this.alphaOffset + i,
         this.handleColor
      );
      return this.getAlphaFromOffset(this.alphaOffset);
   }

   private int getAlphaFromOffset(int i) {
      return 255 - i * (255 / this.getPickerHeight());
   }

   private Color applySaturation(Color color, int i, int j) {
      int k = j - i;
      int l = color.getRed();
      int m = color.getGreen();
      int n = color.getBlue();
      int i2 = (255 - l) / j;
      int i3 = (255 - m) / j;
      int i4 = (255 - n) / j;
      return new Color(l + k * i2, m + k * i3, n + k * i4);
   }

   private Color applyBrightness(Color color, int i) {
      int j = color.getRed();
      int k = color.getGreen();
      int l = color.getBlue();
      if (this.saturationOffset < 10) {
         l = 255;
         k = 255;
         j = 255;
      }

      int m = (255 - (255 - j)) / this.getPickerHeight();
      int n = (255 - (255 - k)) / this.getPickerHeight();
      int i2 = (255 - (255 - l)) / this.getPickerHeight();
      return new Color(j - i * m, k - i * n, l - i * i2);
   }

   public static Color getHueColor(int i) {
      int j = 54 - i;
      Color color = Color.red;
      byte b = 28;
      if (j > 0 && j <= 9) {
         int k = j;
         color = new Color(255, k * b, 0);
      } else if (j > 9 && j <= 18) {
         int l = j - 9;
         color = new Color(255 - l * b, 255, 0);
      } else if (j > 18 && j <= 27) {
         int m = j - 18;
         color = new Color(0, 255, m * b);
      } else if (j > 27 && j <= 36) {
         int n = j - 27;
         color = new Color(0, 255 - n * b, 255);
      } else if (j > 36 && j <= 45) {
         int i2 = j - 36;
         color = new Color(i2 * b, 0, 255);
      } else if (j > 45 && j <= 54) {
         int i3 = j - 45;
         color = new Color(255, 0, 255 - i3 * b);
      }

      return color;
   }

   private ColorSetting getColorSetting() {
      return (ColorSetting)this.getSetting();
   }
}
