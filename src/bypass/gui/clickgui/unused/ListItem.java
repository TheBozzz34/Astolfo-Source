package bypass.gui.clickgui.unused;

import bypass.gui.component.Component;
import bypass.render.RenderUtil;
import java.awt.Color;

public class ListItem extends Component {
   private ListPanel s;
   private static final long c = 68493835363936L;

   public ListItem(ListPanel var1, String var2, int var3, int var4) {
      super(var2, 0.0F, 0.0F, var3, var4);
      this.s = var1;
   }

   @Override
   public void l(int var1) {
      this.s.c(this);
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = c ^ 45374998640658L;
      long var5 = var3 ^ 39308001293406L;
      if (this.s.Z() == this) {
         RenderUtil.drawRect(this.M() + 2.0F, this.E_float() + 3.0F, this.M() + 3.0F, this.E_float() + this.Y() + 3.0F, client.getColor().getRGB());
      }

      client.getCustomFontRenderer().drawStringWithShadow(this.J(), this.M() + 7.0F, this.E_float() + 2.0F, Color.WHITE.getRGB());
   }
}
