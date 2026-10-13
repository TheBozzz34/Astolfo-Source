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
   private static final long h = 85180137929874L;

   public ColorPicker(Category var1, Container var2, Setting<?> var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.G = var1;
      this.X = var2;
   }

   public int G() {
      return this.Y() - 16;
   }

   public float R() {
      return super.E_float() + 10.0F;
   }

   @Override
   public void U(int var1, int var2, int var3) {
      switch (var1) {
         case 0:
            if (Keyboard.isKeyDown(29)) {
               DefaultClickGuiScreen.i = new CopiedColor(this.z().mode.ordinal(), this.z().getValue(), this.d, this.f, this.l, this.e);
               return;
            }

            if (Keyboard.isKeyDown(42)) {
               ColorMode[] var7 = ColorMode.values();
               int var8 = this.K();
               var8++;
               if (var8 >= var7.length || var8 < 0) {
                  var8 = 0;
               }

               this.z().mode = var7[var8];
               return;
            }

            if (this.u((int)this.M(), (int)this.E_float(), this.x(), 10, var2, var3)) {
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
   public void l(int var1) {
   }

   @Override
   public int Y() {
      return this.s_boolean() ? super.Y() : 10;
   }

   @Override
   public void render(int var1, int var2) {
      if (!this.s_boolean()) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
         Gui.drawModalRectWithCustomSizedTexture((int)this.M() + this.x() - 10, (int)this.E_float(), 0.0F, 0.0F, 10, this.Y(), this.x(), this.Y() * 10);
         RenderUtil.drawRect(this.M() + this.x() - 10.0F, this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), this.z().getValue().getRGB());
         GuiMainMenu.m.drawString(this.r().getName(), this.M() + 3.0F, this.E_float(), -1);
      } else {
         GL11.glShadeModel(7425);
         boolean var6 = this.z().mode == ColorMode.RAINBOW;
         if (var6) {
            this.l = HypixelPacketHandler.r;
         }

         Color var7 = this.W(3);
         Color var8 = this.C(var7);
         int var9 = this.S(var8, 3);
         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
         Gui.drawModalRectWithCustomSizedTexture((int)this.M(), (int)this.R() + this.Y() - 14, 0.0F, 0.0F, this.x(), 5, this.x(), this.Y() * 2);
         int var10 = var8.getRed();
         if (var10 >= 210) {
            var10 = 255;
         }

         int var11 = var8.getGreen();
         if (var11 >= 210) {
            var11 = 255;
         }

         int var12 = var8.getBlue();
         if (var12 >= 210) {
            var12 = 255;
         }

         Color var13 = this.f > 48 ? new Color(0, 0, 0, var9) : new Color(var10, var11, var12, var9);
         this.z().setValue(var13);
         RenderUtil.drawRect(this.M(), this.R() + this.Y() - 14.0F, this.M() + this.x(), this.R() + this.Y() - 9.0F, var6 ? var7.getRGB() : var13.getRGB());
         if (Mouse.isButtonDown(0)) {
            int var14 = (int)this.M();
            int var15 = (int)this.R();
            int var16 = var2 - var15;
            if (this.u(var14, var15, 7, this.G(), var1, var2)) {
               this.l = var16;
            }

            if (this.u(var14 + 11, var15, this.x() - 26, this.G(), var1, var2)) {
               int var17 = var1 - var14 - 11;
               this.f = var16;
               this.d = var17;
            }

            if (this.u(var14 + this.x() - 7, var15, 7, this.G(), var1, var2)) {
               this.e = var16;
            }
         }
      }
   }

   private int K() {
      ColorMode[] var4 = ColorMode.values();

      for (int var5 = 0; var5 < var4.length; var5++) {
         if (var4[var5] == this.z().mode) {
            return var5;
         }
      }

      return -1;
   }

   public Color W(int var1) {
      byte var6 = 7;
      RenderUtil.drawGradientRect(this.M(), this.R(), this.M() + var6, this.R() + this.G() / 6, new Color(255, 0, 0).getRGB(), new Color(255, 0, 255).getRGB());
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6, this.M() + var6, this.R() + this.G() / 6 * 2, new Color(255, 0, 255).getRGB(), new Color(0, 0, 255).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 2, this.M() + var6, this.R() + this.G() / 6 * 3, new Color(0, 0, 255).getRGB(), new Color(0, 255, 255).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 3, this.M() + var6, this.R() + this.G() / 6 * 4, new Color(0, 255, 255).getRGB(), new Color(0, 255, 0).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 4, this.M() + var6, this.R() + this.G() / 6 * 5, new Color(0, 255, 0).getRGB(), new Color(255, 255, 0).getRGB()
      );
      RenderUtil.drawGradientRect(
         this.M(), this.R() + this.G() / 6 * 5, this.M() + var6, this.R() + this.G(), new Color(255, 255, 0).getRGB(), new Color(255, 0, 0).getRGB()
      );
      RenderUtil.drawRect(
         this.M(),
         this.R() + this.l + var1 > this.R() + this.G() ? this.R() + this.G() - var1 : this.R() + this.l,
         this.M() + var6,
         this.R() + this.l + var1,
         this.K
      );
      return h(this.l);
   }

   public Color C(Color var1) {
      byte var5 = 7;
      byte var6 = 7;
      byte var7 = 5;
      RenderUtil.drawFourColorRect(
         this.M() + var5 + var7,
         this.R(),
         this.M() + this.x() - var6 - var7,
         this.R() + this.G(),
         Color.white.getRGB(),
         var1.getRGB(),
         Color.white.getRGB(),
         var1.getRGB()
      );
      RenderUtil.drawFourColorRect(
         this.M() + var5 + var7,
         this.R(),
         this.M() + this.x() - var6 - var7,
         this.R() + this.G(),
         new Color(0, 0, 0, 0).getRGB(),
         new Color(0, 0, 0, 0).getRGB(),
         Color.black.getRGB(),
         Color.black.getRGB()
      );
      RenderUtil.drawRect(this.M() + 12.0F + this.d - 1.0F, this.R() + this.f - 1.0F, this.M() + 13.0F + this.d, this.R() + this.f + 1.0F, Color.black.getRGB());
      Color var8 = this.t(var1, this.d, 99 - var6 - var7 * 2 - var5);
      Color var9 = this.Q(var8, this.f);
      RenderUtil.drawRect(this.M() + 12.0F, this.E_float(), this.M() + this.x() - 12.0F, this.E_float() + 9.0F, new Color(50, 50, 50, 200).getRGB());
      GuiMainMenu.m
         .drawString("Done", this.M() + this.x() / 2 - GuiMainMenu.m.getStringWidth("Done", true) / 2, this.E_float(), new Color(200, 200, 200, 255).getRGB());
      return new Color(var9.getRed(), var9.getGreen(), var9.getBlue());
   }

   public int S(Color var1, int var2) {
      byte var6 = 7;
      Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/misc/alpha.png"));
      Gui.drawModalRectWithCustomSizedTexture((int)this.M() + this.x() - var6, (int)this.R(), 0.0F, 0.0F, var6, this.G(), this.x(), this.G() * 2);
      RenderUtil.drawGradientRect(
         this.M() + this.x() - var6,
         this.R(),
         this.M() + this.x(),
         this.R() + this.G(),
         var1.getRGB(),
         new Color(var1.getRed(), var1.getGreen(), var1.getBlue(), 0).getRGB()
      );
      RenderUtil.drawRect(
         this.M() + this.x() - var6,
         this.R() + this.e + var2 > this.R() + this.G() ? this.R() + this.G() - var2 : this.R() + this.e,
         this.M() + this.x(),
         this.R() + this.e + var2,
         this.K
      );
      return this.C(this.e);
   }

   private int C(int var1) {
      return 255 - var1 * (255 / this.G());
   }

   private Color t(Color var1, int var2, int var3) {
      int var6 = var3 - var2;
      int var7 = var1.getRed();
      int var8 = var1.getGreen();
      int var9 = var1.getBlue();
      int var10 = (255 - var7) / var3;
      int var11 = (255 - var8) / var3;
      int var12 = (255 - var9) / var3;
      return new Color(var7 + var6 * var10, var8 + var6 * var11, var9 + var6 * var12);
   }

   private Color Q(Color var1, int var2) {
      int var6 = var1.getRed();
      int var7 = var1.getGreen();
      int var8 = var1.getBlue();
      if (this.d < 10) {
         var8 = 255;
         var7 = 255;
         var6 = 255;
      }

      int var9 = (255 - (255 - var6)) / this.G();
      int var10 = (255 - (255 - var7)) / this.G();
      int var11 = (255 - (255 - var8)) / this.G();
      return new Color(var6 - var2 * var9, var7 - var2 * var10, var8 - var2 * var11);
   }

   public static Color h(int var0) {
      int var3 = 54 - var0;
      Color var4 = Color.red;
      byte var6 = 28;
      if (var3 > 0 && var3 <= 9) {
         int var11 = var3;
         var4 = new Color(255, var11 * var6, 0);
      } else if (var3 > 9 && var3 <= 18) {
         int var10 = var3 - 9;
         var4 = new Color(255 - var10 * var6, 255, 0);
      } else if (var3 > 18 && var3 <= 27) {
         int var9 = var3 - 18;
         var4 = new Color(0, 255, var9 * var6);
      } else if (var3 > 27 && var3 <= 36) {
         int var8 = var3 - 27;
         var4 = new Color(0, 255 - var8 * var6, 255);
      } else if (var3 > 36 && var3 <= 45) {
         int var7 = var3 - 36;
         var4 = new Color(var7 * var6, 0, 255);
      } else if (var3 > 45 && var3 <= 54) {
         int var5 = var3 - 45;
         var4 = new Color(255, 0, 255 - var5 * var6);
      }

      return var4;
   }

   private ColorSetting z() {
      return (ColorSetting)this.r();
   }

   static {
      long var11 = h ^ 59006731230397L;
      long var10000 = var11 ^ 8570650014622756227L;
   }
}
