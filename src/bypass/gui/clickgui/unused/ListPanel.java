package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import java.awt.Color;

public class ListPanel extends Container {
   private static final Color s = new Color(34, 33, 32);
   private static final Color Z = new Color(26, 26, 26);
   private ListItem w;
   private static final long d = 130556815115692L;

   public ListPanel(String var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      this.F(true);
   }

   @Override
   public void l(int var1) {
      if (var1 == 1) {
         this.u(!this.s_boolean());
         if (this.s_boolean()) {
            this.U.backwards().rewind().start();
         } else {
            this.U.forwards().rewind().start();
         }
      }
   }

   @Override
   public void render(int var1, int var2) {
      int var6 = this.W() + 4;
      RenderUtil.drawRect(this.M() - 1.5F, this.E_float(), this.M() + this.x() + 1.5F, this.E_float() + this.Y() + var6 + 3.5F, Z.getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + var6 + 2.0F, s.getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + 6.0F, Z.getRGB());
      client.getCustomFontRenderer().drawStringWithShadow("Config", this.M() + 3.0F, this.E_float() + 2.0F, Color.WHITE.getRGB());
      if (this.s_boolean() || this.U.isRunning()) {
         int var7 = 0;

         for (Element var9 : this.M_ArrayList()) {
            if (var9 instanceof ListItem) {
               ListItem var10 = (ListItem)var9;
               var10.r(this.M(), this.E_float() + var7 + 15.0F);
               if (this.s_boolean()) {
                  var10.render(var1, var2);
               }

               var7 += 15;
               var7 = (int)(var7 - this.U.getValue(0.0, var7));
            }
         }
      }
   }

   private int W() {
      byte var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            var4 += 15;
         }
      }

      return var4;
   }

   public ListItem Z() {
      return this.w;
   }

   public void c(ListItem var1) {
      this.w = var1;
   }
}
