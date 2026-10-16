package bypass.gui.clickgui.unused;

import bypass.gui.component.Component;
import bypass.render.RenderUtil;
import java.awt.Color;

public class ListItem extends Component {
   private ListPanel listPanel;

   public ListItem(ListPanel listPanel, String string, int i, int j) {
      super(string, 0.0F, 0.0F, i, j);
      this.listPanel = listPanel;
   }

   @Override
   public void onClick(int i) {
      this.listPanel.setSelectedItem(this);
   }

   @Override
   public void render(int i, int j) {
      if (this.listPanel.getSelectedItem() == this) {
         RenderUtil.drawRect(
            this.getX() + 2.0F, this.getYPos() + 3.0F, this.getX() + 3.0F, this.getYPos() + this.getHeight() + 3.0F, client.getColor().getRGB()
         );
      }

      client.getCustomFontRenderer().drawStringWithShadow(this.getName(), this.getX() + 7.0F, this.getYPos() + 2.0F, Color.WHITE.getRGB());
   }
}
