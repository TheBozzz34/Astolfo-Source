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

   public ListPanel(String string, int i, int j, int k, int l) {
      super(string, i, j, k, l);
      this.F(true);
   }

   @Override
   public void l(int i) {
      if (i == 1) {
         this.u(!this.s_boolean());
         if (this.s_boolean()) {
            this.U.backwards().rewind().start();
         } else {
            this.U.forwards().rewind().start();
         }
      }
   }

   @Override
   public void render(int i, int j) {
      int k = this.W() + 4;
      RenderUtil.drawRect(this.M() - 1.5F, this.E_float(), this.M() + this.x() + 1.5F, this.E_float() + this.Y() + k + 3.5F, Z.getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + k + 2.0F, s.getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + 6.0F, Z.getRGB());
      client.getCustomFontRenderer().drawStringWithShadow("Config", this.M() + 3.0F, this.E_float() + 2.0F, Color.WHITE.getRGB());
      if (this.s_boolean() || this.U.isRunning()) {
         int l = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof ListItem) {
               ListItem listItem = (ListItem)element;
               listItem.r(this.M(), this.E_float() + l + 15.0F);
               if (this.s_boolean()) {
                  listItem.render(i, j);
               }

               l += 15;
               l = (int)(l - this.U.getValue(0.0, l));
            }
         }
      }
   }

   private int W() {
      byte b = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            b += 15;
         }
      }

      return b;
   }

   public ListItem Z() {
      return this.w;
   }

   public void c(ListItem listItem) {
      this.w = listItem;
   }
}
