package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.render.RenderUtil;
import bypass.setting.EnumSetting;
import bypass.setting.Setting;
import java.awt.Color;
import org.lwjgl.input.Mouse;

public class EnumComponent extends DropdownComponent {
   public Animation u = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private float G;
   private int[] r = new int[128];

   public EnumComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(moduleButton, setting, f, g, i, j);
   }

   @Override
   public void render(int i, int j) {
      float f = this.M() + this.x() / 1.7F;
      RenderUtil.drawRect(f - 1.0F, this.E_float() - 2.0F, this.M() + this.x() + 1.0F, this.E_float() + 9.0F, new Color(34, 36, 43, this.X).getRGB());
      RenderUtil.drawRect(f, this.E_float() - 1.0F, this.M() + this.x(), this.E_float() + 8.0F, new Color(29, 31, 37, this.X).getRGB());
      if (this.G > 0.0F && client.getSidebarGui().A() != this) {
         RenderUtil.drawRect(
            f - 0.5F, this.E_float() + 8.0F, this.M() + this.x() + 0.5F, this.E_float() + this.G + 8.5F, new Color(34, 36, 43, this.X).getRGB()
         );
         RenderUtil.drawRect(f, this.E_float() + 8.0F, this.M() + this.x(), this.E_float() + this.G + 8.0F, new Color(29, 31, 37, this.X).getRGB());
         this.r = new int[128];
         if (!this.u.isRunning()) {
            this.u.backwards().start();
         }

         this.G = (float)this.u.getValue(0.0, this.O_int());
      }

      if (this.X > 40) {
         client.getCustomFontRenderer().drawString(this.n().getName(), this.M(), this.E_float() - 2.0F, new Color(255, 255, 255, this.X).getRGB());
         client.getCustomFontRenderer()
            .drawString(
               this.n().getValue().name(),
               f + this.x() / 4.5F - client.getCustomFontRenderer().getStringWidth(this.n().getValue().name()) / 2.0F,
               this.E_float() - 2.0F,
               new Color(136, 141, 157, this.X).getRGB()
            );
      }
   }

   @Override
   public void M(int i, int j) {
      float f = this.M() + this.x() / 1.7F;
      Enum[] enums = (Enum[])this.n().getValue().getClass().getEnumConstants();
      RenderUtil.drawRect(
         f - 1.0F, this.E_float() + 8.0F, this.M() + this.x() + 1.0F, this.E_float() + this.G + 9.0F, new Color(34, 36, 43, this.X).getRGB()
      );
      RenderUtil.drawRect(f, this.E_float() + 8.0F, this.M() + this.x(), this.E_float() + this.G + 8.0F, new Color(29, 31, 37, this.X).getRGB());
      int k = client.getSidebarGui().M();
      byte b = 13;

      for (int l = 0; l < enums.length; l++) {
         Enum enum2 = enums[l];
         if (this.r[l] > 30) {
            client.getCustomFontRenderer()
               .drawString(
                  enum2.name(),
                  f + this.x() / 4.5F - client.getCustomFontRenderer().getStringWidth(enum2.name()) / 2.0F,
                  this.E_float() + b - 3.0F,
                  new Color(136, 141, 157, this.r[l]).getRGB()
               );
         }

         if (this.o(i, j, (int)(this.E_float() + b)) && Mouse.isButtonDown(0)) {
            this.n().setValue(enum2);
            client.getSidebarGui().G(null);
         }

         if (this.G >= b && this.r[l] < 255) {
            this.r[l] = (int)this.u.getValue(0.0, 255.0);
         }

         b += 13;
      }

      if (!this.u.isRunning()) {
         this.u.forwards().start();
      }

      this.G = (float)this.u.getValue(0.0, this.O_int());
      super.M(i, j);
   }

   public boolean o(int i, int j, int k) {
      return i >= this.M() + this.x() / 1.7F && i <= this.M() + this.x() && j >= k && j <= k + this.Y();
   }

   private int O_int() {
      Enum[] enums = (Enum[])this.n().getValue().getClass().getEnumConstants();
      byte b = 0;

      for (Enum enum2 : enums) {
         b += 13;
      }

      return b;
   }

   public EnumSetting n() {
      return (EnumSetting)this.H();
   }
}
