package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import java.awt.Color;

public class TitleBar extends Container {
   public TitleBar(int i, int j, int k, int l) {
      super("astolfo", i, j, k, l);
      this.F(true);
   }

   @Override
   public void l(int i) {
   }

   @Override
   public void render(int i, int j) {
      RenderUtil.drawRect(this.M() - 1.0F, this.E_float() - 1.0F, this.M() + this.x() + 1.0F, this.E_float() + this.Y() + 1.0F, client.getColor().getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(26, 26, 26).getRGB());
      client.getCustomFontRenderer().drawString(this.J(), this.M() + 3.0F, this.E_float() + 3.0F, -1);
      RenderUtil.drawRect(this.M() + 85.0F, this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(37, 37, 37).getRGB());
   }
}
