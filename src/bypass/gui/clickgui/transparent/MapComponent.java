package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.setting.Setting;
import java.awt.Color;

public class MapComponent extends SettingContainer {
   private static final Color C = new Color(23, 23, 23);
   private static final long d = 40832626348333L;

   public MapComponent(Container var1, Setting var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void render(int var1, int var2) {
      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            this.T().getName() + "...",
            this.M() + this.x() / 2 - client.getCustomFontRenderer().getStringWidth(this.r().getName() + "...") / 2 + 3.0F,
            this.E_float(),
            Color.WHITE.getRGB()
         );
      if (this.s_boolean()) {
         int var6 = 0;

         for (Element var8 : this.M_ArrayList()) {
            if (var8 instanceof SettingComponent) {
               ((SettingComponent)var8).r(this.M() + 1.5F, this.E_float() + var6 + 15.0F);
               var8.render(var1, var2);
               var6 += 15;
               var6 = (int)(var6 - this.U.getValue(0.0, var6));
            }
         }
      }
   }

   @Override
   public void l(int var1) {
      if (var1 == 0) {
         this.u(!this.s_boolean());
         if (this.s_boolean()) {
            this.U.backwards().rewind().start();
         } else {
            this.U.forwards().rewind().start();
         }
      }
   }

   public int d() {
      byte var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof BooleanComponent || var6 instanceof StringComponent || var6 instanceof NumberSlider || var6 instanceof EnumComponent) {
               var4 += 15;
            }
         }
      }

      return var4;
   }
}
