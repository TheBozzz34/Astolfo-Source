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

   public TargetHud(float var1, float var2, int var3, int var4) {
      super("targethud", var1, var2, var3, var4);
      this.F(true);
   }

   @Override
   public void l(int var1) {
   }

   @Override
   public void render(int var1, int var2) {
      float var7 = this.M();
      float var8 = this.E_float();
      float var9 = this.x();
      float var10 = this.Y();
      if (var7 == 1.0F) {
         float var11 = this.e - var9 / 2.0F;
         var7 = var11;
         this.o(var11);
      }

      RenderUtil.drawRect(var7, var8, var7 + var9, var8 + var10, this.q);
      var7 += 30.0F;
      var9 -= 30.0F;
      mc.fontRendererObj.drawStringWithShadow(this.X.getName(), var7, var8 + 5.0F, -1);
      String var17 = MathUtil.ONE_DECIMAL.format(this.X.getHealth() / 2.0F) + " ❤";
      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      mc.fontRendererObj.drawStringWithShadow(var17, var7 / 2.0F, (var8 + 18.0F) / 2.0F, client.getColorSetting().getValue().getRGB());
      GL11.glPopMatrix();
      int var12 = (int)(var7 + (var9 - 3.0F) * (this.X.getHealth() / this.X.getMaxHealth() * 100.0F * 0.01F));
      if (var12 > var7 + var9) {
         var12 = (int)(var7 + var9 - 3.0F);
      }

      int var13 = var12 - this.R;
      if (Math.abs(var13) > 1) {
         this.Q = var13 >= 0;
         this.x = true;
      }

      if (this.R < var12 && !this.Q || this.R > var12 && this.Q) {
         this.x = false;
      }

      if (this.x) {
         if (this.R == 0) {
            this.R = var12;
         }

         if (!this.Q) {
            this.R--;
         } else {
            this.R++;
         }
      } else {
         this.R = var12;
      }

      if (this.u_boolean()) {
         this.R = var12;
      }

      if (this.R > var7 + var9 - 3.0F) {
         this.R = (int)(var7 + var9 - 3.0F);
      }

      if (var12 > var7 + var9 - 3.0F) {
         var12 = (int)(var7 + var9 - 3.0F);
      }

      RenderUtil.drawRect(
         var7, var8 + var10 - 10.0F, var7 + var9 - 3.0F, var8 + var10 - 3.0F, client.getColorSetting().getValue().darker().darker().darker().darker().getRGB()
      );
      RenderUtil.drawRect(var7, var8 + var10 - 10.0F, this.x ? this.R : var12, var8 + var10 - 3.0F, client.getColorSetting().getValue().getRGB());
      if ((this.x ? this.R : var12) - 5 > var7) {
         RenderUtil.drawRect(
            (this.x ? this.R : var12) - 5,
            var8 + var10 - 10.0F,
            this.x ? this.R : var12,
            var8 + var10 - 3.0F,
            client.getColorSetting().getValue().darker().getRGB()
         );
      }

      var7 -= 30.0F;
      GuiInventory.drawEntityOnScreen((int)var7 + 15, (int)var8 + 46, 22, -178.0F, 0.0F, this.X);
   }

   public void z(EntityLivingBase var1) {
      this.X = var1;
   }

   public void y(float var1) {
      this.e = var1;
   }
}
