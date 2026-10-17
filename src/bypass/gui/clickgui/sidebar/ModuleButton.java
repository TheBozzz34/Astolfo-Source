package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Module;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ModuleButton extends Container {
   public Animation fadeAnimation = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private Animation hoverAnimation = new TimedAnimation().setDuration(100).setEasing(Easing.LINEAR);
   private Module module;
   private MapComponent selectedMap;
   private MapComponent previousMap;
   public boolean settingsPanelClosed;
   public int settingsScrollOffset = 25;
   private boolean listeningForBind;
   public int fadeAlpha;

   public ModuleButton(CategoryTab categoryTab, Module module, int i, int j, int k, int l) {
      super(module.getName(), i, j, k, l);
      this.setParent(categoryTab);
      this.module = module;
   }

   @Override
   public void onClick(int i) {
      CategoryTab categoryTab = this.getCategoryTab();
      int j = categoryTab.moduleScrollOffset;
      int k = categoryTab.getSidebar().getHeight() - categoryTab.getModuleButtonY(this);
      boolean bl = k >= j - 10 && categoryTab.getParent().getYPos() - 10.0F < this.getYPos();
      if (bl) {
         switch (i) {
            case 0:
               this.module.toggle();
               break;
            case 1:
               if (this.getCategoryTab().getSelectedModule() != null) {
                  if (this.getCategoryTab().getSelectedModule() != this) {
                     if (!this.module.getSettings().isEmpty()) {
                        this.getCategoryTab().setSelectedModule(this);
                     }
                  } else {
                     this.getCategoryTab().setSelectedModule(null);
                  }
               } else if (!this.module.getSettings().isEmpty()) {
                  this.getCategoryTab().setSelectedModule(this);
               }
               break;
            case 2:
               this.listeningForBind = true;
         }
      }
   }

   @Override
   public void render(int i, int j) {
      if (!this.hoverAnimation.isRunning()) {
         if (this.isHovered(i, j)) {
            this.hoverAnimation.forwards().start();
         } else {
            this.hoverAnimation.backwards().start();
         }
      }

      int k = (int)this.hoverAnimation.getValue(0.0, 255.0);
      if (this.module.isEnabled() && this.fadeAlpha > 40) {
         RenderUtil.drawRect(
            this.getX() - 6.0F, this.getYPos() - 2.0F, this.getX() - 5.0F, this.getYPos() + 9.0F, new Color(255, 255, 255, this.fadeAlpha).getRGB()
         );
      }

      if (this.fadeAlpha > 40) {
         client.getCustomFontRenderer()
            .drawString(this.module.getName(), this.getX(), this.getYPos() - 2.0F, new Color(136, 141, 157, this.fadeAlpha).getRGB());
      }

      if (k > 40) {
         client.getCustomFontRenderer().drawString(this.module.getName(), this.getX(), this.getYPos() - 2.0F, new Color(255, 255, 255, k).getRGB());
      }

      int l = this.settingsScrollOffset;
      Sidebar sidebar = this.getCategoryTab().getSidebar();
      if (!this.settingsPanelClosed && this.getCategoryTab().getPreviousModule() == this && this.getCategoryTab().getSelectedModule() == null
         || this.getCategoryTab().getPreviousModule() == null && this.getCategoryTab().getSelectedModule() == this
         || this.getCategoryTab().getPreviousModule() != this && this.getCategoryTab().getSelectedModule() == this) {
         int m = Astolfo.INSTANCE.getSidebarGui().getPulseColor();
         GuiMainMenu.robotoRegularFont28
            .drawString(
               this.module.getName(),
               this.getX() + 172.5F - GuiMainMenu.robotoRegularFont28.getStringWidth(this.module.getName()) / 2.0F,
               sidebar.getYPos() + 3.0F,
               m
            );
         RenderUtil.drawRect(this.getX() + 135.0F, sidebar.getYPos() + 17.0F, this.getX() + 210.0F, sidebar.getYPos() + 18.0F, m);
         if (this.getSelectedMap() != null) {
            GuiMainMenu.robotoRegularFont28
               .drawString(
                  this.getSelectedMap().getMapSetting().getName(),
                  this.getX() + 310.0F - GuiMainMenu.robotoRegularFont28.getStringWidth(this.getSelectedMap().getMapSetting().getName()) / 2.0F,
                  sidebar.getYPos() + 3.0F,
                  -1
               );
         }

         GL11.glEnable(3089);
         SidebarGui.scissor(sidebar.getX(), sidebar.getYPos() + 20.0F, sidebar.getX() + sidebar.getWidth(), sidebar.getYPos() + sidebar.getHeight());

         for (Element element : this.getChildren()) {
            if (element instanceof SidebarSettingComponent) {
               SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element;
               sidebarSettingComponent.setPosition(sidebar.getX() + 135.0F, sidebar.getYPos() + l);
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

      this.settingsPanelClosed = this.getCategoryTab().getSidebar().getWidth() == 115;
   }

   @Override
   public void onMouseScroll(int i, int j, int k) {
      CategoryTab categoryTab = this.getCategoryTab();
      int l = categoryTab.moduleScrollOffset + k;
      int m = categoryTab.getSidebar().getHeight() - categoryTab.getModuleListHeight();
      if (l > 12) {
         categoryTab.moduleScrollOffset = 12;
      } else if (l >= m) {
         categoryTab.moduleScrollOffset += k;
      }
   }

   @Override
   public void onKeyTyped(char c, int i) {
      if (this.listeningForBind) {
         if (i == 1) {
            this.listeningForBind = false;
            return;
         }

         toasts.infoMessage("Bind", String.format("%s has been binded to %s", this.module.getName(), Keyboard.getKeyName(i)));
         this.module.setKey(i);
         this.listeningForBind = false;
      }

      super.onKeyTyped(c, i);
   }

   public int getSettingsListHeight() {
      byte b = 5;

      for (Element element : this.getChildren()) {
         if (element instanceof SidebarSettingComponent) {
            b += 15;
         }
      }

      return b;
   }

   public int getSettingOffset(SidebarSettingComponent sidebarSettingComponent) {
      byte b = 5;

      for (Element element : this.getChildren()) {
         if (element instanceof SidebarSettingComponent) {
            if (sidebarSettingComponent == element) {
               return b;
            }

            b += 15;
         }
      }

      return b;
   }

   public CategoryTab getCategoryTab() {
      return (CategoryTab)this.getParent();
   }

   public void setSelectedMap(MapComponent mapComponent) {
      MapComponent mapComponent2 = this.selectedMap;
      this.selectedMap = mapComponent;
      this.previousMap = mapComponent2;
   }

   public MapComponent getSelectedMap() {
      return this.selectedMap;
   }

   public MapComponent getPreviousMap() {
      return this.previousMap;
   }

   public boolean isListeningForBind() {
      return this.listeningForBind;
   }
}
