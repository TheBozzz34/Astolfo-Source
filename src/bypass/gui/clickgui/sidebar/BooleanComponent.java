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

   public BooleanComponent(ModuleButton var1, Setting var2, float var3, float var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void l(int var1) {
      if (var1 == 0 && this.y()) {
         this.e().set(!this.e().getValue());
      }
   }

   @Override
   public void render(int var1, int var2) {
      if (!this.q.isRunning()) {
         if (this.e().getValue()) {
            this.q.forwards().start();
         } else {
            this.q.backwards().start();
         }
      }

      int var7 = (int)this.q.getValue(0.0, 255.0);
      BooleanSetting var8 = this.e();
      RenderUtil.drawRect(
         this.M() + this.x() - 9.0F, this.E_float() - 1.5F, this.M() + this.x() + 0.5F, this.E_float() + 8.5F, new Color(255, 255, 255, 120).getRGB()
      );
      RenderUtil.drawRect(this.M() + this.x() - 8.5F, this.E_float() - 1.0F, this.M() + this.x(), this.E_float() + 8.0F, new Color(29, 31, 37, this.X).getRGB());
      RenderUtil.drawRect(
         this.M() + this.x() - 7.5F, this.E_float(), this.M() + this.x() - 1.0F, this.E_float() + 7.0F, new Color(255, 255, 255, var7).getRGB()
      );
      RenderUtil.drawRect(
         this.M() + this.x() - 6.5F, this.E_float() + 1.0F, this.M() + this.x() - 2.0F, this.E_float() + 6.0F, new Color(218, 223, 225, var7).getRGB()
      );
      if (this.X > 40) {
         client.getCustomFontRenderer().drawString(var8.getName(), this.M(), this.E_float(), new Color(255, 255, 255, this.X).getRGB());
      }
   }

   public BooleanSetting e() {
      return (BooleanSetting)this.H();
   }
}
