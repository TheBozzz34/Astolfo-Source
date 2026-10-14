package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.render.RenderUtil;
import bypass.util.MathUtil;
import java.awt.Color;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class TargetHud extends Component {
   private EntityLivingBase X;
   private float e;
   private int R;
   private boolean x;
   private boolean Q;
   private final int q = new Color(17, 17, 17, 197).getRGB();
   private static final long c = 93912931594658L;

   public TargetHud(float f, float g, int i, int j) {
      super("targethud", f, g, i, j);
      this.F(true);
   }

   @Override
   public void l(int i) {
   }

   @Override
   public void render(int i, int j) {
      float f = this.M();
      float g = this.E_float();
      float h = this.x();
      float f2 = this.Y();
      if (f == 1.0F) {
         float f3 = this.e - h / 2.0F;
         f = f3;
         this.o(f3);
      }

      RenderUtil.drawRect(f, g, f + h, g + f2, this.q);
      f += 30.0F;
      h -= 30.0F;
      mc.fontRendererObj.drawStringWithShadow(this.X.getName(), f, g + 5.0F, -1);
      String string = MathUtil.ONE_DECIMAL.format(this.X.getHealth() / 2.0F) + " ❤";
      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      mc.fontRendererObj.drawStringWithShadow(string, f / 2.0F, (g + 18.0F) / 2.0F, client.getColorSetting().getValue().getRGB());
      GL11.glPopMatrix();
      int k = (int)(f + (h - 3.0F) * (this.X.getHealth() / this.X.getMaxHealth() * 100.0F * 0.01F));
      if (k > f + h) {
         k = (int)(f + h - 3.0F);
      }

      int l = k - this.R;
      if (Math.abs(l) > 1) {
         this.Q = l >= 0;
         this.x = true;
      }

      if (this.R < k && !this.Q || this.R > k && this.Q) {
         this.x = false;
      }

      if (this.x) {
         if (this.R == 0) {
            this.R = k;
         }

         if (!this.Q) {
            this.R--;
         } else {
            this.R++;
         }
      } else {
         this.R = k;
      }

      if (this.u_boolean()) {
         this.R = k;
      }

      if (this.R > f + h - 3.0F) {
         this.R = (int)(f + h - 3.0F);
      }

      if (k > f + h - 3.0F) {
         k = (int)(f + h - 3.0F);
      }

      RenderUtil.drawRect(
         f, g + f2 - 10.0F, f + h - 3.0F, g + f2 - 3.0F, client.getColorSetting().getValue().darker().darker().darker().darker().getRGB()
      );
      RenderUtil.drawRect(f, g + f2 - 10.0F, this.x ? this.R : k, g + f2 - 3.0F, client.getColorSetting().getValue().getRGB());
      if ((this.x ? this.R : k) - 5 > f) {
         RenderUtil.drawRect(
            (this.x ? this.R : k) - 5,
            g + f2 - 10.0F,
            this.x ? this.R : k,
            g + f2 - 3.0F,
            client.getColorSetting().getValue().darker().getRGB()
         );
      }

      f -= 30.0F;
      GuiInventory.drawEntityOnScreen((int)f + 15, (int)g + 46, 22, -178.0F, 0.0F, this.X);
   }

   public void z(EntityLivingBase entityLivingBase) {
      this.X = entityLivingBase;
   }

   public void y(float f) {
      this.e = f;
   }
}
