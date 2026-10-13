package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import java.awt.Color;

public class TitleBar extends Container {
   private static final long d = 129136606962365L;

   public TitleBar(int var1, int var2, int var3, int var4) {
      super("astolfo", var1, var2, var3, var4);
      this.F(true);
   }

   @Override
   public void l(int var1) {
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 108289874525391L;
      long var5 = var3 ^ 39308001293406L;
      RenderUtil.drawRect(this.M() - 1.0F, this.E_float() - 1.0F, this.M() + this.x() + 1.0F, this.E_float() + this.Y() + 1.0F, client.getColor().getRGB());
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(26, 26, 26).getRGB());
      client.getCustomFontRenderer().drawString(this.J(), this.M() + 3.0F, this.E_float() + 3.0F, -1);
      RenderUtil.drawRect(this.M() + 85.0F, this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(37, 37, 37).getRGB());
   }
}
