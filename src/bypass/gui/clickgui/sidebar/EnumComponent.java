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
   public Animation dropdownAnimation = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private float dropdownHeight;
   private int[] optionAlphas = new int[128];

   public EnumComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(moduleButton, setting, f, g, i, j);
   }

   @Override
   public void render(int i, int j) {
      float f = this.getX() + this.getWidth() / 1.7F;
      RenderUtil.drawRect(
         f - 1.0F, this.getYPos() - 2.0F, this.getX() + this.getWidth() + 1.0F, this.getYPos() + 9.0F, new Color(34, 36, 43, this.fadeAlpha).getRGB()
      );
      RenderUtil.drawRect(f, this.getYPos() - 1.0F, this.getX() + this.getWidth(), this.getYPos() + 8.0F, new Color(29, 31, 37, this.fadeAlpha).getRGB());
      if (this.dropdownHeight > 0.0F && client.getSidebarGui().getOpenDropdown() != this) {
         RenderUtil.drawRect(
            f - 0.5F,
            this.getYPos() + 8.0F,
            this.getX() + this.getWidth() + 0.5F,
            this.getYPos() + this.dropdownHeight + 8.5F,
            new Color(34, 36, 43, this.fadeAlpha).getRGB()
         );
         RenderUtil.drawRect(
            f,
            this.getYPos() + 8.0F,
            this.getX() + this.getWidth(),
            this.getYPos() + this.dropdownHeight + 8.0F,
            new Color(29, 31, 37, this.fadeAlpha).getRGB()
         );
         this.optionAlphas = new int[128];
         if (!this.dropdownAnimation.isRunning()) {
            this.dropdownAnimation.backwards().start();
         }

         this.dropdownHeight = (float)this.dropdownAnimation.getValue(0.0, this.O_int());
      }

      if (this.fadeAlpha > 40) {
         client.getCustomFontRenderer()
            .drawString(this.getEnumSetting().getName(), this.getX(), this.getYPos() - 2.0F, new Color(255, 255, 255, this.fadeAlpha).getRGB());
         client.getCustomFontRenderer()
            .drawString(
               this.getEnumSetting().getValue().name(),
               f + this.getWidth() / 4.5F - client.getCustomFontRenderer().getStringWidth(this.getEnumSetting().getValue().name()) / 2.0F,
               this.getYPos() - 2.0F,
               new Color(136, 141, 157, this.fadeAlpha).getRGB()
            );
      }
   }

   @Override
   public void renderDropdown(int i, int j) {
      float f = this.getX() + this.getWidth() / 1.7F;
      Enum[] enums = (Enum[])this.getEnumSetting().getValue().getClass().getEnumConstants();
      RenderUtil.drawRect(
         f - 1.0F,
         this.getYPos() + 8.0F,
         this.getX() + this.getWidth() + 1.0F,
         this.getYPos() + this.dropdownHeight + 9.0F,
         new Color(34, 36, 43, this.fadeAlpha).getRGB()
      );
      RenderUtil.drawRect(
         f,
         this.getYPos() + 8.0F,
         this.getX() + this.getWidth(),
         this.getYPos() + this.dropdownHeight + 8.0F,
         new Color(29, 31, 37, this.fadeAlpha).getRGB()
      );
      int k = client.getSidebarGui().getAnimationStep();
      byte b = 13;

      for (int l = 0; l < enums.length; l++) {
         Enum enum2 = enums[l];
         if (this.optionAlphas[l] > 30) {
            client.getCustomFontRenderer()
               .drawString(
                  enum2.name(),
                  f + this.getWidth() / 4.5F - client.getCustomFontRenderer().getStringWidth(enum2.name()) / 2.0F,
                  this.getYPos() + b - 3.0F,
                  new Color(136, 141, 157, this.optionAlphas[l]).getRGB()
               );
         }

         if (this.isOptionHovered(i, j, (int)(this.getYPos() + b)) && Mouse.isButtonDown(0)) {
            this.getEnumSetting().setValue(enum2);
            client.getSidebarGui().setOpenDropdown(null);
         }

         if (this.dropdownHeight >= b && this.optionAlphas[l] < 255) {
            this.optionAlphas[l] = (int)this.dropdownAnimation.getValue(0.0, 255.0);
         }

         b += 13;
      }

      if (!this.dropdownAnimation.isRunning()) {
         this.dropdownAnimation.forwards().start();
      }

      this.dropdownHeight = (float)this.dropdownAnimation.getValue(0.0, this.O_int());
      super.renderDropdown(i, j);
   }

   public boolean isOptionHovered(int i, int j, int k) {
      return i >= this.getX() + this.getWidth() / 1.7F && i <= this.getX() + this.getWidth() && j >= k && j <= k + this.getHeight();
   }

   private int O_int() {
      Enum[] enums = (Enum[])this.getEnumSetting().getValue().getClass().getEnumConstants();
      byte b = 0;

      for (Enum enum2 : enums) {
         b += 13;
      }

      return b;
   }

   public EnumSetting getEnumSetting() {
      return (EnumSetting)this.getSetting();
   }
}
