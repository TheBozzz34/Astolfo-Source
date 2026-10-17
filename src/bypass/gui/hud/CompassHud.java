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
   private int screenWidth;
   public int detailLevel = 2;
   public int visibleWidth = 150;
   public int barHeight = 20;
   public int fullCircleWidth = 500;
   public boolean drawBackground = true;
   public boolean rainbow;
   public boolean textShadow = true;
   public int markerHue;
   public int textHue;
   private int yawOffset;
   private float centerX;
   private int markerColor;
   private int textColor;

   public CompassHud(float f, float g, int i, int j) {
      super("compass", f, g, i, j);
      this.setDraggable(true);
   }

   @Override
   public void onClick(int i) {
   }

   @Override
   public void render(int i, int j) {
      float f = this.getX();
      int k = this.wrapDegrees((int)mc.thePlayer.rotationYaw);
      this.yawOffset = this.fullCircleWidth * k / 360;
      if (f == 1.0F) {
         this.centerX = this.screenWidth / 2.0F;
      } else {
         this.centerX = (int)f;
      }

      if (this.drawBackground) {
         RenderUtil.drawRect(
            this.centerX - this.visibleWidth / 2.0F, this.getYPos(), this.centerX + this.visibleWidth / 2.0F, this.getYPos() + this.barHeight, -988737263
         );
      }

      if (!this.rainbow) {
         this.markerColor = this.markerHue != 0 ? Color.HSBtoRGB(this.markerHue / 100.0F, 1.0F, 1.0F) : -1;
         this.textColor = this.textHue != 0 ? Color.HSBtoRGB(this.textHue / 100.0F, 1.0F, 1.0F) : -1;
      } else {
         this.textColor = this.markerColor = Color.HSBtoRGB((float)(System.currentTimeMillis() % 3000L) / 3000.0F, 1.0F, 1.0F);
      }

      this.drawCenterMarker();
      if (this.detailLevel >= 0) {
         this.drawDirectionLabel("S", 0, 1.5);
         this.drawDirectionLabel("W", 90, 1.5);
         this.drawDirectionLabel("N", 180, 1.5);
         this.drawDirectionLabel("E", 270, 1.5);
      }

      if (this.detailLevel >= 1) {
         this.drawDirectionLabel("SW", 45, 1.0);
         this.drawDirectionLabel("NW", 135, 1.0);
         this.drawDirectionLabel("NE", 225, 1.0);
         this.drawDirectionLabel("SE", 315, 1.0);
      }

      if (this.detailLevel >= 2) {
         this.drawDirectionLabel("15", 15, 0.75);
         this.drawDirectionLabel("30", 30, 0.75);
         this.drawDirectionLabel("60", 60, 0.75);
         this.drawDirectionLabel("75", 75, 0.75);
         this.drawDirectionLabel("105", 105, 0.75);
         this.drawDirectionLabel("120", 120, 0.75);
         this.drawDirectionLabel("150", 150, 0.75);
         this.drawDirectionLabel("165", 165, 0.75);
         this.drawDirectionLabel("195", 195, 0.75);
         this.drawDirectionLabel("210", 210, 0.75);
         this.drawDirectionLabel("240", 240, 0.75);
         this.drawDirectionLabel("255", 255, 0.75);
         this.drawDirectionLabel("285", 285, 0.75);
         this.drawDirectionLabel("300", 300, 0.75);
         this.drawDirectionLabel("330", 330, 0.75);
         this.drawDirectionLabel("345", 345, 0.75);
      }
   }

   private void drawCenterMarker() {
      float f = this.getYPos();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color((this.markerColor >> 16 & 0xFF) / 255.0F, (this.markerColor >> 8 & 0xFF) / 255.0F, (this.markerColor & 0xFF) / 255.0F, 1.0F);
      worldRenderer.begin(6, DefaultVertexFormats.POSITION);
      worldRenderer.pos(this.centerX, f + 3.0F, 0.0).endVertex();
      worldRenderer.pos(this.centerX + 3.0F, f, 0.0).endVertex();
      worldRenderer.pos(this.centerX - 3.0F, f, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   private void drawDirectionLabel(String string, int i, double d) {
      float f = this.getYPos();
      int j = this.fullCircleWidth * i / 360 - this.yawOffset;
      if (j > this.fullCircleWidth / 2) {
         j -= this.fullCircleWidth;
      }

      if (j < -this.fullCircleWidth / 2) {
         j += this.fullCircleWidth;
      }

      double e;
      if ((e = 1.0 - Math.abs(j) / (this.visibleWidth / 2.0)) > 0.1) {
         int k = this.textColor & 16777215;
         int l = k | (int)(e * 255.0) << 24;
         float g = this.centerX + j - (int)(mc.fontRendererObj.getStringWidth(string) * d / 2.0);
         float h = f + this.barHeight / 2.0F - (int)(mc.fontRendererObj.FONT_HEIGHT * d / 2.0);
         GL11.glEnable(3042);
         GL11.glPushMatrix();
         GL11.glTranslated(-g * (d - 1.0), -h * (d - 1.0), 0.0);
         GL11.glScaled(d, d, 1.0);
         if (this.textShadow) {
            mc.fontRendererObj.drawStringWithShadow(string, g, h, l);
         } else {
            mc.fontRendererObj.drawStringNoShadow(string, g, h, l);
         }

         GL11.glPopMatrix();
         GL11.glDisable(3042);
      }
   }

   private int wrapDegrees(int i) {
      if (i > 360) {
         i %= 360;
      }

      while (i < 0) {
         i += 360;
      }

      return i;
   }

   public void setScreenWidth(int i) {
      this.screenWidth = i;
   }
}
