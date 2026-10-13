package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class CompassHud extends Component {
   private int x;
   public int L = 2;
   public int f = 150;
   public int Z = 20;
   public int q = 500;
   public boolean t = true;
   public boolean R;
   public boolean n = true;
   public int j;
   public int N;
   private int r;
   private float s;
   private int u;
   private int O;
   private static final long c = 40005545770084L;

   public CompassHud(float var1, float var2, int var3, int var4) {
      super("compass", var1, var2, var3, var4);
      this.F(true);
   }

   @Override
   public void l(int var1) {
   }

   @Override
   public void render(int var1, int var2) {
      float var7 = this.M();
      int var8 = this.Z((int)mc.thePlayer.rotationYaw);
      this.r = this.q * var8 / 360;
      if (var7 == 1.0F) {
         this.s = this.x / 2.0F;
      } else {
         this.s = (int)var7;
      }

      if (this.t) {
         RenderUtil.drawRect(this.s - this.f / 2.0F, this.E_float(), this.s + this.f / 2.0F, this.E_float() + this.Z, -988737263);
      }

      if (!this.R) {
         this.u = this.j != 0 ? Color.HSBtoRGB(this.j / 100.0F, 1.0F, 1.0F) : -1;
         this.O = this.N != 0 ? Color.HSBtoRGB(this.N / 100.0F, 1.0F, 1.0F) : -1;
      } else {
         this.O = this.u = Color.HSBtoRGB((float)(System.currentTimeMillis() % 3000L) / 3000.0F, 1.0F, 1.0F);
      }

      this.B();
      if (this.L >= 0) {
         this.r("S", 0, 1.5);
         this.r("W", 90, 1.5);
         this.r("N", 180, 1.5);
         this.r("E", 270, 1.5);
      }

      if (this.L >= 1) {
         this.r("SW", 45, 1.0);
         this.r("NW", 135, 1.0);
         this.r("NE", 225, 1.0);
         this.r("SE", 315, 1.0);
      }

      if (this.L >= 2) {
         this.r("15", 15, 0.75);
         this.r("30", 30, 0.75);
         this.r("60", 60, 0.75);
         this.r("75", 75, 0.75);
         this.r("105", 105, 0.75);
         this.r("120", 120, 0.75);
         this.r("150", 150, 0.75);
         this.r("165", 165, 0.75);
         this.r("195", 195, 0.75);
         this.r("210", 210, 0.75);
         this.r("240", 240, 0.75);
         this.r("255", 255, 0.75);
         this.r("285", 285, 0.75);
         this.r("300", 300, 0.75);
         this.r("330", 330, 0.75);
         this.r("345", 345, 0.75);
      }
   }

   private void B() {
      float var3 = this.E_float();
      Tessellator var4 = Tessellator.getInstance();
      WorldRenderer var5 = var4.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color((this.u >> 16 & 0xFF) / 255.0F, (this.u >> 8 & 0xFF) / 255.0F, (this.u & 0xFF) / 255.0F, 1.0F);
      var5.begin(6, DefaultVertexFormats.POSITION);
      var5.pos(this.s, var3 + 3.0F, 0.0).endVertex();
      var5.pos(this.s + 3.0F, var3, 0.0).endVertex();
      var5.pos(this.s - 3.0F, var3, 0.0).endVertex();
      var4.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   private void r(String var1, int var2, double var3) {
      float var8 = this.E_float();
      int var11 = this.q * var2 / 360 - this.r;
      if (var11 > this.q / 2) {
         var11 -= this.q;
      }

      if (var11 < -this.q / 2) {
         var11 += this.q;
      }

      double var9;
      if ((var9 = 1.0 - Math.abs(var11) / (this.f / 2.0)) > 0.1) {
         int var12 = this.O & 16777215;
         int var13 = var12 | (int)(var9 * 255.0) << 24;
         float var14 = this.s + var11 - (int)(mc.fontRendererObj.getStringWidth(var1) * var3 / 2.0);
         float var15 = var8 + this.Z / 2.0F - (int)(mc.fontRendererObj.FONT_HEIGHT * var3 / 2.0);
         GL11.glEnable(3042);
         GL11.glPushMatrix();
         GL11.glTranslated(-var14 * (var3 - 1.0), -var15 * (var3 - 1.0), 0.0);
         GL11.glScaled(var3, var3, 1.0);
         if (this.n) {
            mc.fontRendererObj.drawStringWithShadow(var1, var14, var15, var13);
         } else {
            mc.fontRendererObj.D(var1, var14, var15, var13);
         }

         GL11.glPopMatrix();
         GL11.glDisable(3042);
      }
   }

   private int Z(int var1) {
      if (var1 > 360) {
         var1 %= 360;
      }

      while (var1 < 0) {
         var1 += 360;
      }

      return var1;
   }

   public void i(int var1) {
      this.x = var1;
   }
}
