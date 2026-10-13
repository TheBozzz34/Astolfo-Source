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
   private Animation B;
   private int n;
   private String u;
   private static final long d = 97140108686979L;

   public Sidebar(int var1, int var2, int var3, int var4) {
      super("astolfo #" + client.getVersion(), var1, var2, var3, var4);
      this.B = new TimedAnimation().setDuration(250).setEasing(Easing.LINEAR);
      this.F(true);
   }

   @Override
   public void l(int var1) {
   }

   @Override
   public boolean G(int var1, int var2) {
      return var1 >= this.M() && var1 <= this.M() + this.x() && var2 >= this.E_float() && var2 <= this.E_float() + 20.0F;
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 87234942265585L;
      long var5 = var3 ^ 8972929756414L;
      int var8 = client.getSidebarGui().Z();
      int var9 = client.getSidebarGui().T();
      int var10 = client.getSidebarGui().F();
      int var11 = client.getSidebarGui().M();
      if (this.X != null) {
         if (this.x() < 130) {
            this.t(this.x() + var11);
         } else if (this.X.D() != null) {
            if (this.X.D().A() != null) {
               if (this.x() < 410) {
                  this.t(this.x() + var11);
               }
            } else {
               if (this.x() > 285) {
                  this.t(this.x() - var11);
               }

               if (this.x() < 285) {
                  this.t(this.x() + var11);
               }
            }
         } else if (this.x() > 130) {
            int var12 = this.x() - var11;
            if (var12 < 130) {
               var12 = 130;
            }

            this.t(var12);
         }
      } else if (this.x() > 34) {
         this.t(this.x() - var11);
      }

      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), var8);
      int var17 = Math.min(this.x(), 130);
      RenderUtil.drawRect(this.M() + 34.0F, this.E_float(), this.M() + var17, this.E_float() + this.Y(), var9);
      RenderUtil.drawRect(this.M(), this.E_float() + 30.0F, this.M() + 34.0F, this.E_float() + this.Y(), var10);
      if (this.x() > 285) {
         RenderUtil.drawRect(
            this.M() + 285.0F, this.E_float() + 20.0F, this.M() + 285.5F, this.E_float() + this.Y() - 20.0F, new Color(136, 141, 157, 120).getRGB()
         );
      }

      byte var13 = 0;

      for (Element var15 : this.M_ArrayList()) {
         if (var15 instanceof CategoryTab) {
            Component var16 = (Component)var15;
            var16.r(this.M(), this.E_float() + var13);
            var16.render(var1, var2);
            var13 += 30;
         }
      }
   }

   public void W(CategoryTab var1) {
      CategoryTab var5 = this.X;
      this.X = var1;
      this.O = var5;
      if (this.O != null) {
         this.O.O = 12;
         this.O.j(null);
      }

      if (var1 != null) {
         var1.O = 12;
      }
   }

   public CategoryTab s() {
      return this.O;
   }

   public CategoryTab z() {
      return this.X;
   }
}
