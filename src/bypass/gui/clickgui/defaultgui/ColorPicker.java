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
   private final Category G;
   private final Container X;
   private final int K = new Color(37, 37, 37, 150).getRGB();
   private int l;
   private int d;
   private int f;
   private int e;
   private boolean r;
   private static final int t = 9;

   public ColorPicker(Category category, Container container, Setting<?> setting, int i, int j) {
      super(container, setting, i, j);
      this.G = category;
      this.X = container;
   }

   public int G() {
      return this.Y() - 16;
   }

   public float R() {
      return super.E_float() + 10.0F;
   }

   @Override
   public void U(int i, int j, int k) {
      switch (i) {
         case 0:
            if (Keyboard.isKeyDown(29)) {
               DefaultClickGuiScreen.i = new CopiedColor(this.z().mode.ordinal(), this.z().getValue(), this.d, this.f, this.l, this.e);
               return;
            }

            if (Keyboard.isKeyDown(42)) {
               ColorMode[] colorModes = ColorMode.values();
               int n = this.K();
               n++;
               if (n >= colorModes.length || n < 0) {
                  n = 0;
               }

               this.z().mode = colorModes[n];
               return;
            }

            if (this.u((int)this.M(), (int)this.E_float(), this.x(), 10, j, k)) {
               this.u(!this.s_boolean());
            }
            break;
         case 1:
            if (Keyboard.isKeyDown(29) && DefaultClickGuiScreen.i != null) {
               this.z().setValue(DefaultClickGuiScreen.i.z());
               this.z().mode = ColorMode.values()[DefaultClickGuiScreen.i.b()];
               this.d = DefaultClickGuiScreen.i.a();
               this.f = DefaultClickGuiScreen.i.m();
               this.l = DefaultClickGuiScreen.i.X();
               this.e = DefaultClickGuiScreen.i.B();
            }
      }
   }

   @Override
   public void l(int i) {
   }

   @Override
   public int Y() {
      return this.s_boolean() ? super.Y() : 10;
   }

   @Override
   public void render(int i, int j) {
      if (!this.s_boolean()) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
         Gui.drawModalRectWithCustomSizedTexture((int)this.M() + this.x() - 10, (int)this.E_float(), 0.0F, 0.0F, 10, this.Y(), this.x(), this.Y() * 10);
         RenderUtil.drawRect(this.M() + this.x() - 10.0F, this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), this.z().getValue().getRGB());
         GuiMainMenu.m.drawString(this.r().getName(), this.M() + 3.0F, this.E_float(), -1);
      } else {
         GL11.glShadeModel(7425);
         boolean bl = this.z().mode == ColorMode.RAINBOW;
         if (bl) {
            this.l = HypixelPacketHandler.r;
         }

         Color color = this.W(3);
         Color color2 = this.C(color);
         int k = this.S(color2, 3);
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
         Gui.drawModalRectWithCustomSizedTexture((int)this.M(), (int)this.R() + this.Y() - 14, 0.0F, 0.0F, this.x(), 5, this.x(), this.Y() * 2);
         int n = color2.getRed();
         if (n >= 210) {
            n = 255;
         }

         int i2 = color2.getGreen();
         if (i2 >= 210) {
            i2 = 255;
         }

         int i3 = color2.getBlue();
         if (i3 >= 210) {
            i3 = 255;
         }

         Color color3 = this.f > 48 ? new Color(0, 0, 0, k) : new Color(n, i2, i3, k);
         this.z().setValue(color3);
         RenderUtil.drawRect(this.M(), this.R() + this.Y() - 14.0F, this.M() + this.x(), this.R() + this.Y() - 9.0F, bl ? color.getRGB() : color3.getRGB());
         if (Mouse.isButtonDown(0)) {
            int i4 = (int)this.M();
            int i5 = (int)this.R();
            int i6 = j - i5;
            if (this.u(i4, i5, 7, this.G(), i, j)) {
               this.l = i6;
            }

            if (this.u(i4 + 11, i5, this.x() - 26, this.G(), i, j)) {
               int i7 = i - i4 - 11;
               this.f = i6;
               this.d = i7;
            }

            if (this.u(i4 + this.x() - 7, i5, 7, this.G(), i, j)) {
               this.e = i6;
            }
         }
      }
   }

   private int K() {
      ColorMode[] colorModes = ColorMode.values();

      for (int i = 0; i < colorModes.length; i++) {
         if (colorModes[i] == this.z().mode) {
            return i;
         }
      }

      return -1;
   }

   public Color W(int i) {
      byte b = 7;
      RenderUtil.drawGradientRect(this.M(), this.R(), this.M() + b, this.R() + this.G() / 6, new Color(255, 0, 0).getRGB(), new Color(255, 0, 255).getRGB());
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6, this.M() + b, this.R() + this.G() / 6 * 2, new Color(255, 0, 255).getRGB(), new Color(0, 0, 255).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 2, this.M() + b, this.R() + this.G() / 6 * 3, new Color(0, 0, 255).getRGB(), new Color(0, 255, 255).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 3, this.M() + b, this.R() + this.G() / 6 * 4, new Color(0, 255, 255).getRGB(), new Color(0, 255, 0).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 4, this.M() + b, this.R() + this.G() / 6 * 5, new Color(0, 255, 0).getRGB(), new Color(255, 255, 0).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 5, this.M() + b, this.R() + this.G(), new Color(255, 255, 0).getRGB(), new Color(255, 0, 0).getRGB()
      );
      RenderUtil.drawRect(
         this.M(), this.R() + this.l + i > this.R() + this.G() ? this.R() + this.G() - i : this.R() + this.l, this.M() + b, this.R() + this.l + i, this.K
      );
      return h(this.l);
   }

   public Color C(Color color) {
      byte b = 7;
      byte b2 = 7;
      byte b3 = 5;
      RenderUtil.drawFourColorRect(
         this.M() + b + b3,
         this.R(),
         this.M() + this.x() - b2 - b3,
         this.R() + this.G(),
         Color.white.getRGB(),
         color.getRGB(),
         Color.white.getRGB(),
         color.getRGB()
      );
      RenderUtil.drawFourColorRect(
         this.M() + b + b3,
         this.R(),
         this.M() + this.x() - b2 - b3,
         this.R() + this.G(),
         new Color(0, 0, 0, 0).getRGB(),
         new Color(0, 0, 0, 0).getRGB(),
         Color.black.getRGB(),
         Color.black.getRGB()
      );
      RenderUtil.drawRect(this.M() + 12.0F + this.d - 1.0F, this.R() + this.f - 1.0F, this.M() + 13.0F + this.d, this.R() + this.f + 1.0F, Color.black.getRGB());
      Color color2 = this.t(color, this.d, 99 - b2 - b3 * 2 - b);
      Color color3 = this.Q(color2, this.f);
      RenderUtil.drawRect(this.M() + 12.0F, this.E_float(), this.M() + this.x() - 12.0F, this.E_float() + 9.0F, new Color(50, 50, 50, 200).getRGB());
      GuiMainMenu.m
         .drawString("Done", this.M() + this.x() / 2 - GuiMainMenu.m.getStringWidth("Done", true) / 2, this.E_float(), new Color(200, 200, 200, 255).getRGB());
      return new Color(color3.getRed(), color3.getGreen(), color3.getBlue());
   }

   public int S(Color color, int i) {
      byte b = 7;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
      Gui.drawModalRectWithCustomSizedTexture((int)this.M() + this.x() - b, (int)this.R(), 0.0F, 0.0F, b, this.G(), this.x(), this.G() * 2);
      RenderUtil.drawGradientRect(
         this.M() + this.x() - b,
         this.R(),
         this.M() + this.x(),
         this.R() + this.G(),
         color.getRGB(),
         new Color(color.getRed(), color.getGreen(), color.getBlue(), 0).getRGB()
      );
      RenderUtil.drawRect(
         this.M() + this.x() - b,
         this.R() + this.e + i > this.R() + this.G() ? this.R() + this.G() - i : this.R() + this.e,
         this.M() + this.x(),
         this.R() + this.e + i,
         this.K
      );
      return this.C(this.e);
   }

   private int C(int i) {
      return 255 - i * (255 / this.G());
   }

   private Color t(Color color, int i, int j) {
      int k = j - i;
      int l = color.getRed();
      int m = color.getGreen();
      int n = color.getBlue();
      int i2 = (255 - l) / j;
      int i3 = (255 - m) / j;
      int i4 = (255 - n) / j;
      return new Color(l + k * i2, m + k * i3, n + k * i4);
   }

   private Color Q(Color color, int i) {
      int j = color.getRed();
      int k = color.getGreen();
      int l = color.getBlue();
      if (this.d < 10) {
         l = 255;
         k = 255;
         j = 255;
      }

      int m = (255 - (255 - j)) / this.G();
      int n = (255 - (255 - k)) / this.G();
      int i2 = (255 - (255 - l)) / this.G();
      return new Color(j - i * m, k - i * n, l - i * i2);
   }

   public static Color h(int i) {
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

   private ColorSetting z() {
      return (ColorSetting)this.r();
   }
}
