package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import java.awt.Color;

public class ListPanel extends Container {
   private static final Color backgroundColor = new Color(34, 33, 32);
   private static final Color borderColor = new Color(26, 26, 26);
   private ListItem selectedItem;

   public ListPanel(String string, int i, int j, int k, int l) {
      super(string, i, j, k, l);
      this.setDraggable(true);
   }

   @Override
   public void onClick(int i) {
      if (i == 1) {
         this.setExpanded(!this.isExpanded());
         if (this.isExpanded()) {
            this.expandAnimation.backwards().rewind().start();
         } else {
            this.expandAnimation.forwards().rewind().start();
         }
      }
   }

   @Override
   public void render(int i, int j) {
      int k = this.getItemsHeight() + 4;
      RenderUtil.drawRect(
         this.getX() - 1.5F, this.getYPos(), this.getX() + this.getWidth() + 1.5F, this.getYPos() + this.getHeight() + k + 3.5F, borderColor.getRGB()
      );
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight() + k + 2.0F, backgroundColor.getRGB());
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight() + 6.0F, borderColor.getRGB());
      client.getCustomFontRenderer().drawStringWithShadow("Config", this.getX() + 3.0F, this.getYPos() + 2.0F, Color.WHITE.getRGB());
      if (this.isExpanded() || this.expandAnimation.isRunning()) {
         int l = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof ListItem) {
               ListItem listItem = (ListItem)element;
               listItem.setPosition(this.getX(), this.getYPos() + l + 15.0F);
               if (this.isExpanded()) {
                  listItem.render(i, j);
               }

               l += 15;
               l = (int)(l - this.expandAnimation.getValue(0.0, l));
            }
         }
      }
   }

   private int getItemsHeight() {
      byte b = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            b += 15;
         }
      }

      return b;
   }

   public ListItem getSelectedItem() {
      return this.selectedItem;
   }

   public void setSelectedItem(ListItem listItem) {
      this.selectedItem = listItem;
   }
}
