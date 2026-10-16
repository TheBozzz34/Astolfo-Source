package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import java.awt.Color;

public class Sidebar extends Container {
   private CategoryTab selectedTab;
   private CategoryTab previousTab;
   private Animation unusedAnimation = new TimedAnimation().setDuration(250).setEasing(Easing.LINEAR);
   private int unusedInt;
   private String unusedString;

   public Sidebar(int i, int j, int k, int l) {
      super("astolfo #" + client.getVersion(), i, j, k, l);
      this.setDraggable(true);
   }

   @Override
   public void onClick(int i) {
   }

   @Override
   public boolean isHovered(int i, int j) {
      return i >= this.getX() && i <= this.getX() + this.getWidth() && j >= this.getYPos() && j <= this.getYPos() + 20.0F;
   }

   @Override
   public void render(int i, int j) {
      int k = client.getSidebarGui().getBaseColor();
      int l = client.getSidebarGui().getPanelColor();
      int m = client.getSidebarGui().getSurfaceColor();
      int n = client.getSidebarGui().getAnimationStep();
      if (this.selectedTab != null) {
         if (this.getWidth() < 130) {
            this.setWidth(this.getWidth() + n);
         } else if (this.selectedTab.getSelectedModule() != null) {
            if (this.selectedTab.getSelectedModule().getSelectedMap() != null) {
               if (this.getWidth() < 410) {
                  this.setWidth(this.getWidth() + n);
               }
            } else {
               if (this.getWidth() > 285) {
                  this.setWidth(this.getWidth() - n);
               }

               if (this.getWidth() < 285) {
                  this.setWidth(this.getWidth() + n);
               }
            }
         } else if (this.getWidth() > 130) {
            int i2 = this.getWidth() - n;
            if (i2 < 130) {
               i2 = 130;
            }

            this.setWidth(i2);
         }
      } else if (this.getWidth() > 34) {
         this.setWidth(this.getWidth() - n);
      }

      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), k);
      int i3 = Math.min(this.getWidth(), 130);
      RenderUtil.drawRect(this.getX() + 34.0F, this.getYPos(), this.getX() + i3, this.getYPos() + this.getHeight(), l);
      RenderUtil.drawRect(this.getX(), this.getYPos() + 30.0F, this.getX() + 34.0F, this.getYPos() + this.getHeight(), m);
      if (this.getWidth() > 285) {
         RenderUtil.drawRect(
            this.getX() + 285.0F,
            this.getYPos() + 20.0F,
            this.getX() + 285.5F,
            this.getYPos() + this.getHeight() - 20.0F,
            new Color(136, 141, 157, 120).getRGB()
         );
      }

      byte b = 0;

      for (Element element : this.getChildren()) {
         if (element instanceof CategoryTab) {
            Component component = (Component)element;
            component.setPosition(this.getX(), this.getYPos() + b);
            component.render(i, j);
            b += 30;
         }
      }
   }

   public void setSelectedTab(CategoryTab categoryTab) {
      CategoryTab categoryTab2 = this.selectedTab;
      this.selectedTab = categoryTab;
      this.previousTab = categoryTab2;
      if (this.previousTab != null) {
         this.previousTab.moduleScrollOffset = 12;
         this.previousTab.setSelectedModule(null);
      }

      if (categoryTab != null) {
         categoryTab.moduleScrollOffset = 12;
      }
   }

   public CategoryTab getPreviousTab() {
      return this.previousTab;
   }

   public CategoryTab getSelectedTab() {
      return this.selectedTab;
   }
}
