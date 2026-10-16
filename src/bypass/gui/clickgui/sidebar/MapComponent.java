package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.setting.MapSetting;
import java.awt.Color;

public class MapComponent extends SidebarSettingComponent {
   private Animation arrowAnimation = new TimedAnimation().setDuration(450).setEasing(Easing.LINEAR).forwards().start();
   private final MapSetting mapSetting;
   private boolean mapPanelClosed;
   private int listOffset = 25;
   private boolean arrowForward;

   public MapComponent(ModuleButton moduleButton, MapSetting mapSetting, int i, int j, int k, int l) {
      super(moduleButton, mapSetting, i, j, k, l);
      this.setParent(moduleButton);
      this.mapSetting = mapSetting;
   }

   @Override
   public void onClick(int i) {
      if (i == 0 && this.isVisible()) {
         if (this.getModuleButton().getSelectedMap() != null) {
            if (this.getModuleButton().getSelectedMap() != this) {
               this.getModuleButton().setSelectedMap(this);
            } else {
               this.getModuleButton().setSelectedMap(null);
            }
         } else {
            this.getModuleButton().setSelectedMap(this);
         }
      }
   }

   @Override
   public void render(int i, int j) {
      if (!this.arrowAnimation.isRunning() && (this.isHovered(i, j) || this.getModuleButton().getSelectedMap() == this)) {
         if (this.arrowForward) {
            this.arrowAnimation.forwards().start();
         } else {
            this.arrowAnimation.backwards().start();
         }

         this.arrowForward = !this.arrowForward;
      }

      if (!this.isHovered(i, j) && this.getModuleButton().getSelectedMap() != this) {
         this.arrowAnimation.forwards();
      }

      if (this.fadeAlpha > 40) {
         client.getCustomFontRenderer()
            .drawString(this.mapSetting.getName(), this.getX(), this.getYPos() - 2.0F, new Color(255, 255, 255, this.fadeAlpha).getRGB());
      }

      int k = Astolfo.INSTANCE.getSidebarGui().getPulseColor();
      RenderUtil.drawArrow(
         this.getX() + this.getWidth() - 8.0F + this.arrowAnimation.getValue(-8.0, 0.0),
         this.getYPos(),
         5.0,
         true,
         this.getModuleButton().getSelectedMap() == this ? k : new Color(255, 255, 255, this.fadeAlpha).getRGB()
      );
      Sidebar sidebar = this.getModuleButton().getCategoryTab().getSidebar();
      int l = this.listOffset;
      if (!this.mapPanelClosed && this.getModuleButton().getPreviousMap() == this && this.getModuleButton().getSelectedMap() == null
         || this.getModuleButton().getPreviousMap() == null && this.getModuleButton().getSelectedMap() == this
         || this.getModuleButton().getPreviousMap() != this && this.getModuleButton().getSelectedMap() == this) {
         for (Element element : this.getChildren()) {
            if (element instanceof SidebarSettingComponent) {
               SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element;
               sidebarSettingComponent.setPosition(this.getX() + 155.0F, sidebar.getYPos() + l);
               sidebarSettingComponent.render(i, j);
               if (!sidebarSettingComponent.fadeAnimation.isRunning()) {
                  sidebarSettingComponent.fadeAnimation.forwards().start();
               }

               sidebarSettingComponent.fadeAlpha = (int)sidebarSettingComponent.fadeAnimation.getValue(0.0, 255.0);
               l += 15;
            }
         }
      } else {
         for (Element element2 : this.getChildren()) {
            if (element2 instanceof SidebarSettingComponent) {
               SidebarSettingComponent sidebarSettingComponent2 = (SidebarSettingComponent)element2;
               if (!sidebarSettingComponent2.fadeAnimation.isRunning()) {
                  sidebarSettingComponent2.fadeAnimation.backwards().start();
               }

               sidebarSettingComponent2.fadeAlpha = (int)sidebarSettingComponent2.fadeAnimation.getValue(0.0, 255.0);
            }
         }
      }

      this.mapPanelClosed = this.getModuleButton().getCategoryTab().getSidebar().getWidth() == 270;
   }

   public MapSetting getMapSetting() {
      return this.mapSetting;
   }
}
