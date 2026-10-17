package bypass.gui.clickgui.defaultgui;

import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;

public class CategoryPanel extends Container {
   private Category category;
   private static final ResourceLocation eyeOpenIcon = new ResourceLocation("client/icon/eye_open.png");
   private static final ResourceLocation eyeClosedIcon = new ResourceLocation("client/icon/eye_close.png");

   public CategoryPanel(Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.category = category;
      this.setDraggable(true);
   }

   @Override
   public void onClick(int i) {
      if (i == 1) {
         this.setExpanded(!this.isExpanded());
      }
   }

   @Override
   public void render(int i, int j) {
      int k = this.getModulesHeight() + 2;
      int l = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.category.getColor() : client.getColorSetting().getValue().getRGB();
      RenderUtil.drawRect(
         this.getX() - 0.5F, this.getYPos() - 0.5F, this.getX() + this.getWidth() + 0.5F, this.getYPos() + this.getHeight() + k + 0.5F, l
      );
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight() + k, new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.getX() + 2.0F,
         this.getYPos() + 17.0F,
         this.getX() + this.getWidth() - 2.0F,
         this.getYPos() + this.getHeight() + k - 2.0F,
         new Color(37, 37, 37).getRGB()
      );
      RenderUtil.setColor(this.isExpanded() ? l : new Color(1710618).brighter().brighter().getRGB());
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.isExpanded() ? eyeOpenIcon : eyeClosedIcon);
      Gui.drawModalRectWithCustomSizedTexture((int)(this.getX() + this.getWidth() - 21.0F), (int)this.getYPos() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      RenderUtil.setColor(l);
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s.png", this.category.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.getX() + this.getWidth() - 11.0F), (int)this.getYPos() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GuiMainMenu.latoBoldFont15.drawStringWithShadow(this.category.getDisplayName().toLowerCase(), this.getX() + 4.0F, this.getYPos() + 3.5F, -1);
      if (this.isExpanded() || this.expandAnimation.isRunning()) {
         byte b = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof ModuleButton) {
               ModuleButton moduleButton = (ModuleButton)element;
               moduleButton.setPosition(this.getX() + 2.0F, this.getYPos() + b + 17.0F);
               if (this.isExpanded()) {
                  moduleButton.render(i, j);
               }

               b += !moduleButton.isExpanded()
                  ? 15
                  : moduleButton.getSettingsHeight() + (this.hasExpandedMapComponent((ModuleButton)element) ? this.getMapComponentsHeight((ModuleButton)element) + 2 : 0);
            }
         }
      }
   }

   private int getModulesHeight() {
      int i = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof ModuleButton) {
               if (((ModuleButton)element).isExpanded()) {
                  i += ((ModuleButton)element).getSettingsHeight()
                     + (this.hasExpandedMapComponent((ModuleButton)element) ? this.getMapComponentsHeight((ModuleButton)element) + 2 : 0);
               } else {
                  i += 15;
               }
            }
         }
      }

      return i;
   }

   private boolean hasExpandedMapComponent(ModuleButton moduleButton) {
      if (!moduleButton.getChildren().isEmpty()) {
         for (Element element : moduleButton.getChildren()) {
            if (element instanceof MapComponent && ((MapComponent)element).isExpanded()) {
               return true;
            }
         }
      }

      return false;
   }

   private int getMapComponentsHeight(ModuleButton moduleButton) {
      int i = 0;
      if (!moduleButton.getChildren().isEmpty()) {
         for (Element element : moduleButton.getChildren()) {
            if (element instanceof MapComponent) {
               i += ((MapComponent)element).getChildrenHeight();
            }
         }
      }

      return i;
   }

   public Category getCategory() {
      return this.category;
   }
}
