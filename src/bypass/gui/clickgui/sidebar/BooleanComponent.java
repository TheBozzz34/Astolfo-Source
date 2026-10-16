package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import java.awt.Color;

public class BooleanComponent extends SidebarSettingComponent {
   public Animation checkAnimation = new TimedAnimation().setDuration(150).setEasing(Easing.LINEAR);

   public BooleanComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(moduleButton, setting, f, g, i, j);
   }

   @Override
   public void onClick(int i) {
      if (i == 0 && this.isVisible()) {
         this.getBooleanSetting().set(!this.getBooleanSetting().getValue());
      }
   }

   @Override
   public void render(int i, int j) {
      if (!this.checkAnimation.isRunning()) {
         if (this.getBooleanSetting().getValue()) {
            this.checkAnimation.forwards().start();
         } else {
            this.checkAnimation.backwards().start();
         }
      }

      int k = (int)this.checkAnimation.getValue(0.0, 255.0);
      BooleanSetting booleanSetting = this.getBooleanSetting();
      RenderUtil.drawRect(
         this.getX() + this.getWidth() - 9.0F,
         this.getYPos() - 1.5F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + 8.5F,
         new Color(255, 255, 255, 120).getRGB()
      );
      RenderUtil.drawRect(
         this.getX() + this.getWidth() - 8.5F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth(),
         this.getYPos() + 8.0F,
         new Color(29, 31, 37, this.fadeAlpha).getRGB()
      );
      RenderUtil.drawRect(
         this.getX() + this.getWidth() - 7.5F,
         this.getYPos(),
         this.getX() + this.getWidth() - 1.0F,
         this.getYPos() + 7.0F,
         new Color(255, 255, 255, k).getRGB()
      );
      RenderUtil.drawRect(
         this.getX() + this.getWidth() - 6.5F,
         this.getYPos() + 1.0F,
         this.getX() + this.getWidth() - 2.0F,
         this.getYPos() + 6.0F,
         new Color(218, 223, 225, k).getRGB()
      );
      if (this.fadeAlpha > 40) {
         client.getCustomFontRenderer().drawString(booleanSetting.getName(), this.getX(), this.getYPos(), new Color(255, 255, 255, this.fadeAlpha).getRGB());
      }
   }

   public BooleanSetting getBooleanSetting() {
      return (BooleanSetting)this.getSetting();
   }
}
