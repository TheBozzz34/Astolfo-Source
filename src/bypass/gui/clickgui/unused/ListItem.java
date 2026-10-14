package bypass.gui.clickgui.unused;

import bypass.gui.component.Component;
import bypass.render.RenderUtil;
import java.awt.Color;

public class ListItem extends Component {
   private ListPanel s;

   public ListItem(ListPanel listPanel, String string, int i, int j) {
      super(string, 0.0F, 0.0F, i, j);
      this.s = listPanel;
   }

   @Override
   public void l(int i) {
      this.s.c(this);
   }

   @Override
   public void render(int i, int j) {
      if (this.s.Z() == this) {
         RenderUtil.drawRect(this.M() + 2.0F, this.E_float() + 3.0F, this.M() + 3.0F, this.E_float() + this.Y() + 3.0F, client.getColor().getRGB());
      }

      client.getCustomFontRenderer().drawStringWithShadow(this.J(), this.M() + 7.0F, this.E_float() + 2.0F, Color.WHITE.getRGB());
   }
}
