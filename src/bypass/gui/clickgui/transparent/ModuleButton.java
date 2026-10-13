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
   private static final long d = 71779781047116L;

   public ModuleButton(Container var1, Module var2, int var3, int var4) {
      super(var2.getName(), 0.0F, 0.0F, var3, var4);
      this.L = var1;
      this.A = var2;
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 94864256331070L;
      long var5 = var3 ^ 39308001293406L;
      if (this.G(var1, var2)) {
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

      if (this.K && Mouse.isButtonDown(0) && !this.G(var1, var2)) {
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
         int var8 = 0;

         for (Element var10 : this.M_ArrayList()) {
            if (var10 instanceof Component) {
               ((Component)var10).r(this.M() + 1.0F, this.E_float() + var8 + 15.0F);
               if (this.s_boolean()) {
                  var10.render(var1, var2);
               }

               var8 += 15 + (var10 instanceof MapComponent && ((MapComponent)var10).s_boolean() ? ((MapComponent)var10).d() : 0);
               var8 = (int)(var8 - this.U.getValue(0.0, var8));
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
   public boolean G(int var1, int var2) {
      return var1 >= this.M() && var1 <= this.M() + this.x() && var2 >= this.E_float() && var2 <= this.E_float() + this.Y();
   }

   private void u(float var1, float var2, float var3, float var4, float var5, int var6) {
      RenderUtil.drawLine(var5, var1, var2, var1 + var3 / 2.0F, var2 + var4, var6);
      RenderUtil.drawLine(var5, var1 + var3 / 2.0F, var2 + var4, var1 + var3, var2, var6);
   }

   @Override
   public void l(int var1) {
      long var2 = d ^ 36655112826250L;
      long var4 = var2 ^ 96109111944053L;
      if (this.L.s_boolean()) {
         if (var1 == 0) {
            if (this.K) {
               this.K = false;
               return;
            }

            Module var7 = client.getModuleManager().getModule(this.A.getName());
            var7.setEnabled(!var7.isEnabled());
         }

         if (var1 == 1 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))) {
            this.K = true;
            return;
         }

         if (var1 == 1 && this.O > 0 && !this.M_ArrayList().isEmpty() && !this.K) {
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
   public void P(char var1, int var2) {
      long var3 = d ^ 78080380800216L;
      long var5 = var3 ^ 70801581277903L;
      if (this.K && ChatAllowedCharacters.isAllowedCharacter(var1)) {
         client.getNotificationManager()
            .i(String.format("%s's bind is now %s", this.a().getName(), Character.toUpperCase(var1)), "", NotificationType.SUCCESS, 1500)
            .K();
         this.a().setKey(var2);
         this.K = false;
      }

      super.P(var1, var2);
   }

   public int d() {
      byte var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof SettingComponent || var6 instanceof SettingContainer) {
               var4 += 15;
            }
         }
      }

      return var4 + 15;
   }

   public Module a() {
      return this.A;
   }
}
