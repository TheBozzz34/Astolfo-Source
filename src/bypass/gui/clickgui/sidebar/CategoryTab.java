package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Button;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CategoryTab extends Container {
   public Animation hoverAnimation = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private ModuleButton selectedModule;
   private ModuleButton previousModule;
   private Category category;
   public boolean sidebarClosed;
   public int moduleScrollOffset = 12;
   public final int initialModuleListHeight;
   private int hoverAlpha;

   public CategoryTab(Sidebar sidebar, Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.setParent(sidebar);
      this.category = category;
      this.initialModuleListHeight = this.getModuleListHeight();
   }

   @Override
   public void onClick(int i) {
      if (i == 0) {
         if (this.getSidebar().getSelectedTab() == this) {
            this.selectedModule = null;
            this.getSidebar().setSelectedTab(null);
         } else {
            this.getSidebar().setSelectedTab(this);
         }
      }
   }

   @Override
   public void render(int i, int j) {
      if (!this.hoverAnimation.isRunning()) {
         if (!this.isHovered(i, j) && this.getSidebar().getSelectedTab() != this) {
            this.hoverAnimation.backwards().start();
         } else {
            this.hoverAnimation.forwards().start();
         }
      }

      this.hoverAlpha = (int)this.hoverAnimation.getValue(0.0, 255.0);
      int k = Math.min(this.hoverAlpha, client.getSidebarGui().fadeAlpha);
      int l = client.getSidebarGui().getSurfaceColor();
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), l);
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(29, 31, 37, k).getRGB());
      RenderUtil.setColor(new Color(136, 141, 157, client.getSidebarGui().fadeAlpha).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s_good.png", this.category.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.getX() + 10.0F), (int)this.getYPos() + 6, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
      RenderUtil.setColor(new Color(255, 255, 255, k).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s_good.png", this.category.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.getX() + 10.0F), (int)this.getYPos() + 6, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
      if (this.getSidebar().getSelectedTab() == this || k > 35) {
         GuiMainMenu.c
            .drawString(
               this.getName().toUpperCase(),
               this.getX() + this.getWidth() / 2.0F - GuiMainMenu.c.getStringWidth(this.getName()) / 2.0F - 0.5F,
               this.getYPos() + 23.0F,
               new Color(255, 255, 255, k).getRGB()
            );
      }

      int m = this.moduleScrollOffset;
      if (!this.sidebarClosed && this.getSidebar().getPreviousTab() == this && this.getSidebar().getSelectedTab() == null
         || this.getSidebar().getPreviousTab() == null && this.getSidebar().getSelectedTab() == this
         || this.getSidebar().getPreviousTab() != this && this.getSidebar().getSelectedTab() == this) {
         GL11.glEnable(3089);
         SidebarGui.scissor(
            this.getSidebar().getX(),
            this.getSidebar().getYPos(),
            this.getSidebar().getX() + this.getSidebar().getWidth(),
            this.getSidebar().getYPos() + this.getSidebar().getHeight()
         );

         for (Element element : this.getChildren()) {
            if (element instanceof ModuleButton) {
               ModuleButton moduleButton = (ModuleButton)element;
               moduleButton.setPosition(this.getSidebar().getX() + 40.0F, this.getSidebar().getYPos() + m);
               moduleButton.render(i, j);
               if (!moduleButton.fadeAnimation.isRunning()) {
                  moduleButton.fadeAnimation.forwards().start();
               }

               moduleButton.fadeAlpha = (int)moduleButton.fadeAnimation.getValue(0.0, 255.0);
               m += 15;
            } else if (element instanceof Button) {
               Component component = (Component)element;
               component.setPosition(this.getSidebar().getX() + 40.0F, this.getSidebar().getYPos() + m);
               component.render(i, j);
               m += 13;
            }

            if (element instanceof StringComponent) {
               Component component2 = (Component)element;
               component2.setPosition(this.getSidebar().getX() + 40.0F, this.getSidebar().getYPos() + m);
               component2.render(i, j);
               m += 20;
            }
         }

         GL11.glDisable(3089);
      } else {
         for (Element element2 : this.getChildren()) {
            if (element2 instanceof ModuleButton) {
               ModuleButton moduleButton2 = (ModuleButton)element2;
               if (!moduleButton2.fadeAnimation.isRunning()) {
                  moduleButton2.fadeAnimation.backwards().start();
               }

               moduleButton2.fadeAlpha = (int)moduleButton2.fadeAnimation.getValue(0.0, 255.0);
            }
         }
      }

      this.sidebarClosed = this.getSidebar().getWidth() == 34;
   }

   public int getModuleListHeight() {
      byte b = 5;

      for (Element element : this.getChildren()) {
         if (element instanceof ModuleButton) {
            b += 15;
         }
      }

      return b;
   }

   public int getModuleButtonY(ModuleButton moduleButton) {
      byte b = 5;

      for (Element element : this.getChildren()) {
         if (element instanceof ModuleButton) {
            if (moduleButton == element) {
               return b;
            }

            b += 15;
         }
      }

      return b;
   }

   public void setSelectedModule(ModuleButton moduleButton) {
      if (moduleButton == null) {
         if (this.previousModule != null) {
            this.previousModule.setSelectedMap(null);
         }
      } else {
         moduleButton.setSelectedMap(null);
      }

      ModuleButton moduleButton2 = this.selectedModule;
      this.selectedModule = moduleButton;
      this.previousModule = moduleButton2;
   }

   public ModuleButton getSelectedModule() {
      return this.selectedModule;
   }

   public ModuleButton getPreviousModule() {
      return this.previousModule;
   }

   public Sidebar getSidebar() {
      return (Sidebar)this.getParent();
   }
}
