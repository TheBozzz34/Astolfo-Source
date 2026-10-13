package bypass.gui.clickgui.unused;

import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import java.awt.Color;
import org.lwjgl.input.Mouse;

public class HubPanel extends Container {
   private HubCategory f;
   private static final long d = 129259732381092L;

   public HubPanel(int var1, int var2, int var3, int var4) {
      super("Hub", var1, var2, var3, var4);
      this.F(true);
   }

   @Override
   public void render(int var1, int var2) {
      RenderUtil.drawRect(this.M() - 4.0F, this.E_float() - 4.0F, this.M() + this.x() + 4.0F, this.E_float() + this.Y() + 4.0F, new Color(67, 63, 65).getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(32, 31, 32).getRGB());
      if (this.f != null) {
         this.f.Z(this);
         client.getCustomFontRenderer().drawString(this.f.J(), this.M() + 50.0F, this.E_float() + 5.0F, Color.WHITE.getRGB());
         this.f.render(var1, var2);
      } else {
         client.getCustomFontRenderer()
            .drawString(
               this.J(), this.M() + this.x() / 2 - client.getCustomFontRenderer().getStringWidth("Hub") / 2, this.E_float() + 5.0F, Color.WHITE.getRGB()
            );
         int var7 = 0;
         int var8 = 0;

         for (Element var10 : this.M_ArrayList()) {
            if (var10 instanceof HubCategory) {
               Component var11 = (Component)var10;
               float var12 = this.M() + this.x() / 3 * var8;
               int var13 = var7 >= 3 ? 110 : 20;
               var11.r(var12, this.E_float() + var13);
               var11.render(var1, var2);
               ((HubCategory)var11).Z(this);
               var7++;
               if (++var8 >= 3) {
                  var8 = 0;
               }
            }
         }

         if (this.f == null) {
            for (Element var15 : this.M_ArrayList()) {
               if (var15 instanceof HubCategory) {
                  Component var16 = (Component)var15;
                  if (this.G(var1, var2) && Mouse.isButtonDown(0)) {
                     this.f = (HubCategory)var16;
                  }
               }
            }
         }
      }
   }

   @Override
   public void l(int var1) {
   }

   public HubCategory n() {
      return this.f;
   }
}
