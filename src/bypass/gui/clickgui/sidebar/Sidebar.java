package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import java.awt.Color;

public class Sidebar extends Container {
   private CategoryTab X;
   private CategoryTab O;
   private Animation B = new TimedAnimation().setDuration(250).setEasing(Easing.LINEAR);
   private int n;
   private String u;

   public Sidebar(int i, int j, int k, int l) {
      super("astolfo #" + client.getVersion(), i, j, k, l);
      this.F(true);
   }

   @Override
   public void l(int i) {
   }

   @Override
   public boolean G(int i, int j) {
      return i >= this.M() && i <= this.M() + this.x() && j >= this.E_float() && j <= this.E_float() + 20.0F;
   }

   @Override
   public void render(int i, int j) {
      int k = client.getSidebarGui().Z();
      int l = client.getSidebarGui().T();
      int m = client.getSidebarGui().F();
      int n = client.getSidebarGui().M();
      if (this.X != null) {
         if (this.x() < 130) {
            this.t(this.x() + n);
         } else if (this.X.D() != null) {
            if (this.X.D().A() != null) {
               if (this.x() < 410) {
                  this.t(this.x() + n);
               }
            } else {
               if (this.x() > 285) {
                  this.t(this.x() - n);
               }

               if (this.x() < 285) {
                  this.t(this.x() + n);
               }
            }
         } else if (this.x() > 130) {
            int i2 = this.x() - n;
            if (i2 < 130) {
               i2 = 130;
            }

            this.t(i2);
         }
      } else if (this.x() > 34) {
         this.t(this.x() - n);
      }

      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), k);
      int i3 = Math.min(this.x(), 130);
      RenderUtil.drawRect(this.M() + 34.0F, this.E_float(), this.M() + i3, this.E_float() + this.Y(), l);
      RenderUtil.drawRect(this.M(), this.E_float() + 30.0F, this.M() + 34.0F, this.E_float() + this.Y(), m);
      if (this.x() > 285) {
         RenderUtil.drawRect(
            this.M() + 285.0F, this.E_float() + 20.0F, this.M() + 285.5F, this.E_float() + this.Y() - 20.0F, new Color(136, 141, 157, 120).getRGB()
         );
      }

      byte b = 0;

      for (Element element : this.M_ArrayList()) {
         if (element instanceof CategoryTab) {
            Component component = (Component)element;
            component.r(this.M(), this.E_float() + b);
            component.render(i, j);
            b += 30;
         }
      }
   }

   public void W(CategoryTab categoryTab) {
      CategoryTab categoryTab2 = this.X;
      this.X = categoryTab;
      this.O = categoryTab2;
      if (this.O != null) {
         this.O.O = 12;
         this.O.j(null);
      }

      if (categoryTab != null) {
         categoryTab.O = 12;
      }
   }

   public CategoryTab s() {
      return this.O;
   }

   public CategoryTab z() {
      return this.X;
   }
}
