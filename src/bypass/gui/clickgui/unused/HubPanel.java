package bypass.gui.clickgui.unused;

import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import java.awt.Color;
import org.lwjgl.input.Mouse;

public class HubPanel extends Container {
   private HubCategory f;

   public HubPanel(int i, int j, int k, int l) {
      super("Hub", i, j, k, l);
      this.F(true);
   }

   @Override
   public void render(int i, int j) {
      RenderUtil.drawRect(this.M() - 4.0F, this.E_float() - 4.0F, this.M() + this.x() + 4.0F, this.E_float() + this.Y() + 4.0F, new Color(67, 63, 65).getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(32, 31, 32).getRGB());
      if (this.f != null) {
         this.f.Z(this);
         client.getCustomFontRenderer().drawString(this.f.J(), this.M() + 50.0F, this.E_float() + 5.0F, Color.WHITE.getRGB());
         this.f.render(i, j);
      } else {
         client.getCustomFontRenderer()
            .drawString(
               this.J(), this.M() + this.x() / 2 - client.getCustomFontRenderer().getStringWidth("Hub") / 2, this.E_float() + 5.0F, Color.WHITE.getRGB()
            );
         int k = 0;
         int l = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof HubCategory) {
               Component component = (Component)element;
               float g = this.M() + this.x() / 3 * l;
               int m = k >= 3 ? 110 : 20;
               component.r(g, this.E_float() + m);
               component.render(i, j);
               ((HubCategory)component).Z(this);
               k++;
               if (++l >= 3) {
                  l = 0;
               }
            }
         }

         if (this.f == null) {
            for (Element element2 : this.M_ArrayList()) {
               if (element2 instanceof HubCategory) {
                  Component component2 = (Component)element2;
                  if (this.G(i, j) && Mouse.isButtonDown(0)) {
                     this.f = (HubCategory)component2;
                  }
               }
            }
         }
      }
   }

   @Override
   public void l(int i) {
   }

   public HubCategory n() {
      return this.f;
   }
}
