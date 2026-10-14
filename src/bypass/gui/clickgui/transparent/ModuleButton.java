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
   private Container L;
   private Module A;
   private int O = 0;
   private final Color G = new Color(85, 85, 85);
   private static final Color N = new Color(1, 1, 1, 70);
   private static final Color T = new Color(255, 255, 255, 170);
   private boolean K;
   private int Y;

   public ModuleButton(Container container, Module module, int i, int j) {
      super(module.getName(), 0.0F, 0.0F, i, j);
      this.L = container;
      this.A = module;
   }

   @Override
   public void render(int i, int j) {
      if (this.G(i, j)) {
         if (this.Y < 120) {
            this.Y += 5;
         }
      } else if (this.Y > 0) {
         this.Y -= 5;
      }

      if (this.Y > 0) {
         RenderUtil.drawRect(
            this.M() + 1.0F, this.E_float() - 1.0F, this.M() + this.x() + 1.0F, this.E_float() + this.Y() + 4.0F, new Color(255, 255, 255, this.Y).getRGB()
         );
      }

      if (this.K && Mouse.isButtonDown(0) && !this.G(i, j)) {
         this.K = false;
      }

      RenderUtil.drawRect(
         this.M() + 1.0F, this.E_float() - 1.0F, this.M() + this.x() + 1.0F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            this.A.getName(),
            this.M() + 3.0F,
            this.E_float(),
            this.K ? Color.RED.getRGB() : (this.A.isEnabled() ? client.getColor().getRGB() : new Color(154, 154, 154).getRGB())
         );
      if (!this.M_ArrayList().isEmpty()) {
         if (!this.s_boolean()) {
            this.u(
               this.M() + this.x() - 10.0F,
               this.E_float() + 4.0F,
               7.0F,
               4.0F,
               2.0F,
               this.A.isEnabled() ? client.getColor().getRGB() : new Color(154, 154, 154).getRGB()
            );
         } else {
            this.u(
               this.M() + this.x() - 10.0F,
               this.E_float() + 8.0F,
               7.0F,
               -4.0F,
               2.0F,
               this.A.isEnabled() ? client.getColor().getRGB() : new Color(154, 154, 154).getRGB()
            );
         }
      }

      if (this.s_boolean() || this.U.isRunning()) {
         int k = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof Component) {
               ((Component)element).r(this.M() + 1.0F, this.E_float() + k + 15.0F);
               if (this.s_boolean()) {
                  element.render(i, j);
               }

               k += 15 + (element instanceof MapComponent && ((MapComponent)element).s_boolean() ? ((MapComponent)element).d() : 0);
               k = (int)(k - this.U.getValue(0.0, k));
            }
         }
      }

      if (this.A.isEnabled()) {
         this.F.backwards().start();
      } else {
         this.F.forwards().start();
      }

      if (this.L.s_boolean()) {
         this.O++;
      } else {
         this.O = 0;
      }
   }

   @Override
   public boolean G(int i, int j) {
      return i >= this.M() && i <= this.M() + this.x() && j >= this.E_float() && j <= this.E_float() + this.Y();
   }

   private void u(float f, float g, float h, float f2, float f3, int i) {
      RenderUtil.drawLine(f3, f, g, f + h / 2.0F, g + f2, i);
      RenderUtil.drawLine(f3, f + h / 2.0F, g + f2, f + h, g, i);
   }

   @Override
   public void l(int i) {
      if (this.L.s_boolean()) {
         if (i == 0) {
            if (this.K) {
               this.K = false;
               return;
            }

            Module module = client.getModuleManager().getModule(this.A.getName());
            module.setEnabled(!module.isEnabled());
         }

         if (i == 1 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))) {
            this.K = true;
            return;
         }

         if (i == 1 && this.O > 0 && !this.M_ArrayList().isEmpty() && !this.K) {
            this.u(!this.s_boolean());
            if (this.s_boolean()) {
               this.U.backwards().rewind().start();
            } else {
               this.U.forwards().rewind().start();
            }
         }
      }
   }

   @Override
   public void P(char c, int i) {
      if (this.K && ChatAllowedCharacters.isAllowedCharacter(c)) {
         client.getNotificationManager()
            .i(String.format("%s's bind is now %s", this.a().getName(), Character.toUpperCase(c)), "", NotificationType.SUCCESS, 1500)
            .K();
         this.a().setKey(i);
         this.K = false;
      }

      super.P(c, i);
   }

   public int d() {
      byte b = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof SettingComponent || element instanceof SettingContainer) {
               b += 15;
            }
         }
      }

      return b + 15;
   }

   public Module a() {
      return this.A;
   }
}
