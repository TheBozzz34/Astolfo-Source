package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Module;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.input.Keyboard;

public class ModuleButton extends Container {
   private final CategoryPanel categoryPanel;
   private final Module module;
   private int openTicks = 0;
   private boolean listeningForBind;

   public ModuleButton(CategoryPanel categoryPanel, Module module, int i, int j) {
      super(module.getName(), 0.0F, 0.0F, i, j);
      this.categoryPanel = categoryPanel;
      this.module = module;
   }

   @Override
   public void onClick(int i) {
      if (this.categoryPanel.isExpanded()) {
         if (i == 0) {
            this.module.setEnabled(!this.module.isEnabled());
         }

         if (i == 1 && DefaultClickGuiScreen.showKeybinds && Keyboard.isKeyDown(42)) {
            this.listeningForBind = !this.listeningForBind;
            return;
         }

         if (i == 1 && this.openTicks > 0 && !this.getChildren().isEmpty()) {
            this.setExpanded(!this.isExpanded());
            if (this.isExpanded()) {
               this.expandAnimation.backwards().rewind().start();
            } else {
               this.expandAnimation.forwards().rewind().start();
            }
         }
      }
   }

   @Override
   public void onKeyTyped(char c, int i) {
      if (DefaultClickGuiScreen.showKeybinds) {
         if (this.listeningForBind && i == 1) {
            this.listeningForBind = false;
         }

         if (this.listeningForBind) {
            this.module.setKey(i);
            this.listeningForBind = false;
         }
      }

      super.onKeyTyped(c, i);
   }

   @Override
   public void render(int i, int j) {
      int k = this.getExpandedHeight();
      boolean bl = HUD.gui.getSetting("Left-Align").asBoolean();
      int l = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.categoryPanel.getCategory().getColor() : client.getColorSetting().getValue().getRGB();
      if (this.module.isEnabled()) {
         if (this.isHovered(i, j)) {
            RenderUtil.drawRect(
               this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(l).darker().getRGB()
            );
         } else {
            RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), l);
         }
      } else if (this.isHovered(i, j - 1)) {
         RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), new Color(52, 52, 52).getRGB());
      }

      if (this.isExpanded()) {
         RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + k, new Color(37, 37, 37).darker().getRGB());
      }

      if (DefaultClickGuiScreen.showKeybinds && (this.module.getKey() != 0 || this.listeningForBind)) {
         try {
            String string = String.format("[%s]", this.listeningForBind ? "..." : Keyboard.getKeyName(this.module.getKey()));
            GuiMainMenu.m
               .drawString(
                  string,
                  bl ? this.getX() + this.getWidth() - GuiMainMenu.m.getStringWidth(string) - 2.0F : this.getX() + 3.0F,
                  this.getYPos() + 2.0F,
                  Color.GRAY.getRGB()
               );
         } catch (Exception exception) {
         }
      }

      GuiMainMenu.m
         .drawString(
            this.module.getName().toLowerCase(),
            bl ? this.getX() + 3.0F : this.getX() + this.getWidth() - GuiMainMenu.m.getStringWidth(this.module.getName().toLowerCase(), true) - 3.0F,
            this.getYPos() + 3.0F,
            this.isExpanded() && this.module.isEnabled() ? l : 13421772
         );
      if (this.isExpanded()) {
         int n = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof Component) {
               ((Component)element).setPosition(this.getX() + 2.0F, this.getYPos() + n + 15.0F);
               if (this.isExpanded()) {
                  element.render(i, j);
               }

               n += ((Component)element).getHeight()
                  + (element instanceof MapComponent && ((MapComponent)element).isExpanded() ? ((MapComponent)element).getChildrenHeight() : 0);
            }
         }
      }

      if (this.categoryPanel.isExpanded()) {
         this.openTicks++;
      } else {
         this.openTicks = 0;
      }
   }

   public int getSettingsHeight() {
      int i = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof SettingComponent || element instanceof SettingContainer) {
               i += ((Component)element).getHeight();
            }
         }
      }

      return i + 18;
   }

   public int getExpandedHeight() {
      int i = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof SettingComponent || element instanceof SettingContainer) {
               i += ((Component)element).getHeight();
            }

            if (element instanceof MapComponent) {
               i += ((MapComponent)element).getChildrenHeight();
            }
         }
      }

      return i + 18;
   }

   public CategoryPanel getParent() {
      return this.categoryPanel;
   }
}
