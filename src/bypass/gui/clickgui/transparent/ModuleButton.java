package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Module;
import bypass.notification.NotificationType;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ModuleButton extends Container {
   private Container parentPanel;
   private Module module;
   private int openTicks = 0;
   private final Color grayColor = new Color(85, 85, 85);
   private static final Color shadowColor = new Color(1, 1, 1, 70);
   private static final Color whiteColor = new Color(255, 255, 255, 170);
   private boolean listeningForBind;
   private int hoverAlpha;

   public ModuleButton(Container container, Module module, int i, int j) {
      super(module.getName(), 0.0F, 0.0F, i, j);
      this.parentPanel = container;
      this.module = module;
   }

   @Override
   public void render(int i, int j) {
      if (this.isHovered(i, j)) {
         if (this.hoverAlpha < 120) {
            this.hoverAlpha += 5;
         }
      } else if (this.hoverAlpha > 0) {
         this.hoverAlpha -= 5;
      }

      if (this.hoverAlpha > 0) {
         RenderUtil.drawRect(
            this.getX() + 1.0F,
            this.getYPos() - 1.0F,
            this.getX() + this.getWidth() + 1.0F,
            this.getYPos() + this.getHeight() + 4.0F,
            new Color(255, 255, 255, this.hoverAlpha).getRGB()
         );
      }

      if (this.listeningForBind && Mouse.isButtonDown(0) && !this.isHovered(i, j)) {
         this.listeningForBind = false;
      }

      RenderUtil.drawRect(
         this.getX() + 1.0F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth() + 1.0F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            this.module.getName(),
            this.getX() + 3.0F,
            this.getYPos(),
            this.listeningForBind ? Color.RED.getRGB() : (this.module.isEnabled() ? client.getColor().getRGB() : new Color(154, 154, 154).getRGB())
         );
      if (!this.getChildren().isEmpty()) {
         if (!this.isExpanded()) {
            this.drawChevron(
               this.getX() + this.getWidth() - 10.0F,
               this.getYPos() + 4.0F,
               7.0F,
               4.0F,
               2.0F,
               this.module.isEnabled() ? client.getColor().getRGB() : new Color(154, 154, 154).getRGB()
            );
         } else {
            this.drawChevron(
               this.getX() + this.getWidth() - 10.0F,
               this.getYPos() + 8.0F,
               7.0F,
               -4.0F,
               2.0F,
               this.module.isEnabled() ? client.getColor().getRGB() : new Color(154, 154, 154).getRGB()
            );
         }
      }

      if (this.isExpanded() || this.expandAnimation.isRunning()) {
         int k = 0;

         for (Element element : this.getChildren()) {
            if (element instanceof Component) {
               ((Component)element).setPosition(this.getX() + 1.0F, this.getYPos() + k + 15.0F);
               if (this.isExpanded()) {
                  element.render(i, j);
               }

               k += 15 + (element instanceof MapComponent && ((MapComponent)element).isExpanded() ? ((MapComponent)element).getChildrenHeight() : 0);
               k = (int)(k - this.expandAnimation.getValue(0.0, k));
            }
         }
      }

      if (this.module.isEnabled()) {
         this.stateAnimation.backwards().start();
      } else {
         this.stateAnimation.forwards().start();
      }

      if (this.parentPanel.isExpanded()) {
         this.openTicks++;
      } else {
         this.openTicks = 0;
      }
   }

   @Override
   public boolean isHovered(int i, int j) {
      return i >= this.getX() && i <= this.getX() + this.getWidth() && j >= this.getYPos() && j <= this.getYPos() + this.getHeight();
   }

   private void drawChevron(float f, float g, float h, float f2, float f3, int i) {
      RenderUtil.drawLine(f3, f, g, f + h / 2.0F, g + f2, i);
      RenderUtil.drawLine(f3, f + h / 2.0F, g + f2, f + h, g, i);
   }

   @Override
   public void onClick(int i) {
      if (this.parentPanel.isExpanded()) {
         if (i == 0) {
            if (this.listeningForBind) {
               this.listeningForBind = false;
               return;
            }

            Module module2 = client.getModuleManager().getModule(this.module.getName());
            module2.setEnabled(!module2.isEnabled());
         }

         if (i == 1 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))) {
            this.listeningForBind = true;
            return;
         }

         if (i == 1 && this.openTicks > 0 && !this.getChildren().isEmpty() && !this.listeningForBind) {
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
      if (this.listeningForBind && ChatAllowedCharacters.isAllowedCharacter(c)) {
         client.getNotificationManager()
            .createNotification(String.format("%s's bind is now %s", this.getModule().getName(), Character.toUpperCase(c)), "", NotificationType.SUCCESS, 1500)
            .show();
         this.getModule().setKey(i);
         this.listeningForBind = false;
      }

      super.onKeyTyped(c, i);
   }

   public int getExpandedHeight() {
      byte b = 0;
      if (this.isExpanded()) {
         for (Element element : this.getChildren()) {
            if (element instanceof SettingComponent || element instanceof SettingContainer) {
               b += 15;
            }
         }
      }

      return b + 15;
   }

   public Module getModule() {
      return this.module;
   }
}
