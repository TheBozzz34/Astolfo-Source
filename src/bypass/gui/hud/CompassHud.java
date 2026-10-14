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

   public CompassHud(float f, float g, int i, int j) {
      super("compass", f, g, i, j);
      this.F(true);
   }

   @Override
   public void l(int i) {
   }

   @Override
   public void render(int i, int j) {
      float g = this.M();
      int k = this.Z((int)mc.thePlayer.rotationYaw);
      this.r = this.q * k / 360;
      if (g == 1.0F) {
         this.s = this.x / 2.0F;
      } else {
         this.s = (int)g;
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
      float f = this.E_float();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color((this.u >> 16 & 0xFF) / 255.0F, (this.u >> 8 & 0xFF) / 255.0F, (this.u & 0xFF) / 255.0F, 1.0F);
      worldRenderer.begin(6, DefaultVertexFormats.POSITION);
      worldRenderer.pos(this.s, f + 3.0F, 0.0).endVertex();
      worldRenderer.pos(this.s + 3.0F, f, 0.0).endVertex();
      worldRenderer.pos(this.s - 3.0F, f, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   private void r(String string, int i, double d) {
      float g = this.E_float();
      int j = this.q * i / 360 - this.r;
      if (j > this.q / 2) {
         j -= this.q;
      }

      if (j < -this.q / 2) {
         j += this.q;
      }

      double e;
      if ((e = 1.0 - Math.abs(j) / (this.f / 2.0)) > 0.1) {
         int k = this.O & 16777215;
         int l = k | (int)(e * 255.0) << 24;
         float h = this.s + j - (int)(mc.fontRendererObj.getStringWidth(string) * d / 2.0);
         float f2 = g + this.Z / 2.0F - (int)(mc.fontRendererObj.FONT_HEIGHT * d / 2.0);
         GL11.glEnable(3042);
         GL11.glPushMatrix();
         GL11.glTranslated(-h * (d - 1.0), -f2 * (d - 1.0), 0.0);
         GL11.glScaled(d, d, 1.0);
         if (this.n) {
            mc.fontRendererObj.drawStringWithShadow(string, h, f2, l);
         } else {
            mc.fontRendererObj.D(string, h, f2, l);
         }

         GL11.glPopMatrix();
         GL11.glDisable(3042);
      }
   }

   private int Z(int i) {
      if (i > 360) {
         i %= 360;
      }

      while (i < 0) {
         i += 360;
      }

      return i;
   }

   public void i(int i) {
      this.x = i;
   }
}
