package bypass.gui.component;

import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;

public abstract class Button extends Component {
   public Button(String string, float f, float g, int i, int j) {
      super(string, f, g, i, j);
   }

   @Override
   public void onClick(int i) {
      this.onPress();
   }

   public abstract void onPress();

   @Override
   public void render(int i, int j) {
      int k = client.getSidebarGui().getBaseColor();
      int l = client.getSidebarGui().getPanelColor();
      int m = client.getSidebarGui().getSurfaceColor();
      RenderUtil.drawRect(this.getX() + 1.0F, this.getYPos() + 1.0F, this.getX() + this.getWidth() - 2.0F, this.getYPos() + this.getHeight() - 2.0F, k);
      RenderUtil.drawRect(this.getX() + 2.0F, this.getYPos() + 2.0F, this.getX() + this.getWidth() - 3.0F, this.getYPos() + this.getHeight() - 3.0F, m);
      GuiMainMenu.c
         .drawString(
            this.getName(),
            this.getX() + (this.getWidth() - GuiMainMenu.c.getStringWidth(this.getName())) / 2 + 1.0F,
            this.getYPos() + this.getHeight() / 2 - 3.0F,
            Color.WHITE.getRGB()
         );
   }
}
