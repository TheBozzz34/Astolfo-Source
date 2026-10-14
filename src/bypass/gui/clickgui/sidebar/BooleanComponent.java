package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import java.awt.Color;

public class BooleanComponent extends SidebarSettingComponent {
   public Animation q = new TimedAnimation().setDuration(150).setEasing(Easing.LINEAR);
   private static final long d = 58015731304030L;

   public BooleanComponent(ModuleButton moduleButton, Setting setting, float g, float h, int i, int j) {
      super(moduleButton, setting, g, h, i, j);
   }

   @Override
   public void l(int i) {
      if (i == 0 && this.y()) {
         this.e().set(!this.e().getValue());
      }
   }

   @Override
   public void render(int i, int j) {
      if (!this.q.isRunning()) {
         if (this.e().getValue()) {
            this.q.forwards().start();
         } else {
            this.q.backwards().start();
         }
      }

      int k = (int)this.q.getValue(0.0, 255.0);
      BooleanSetting booleanSetting = this.e();
      RenderUtil.drawRect(
         this.M() + this.x() - 9.0F, this.E_float() - 1.5F, this.M() + this.x() + 0.5F, this.E_float() + 8.5F, new Color(255, 255, 255, 120).getRGB()
      );
      RenderUtil.drawRect(this.M() + this.x() - 8.5F, this.E_float() - 1.0F, this.M() + this.x(), this.E_float() + 8.0F, new Color(29, 31, 37, this.X).getRGB());
      RenderUtil.drawRect(
         this.M() + this.x() - 7.5F, this.E_float(), this.M() + this.x() - 1.0F, this.E_float() + 7.0F, new Color(255, 255, 255, k).getRGB()
      );
      RenderUtil.drawRect(
         this.M() + this.x() - 6.5F, this.E_float() + 1.0F, this.M() + this.x() - 2.0F, this.E_float() + 6.0F, new Color(218, 223, 225, k).getRGB()
      );
      if (this.X > 40) {
         client.getCustomFontRenderer().drawString(booleanSetting.getName(), this.M(), this.E_float(), new Color(255, 255, 255, this.X).getRGB());
      }
   }

   public BooleanSetting e() {
      return (BooleanSetting)this.H();
   }
}
