package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import java.awt.Color;

public class TitleBar extends Container {
   public TitleBar(int i, int j, int k, int l) {
      super("astolfo", i, j, k, l);
      this.setDraggable(true);
   }

   @Override
   public void onClick(int i) {
   }

   @Override
   public void render(int i, int j) {
      RenderUtil.drawRect(
         this.getX() - 1.0F, this.getYPos() - 1.0F, this.getX() + this.getWidth() + 1.0F, this.getYPos() + this.getHeight() + 1.0F, client.getColor().getRGB()
      );
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(26, 26, 26).getRGB());
      client.getCustomFontRenderer().drawString(this.getName(), this.getX() + 3.0F, this.getYPos() + 3.0F, -1);
      RenderUtil.drawRect(this.getX() + 85.0F, this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(37, 37, 37).getRGB());
   }
}
