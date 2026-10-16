package bypass.gui.clickgui.transparent;

import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class CategoryPanel extends Container {
   private Category category;
   private static final Color lightColor = new Color(38, 37, 38);
   private static final Color darkColor = new Color(26, 26, 26);

   public CategoryPanel(Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.category = category;
      this.setDraggable(true);
   }

   @Override
   public void render(int i, int j) {
      int k = this.getModulesHeight() + 4;
      RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight() + 6.0F, client.getColor().getRGB());
      client.getCustomFontRenderer().drawStringWithShadow(this.category.getDisplayName(), this.getX() + 2.0F, this.getYPos() + 2.0F, Color.WHITE.getRGB());
      RenderUtil.setColor(this.isExpanded() ? client.getColor().brighter().getRGB() : new Color(55, 55, 55).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s.png", this.category.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.getX() + this.getWidth() - 10.0F), (int)this.getYPos() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      if (this.isExpanded() || this.expandAnimation.isRunning()) {
         int l = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof ModuleButton) {
               ModuleButton moduleButton = (ModuleButton)element;
               moduleButton.setPosition(this.getX(), this.getYPos() + l + 17.0F);
               if (this.isExpanded()) {
                  moduleButton.render(i, j);
               }

               l += !moduleButton.isExpanded()
                  ? 15
                  : moduleButton.getExpandedHeight() + (this.hasExpandedMap((ModuleButton)element) ? this.getMapComponentsHeight((ModuleButton)element) : 0);
               l = (int)(l - this.expandAnimation.getValue(0.0, l));
            }
         }
      }
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

   private int getModulesHeight() {
      int i = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof ModuleButton) {
               if (((ModuleButton)element).isExpanded()) {
                  i += ((ModuleButton)element).getExpandedHeight()
                     + (this.hasExpandedMap((ModuleButton)element) ? this.getMapComponentsHeight((ModuleButton)element) : 0);
               } else {
                  i += 15;
               }
            }
         }
      }

      return i;
   }

   private boolean hasExpandedMap(ModuleButton moduleButton) {
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
