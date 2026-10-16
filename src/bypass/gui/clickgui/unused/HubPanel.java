package bypass.gui.clickgui.unused;

import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import java.awt.Color;
import org.lwjgl.input.Mouse;

public class HubPanel extends Container {
   private HubCategory selectedCategory;

   public HubPanel(int i, int j, int k, int l) {
      super("Hub", i, j, k, l);
      this.setDraggable(true);
   }

   @Override
   public void render(int i, int j) {
      RenderUtil.drawRect(
         this.getX() - 4.0F,
         this.getYPos() - 4.0F,
         this.getX() + this.getWidth() + 4.0F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(67, 63, 65).getRGB()
      );
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(32, 31, 32).getRGB());
      if (this.selectedCategory != null) {
         this.selectedCategory.setHubPanel(this);
         client.getCustomFontRenderer().drawString(this.selectedCategory.getName(), this.getX() + 50.0F, this.getYPos() + 5.0F, Color.WHITE.getRGB());
         this.selectedCategory.render(i, j);
      } else {
         client.getCustomFontRenderer()
            .drawString(
               this.getName(),
               this.getX() + this.getWidth() / 2 - client.getCustomFontRenderer().getStringWidth("Hub") / 2,
               this.getYPos() + 5.0F,
               Color.WHITE.getRGB()
            );
         int k = 0;
         int l = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof HubCategory) {
               Component component = (Component)element;
               float f = this.getX() + this.getWidth() / 3 * l;
               int m = k >= 3 ? 110 : 20;
               component.setPosition(f, this.getYPos() + m);
               component.render(i, j);
               ((HubCategory)component).setHubPanel(this);
               k++;
               if (++l >= 3) {
                  l = 0;
               }
            }
         }

         if (this.selectedCategory == null) {
            for (Element element2 : this.getChildren()) {
               if (element2 instanceof HubCategory) {
                  Component component2 = (Component)element2;
                  if (this.isHovered(i, j) && Mouse.isButtonDown(0)) {
                     this.selectedCategory = (HubCategory)component2;
                  }
               }
            }
         }
      }
   }

   @Override
   public void onClick(int i) {
   }

   public HubCategory getSelectedCategory() {
      return this.selectedCategory;
   }
}
