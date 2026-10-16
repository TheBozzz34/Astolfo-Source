package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.render.RenderUtil;
import bypass.util.MathUtil;
import java.awt.Color;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class TargetHud extends Component {
   private EntityLivingBase target;
   private float screenCenterX;
   private int animatedBarEnd;
   private boolean animatingBar;
   private boolean healthIncreasing;
   private final int backgroundColor = new Color(17, 17, 17, 197).getRGB();

   public TargetHud(float f, float g, int i, int j) {
      super("targethud", f, g, i, j);
      this.setDraggable(true);
   }

   @Override
   public void onClick(int i) {
   }

   @Override
   public void render(int i, int j) {
      float f = this.getX();
      float g = this.getYPos();
      float h = this.getWidth();
      float f2 = this.getHeight();
      if (f == 1.0F) {
         float f3 = this.screenCenterX - h / 2.0F;
         f = f3;
         this.setX(f3);
      }

      RenderUtil.drawRect(f, g, f + h, g + f2, this.backgroundColor);
      f += 30.0F;
      h -= 30.0F;
      mc.fontRendererObj.drawStringWithShadow(this.target.getName(), f, g + 5.0F, -1);
      String string = MathUtil.ONE_DECIMAL.format(this.target.getHealth() / 2.0F) + " ❤";
      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      mc.fontRendererObj.drawStringWithShadow(string, f / 2.0F, (g + 18.0F) / 2.0F, client.getColorSetting().getValue().getRGB());
      GL11.glPopMatrix();
      int k = (int)(f + (h - 3.0F) * (this.target.getHealth() / this.target.getMaxHealth() * 100.0F * 0.01F));
      if (k > f + h) {
         k = (int)(f + h - 3.0F);
      }

      int l = k - this.animatedBarEnd;
      if (Math.abs(l) > 1) {
         this.healthIncreasing = l >= 0;
         this.animatingBar = true;
      }

      if (this.animatedBarEnd < k && !this.healthIncreasing || this.animatedBarEnd > k && this.healthIncreasing) {
         this.animatingBar = false;
      }

      if (this.animatingBar) {
         if (this.animatedBarEnd == 0) {
            this.animatedBarEnd = k;
         }

         if (!this.healthIncreasing) {
            this.animatedBarEnd--;
         } else {
            this.animatedBarEnd++;
         }
      } else {
         this.animatedBarEnd = k;
      }

      if (this.isDragging()) {
         this.animatedBarEnd = k;
      }

      if (this.animatedBarEnd > f + h - 3.0F) {
         this.animatedBarEnd = (int)(f + h - 3.0F);
      }

      if (k > f + h - 3.0F) {
         k = (int)(f + h - 3.0F);
      }

      RenderUtil.drawRect(
         f, g + f2 - 10.0F, f + h - 3.0F, g + f2 - 3.0F, client.getColorSetting().getValue().darker().darker().darker().darker().getRGB()
      );
      RenderUtil.drawRect(
         f, g + f2 - 10.0F, this.animatingBar ? this.animatedBarEnd : k, g + f2 - 3.0F, client.getColorSetting().getValue().getRGB()
      );
      if ((this.animatingBar ? this.animatedBarEnd : k) - 5 > f) {
         RenderUtil.drawRect(
            (this.animatingBar ? this.animatedBarEnd : k) - 5,
            g + f2 - 10.0F,
            this.animatingBar ? this.animatedBarEnd : k,
            g + f2 - 3.0F,
            client.getColorSetting().getValue().darker().getRGB()
         );
      }

      f -= 30.0F;
      GuiInventory.drawEntityOnScreen((int)f + 15, (int)g + 46, 22, -178.0F, 0.0F, this.target);
   }

   public void setTarget(EntityLivingBase entityLivingBase) {
      this.target = entityLivingBase;
   }

   public void setScreenCenterX(float f) {
      this.screenCenterX = f;
   }
}
