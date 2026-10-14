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

   public MapComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
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
         int k = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof SettingComponent) {
               ((SettingComponent)element).r(this.M() + 1.5F, this.E_float() + k + 15.0F);
               element.render(i, j);
               k += 15;
               k = (int)(k - this.U.getValue(0.0, k));
            }
         }
      }
   }

   @Override
   public void l(int i) {
      if (i == 0) {
         this.u(!this.s_boolean());
         if (this.s_boolean()) {
            this.U.backwards().rewind().start();
         } else {
            this.U.forwards().rewind().start();
         }
      }
   }

   public int d() {
      byte b = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof BooleanComponent || element instanceof StringComponent || element instanceof NumberSlider || element instanceof EnumComponent) {
               b += 15;
            }
         }
      }

      return b;
   }
}
