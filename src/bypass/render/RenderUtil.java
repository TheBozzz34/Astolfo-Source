package bypass.render;

import bypass.lua.ScriptName;
import bypass.module.visuals.HUD;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class RenderUtil {
   public static void drawRect(double d, double d2, double d3, double d4, int i) {
      begin2D();
      setColor(i);
      rectVertices(d, d2, d3, d4);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   @ScriptName(j = "drw")
   public static void j(double d, double d2, double d3, double d4, int i, int j, int k, int l) {
      begin2D();
      GL11.glColor4f(i / 255.0F, j / 255.0F, k / 255.0F, l / 255.0F);
      rectVertices(d, d2, d3, d4);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   public static void drawThinBorderedRect(float f, float g, float h, float f2, int i, int j) {
      begin2D();
      f *= 2.0F;
      h *= 2.0F;
      g *= 2.0F;
      f2 *= 2.0F;
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      drawVerticalLine(f, g, f2 - 1.0F, j);
      drawVerticalLine(h - 1.0F, g, f2, j);
      drawHorizontalLine(f, h - 1.0F, g, j);
      drawHorizontalLine(f, h - 2.0F, f2 - 1.0F, j);
      drawRect(f + 1.0F, g + 1.0F, h - 1.0F, f2 - 1.0F, i);
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      end2D();
   }

   public static void drawArrow(double d, double d2, double d3, boolean bl, int i) {
      begin2D();
      GL11.glEnable(2881);
      setColor(i);
      arrowVertices(d, d2, d3, bl);
      GL11.glDisable(2881);
      end2D();
   }

   public static void beginNoTexture() {
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.disableDepth();
      GL11.glEnable(2848);
   }

   public static void endNoTexture() {
      GlStateManager.enableTexture2D();
      GlStateManager.enableAlpha();
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2848);
   }

   public static void drawArc(double d, double d2, int i, int j, double d3, int k) {
      float f = (k >> 24 & 0xFF) / 255.0F;
      float g = (k >> 16 & 0xFF) / 255.0F;
      float h = (k >> 8 & 0xFF) / 255.0F;
      float f2 = (k & 0xFF) / 255.0F;
      begin2D();
      GlStateManager.color(g, h, f2, f);
      GlStateManager.translate(d, d2, 0.0);
      GlStateManager.rotate(j, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-d, -d2, 0.0);
      GL11.glBegin(3);

      for (int l = 0; l <= i; l++) {
         double e = Math.sin(l * Math.PI / 180.0) * d3;
         double d4 = Math.cos(l * Math.PI / 180.0) * d3;
         GL11.glVertex2d(d + e, d2 + d4);
      }

      GL11.glEnd();
      end2D();
   }

   public static void drawFilledArc(double d, double d2, int i, int j, double d3, int k) {
      float f = (k >> 24 & 0xFF) / 255.0F;
      float g = (k >> 16 & 0xFF) / 255.0F;
      float h = (k >> 8 & 0xFF) / 255.0F;
      float f2 = (k & 0xFF) / 255.0F;
      begin2D();
      GlStateManager.color(g, h, f2, f);
      GlStateManager.translate(d, d2, 0.0);
      GlStateManager.rotate(j, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-d, -d2, 0.0);
      GL11.glBegin(6);
      GL11.glVertex2d(d, d2);

      for (int l = 0; l <= i; l++) {
         double e = Math.sin(l * Math.PI / 180.0) * d3;
         double d4 = Math.cos(l * Math.PI / 180.0) * d3;
         GL11.glVertex2d(d + e, d2 + d4);
      }

      GL11.glEnd();
      end2D();
   }

   public static void drawCircle(double d, double d2, double d3, int i) {
      drawArc(d, d2, 360, 0, d3, i);
      drawFilledArc(d, d2, 360, 0, d3, i);
   }

   public static void arrowVertices(double d, double d2, double d3, boolean bl) {
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      d *= 2.0;
      d2 *= 2.0;
      d3 *= 2.0;
      GL11.glBegin(4);
      if (bl) {
         GL11.glVertex2d(d + d3, d2 + d3 / 2.0);
         GL11.glVertex2d(d, d2);
         GL11.glVertex2d(d, d2 + d3);
      } else {
         GL11.glVertex2d(d + d3, d2 - d3);
         GL11.glVertex2d(d, d2 - d3);
         GL11.glVertex2d(d + d3 / 2.0, d2);
      }

      GL11.glEnd();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
   }

   public static void drawBorderedRect(float f, float g, float h, float f2, float f3, int i, int j) {
      begin2D();
      setColor(i);
      rectVertices(f + f3, g + f3, h - f3, f2 - f3);
      setColor(j);
      rectVertices(f + f3, g, h - f3, g + f3);
      rectVertices(f, g, f + f3, f2);
      rectVertices(h - f3, g, h, f2);
      rectVertices(f + f3, f2 - f3, h - f3, f2);
      end2D();
   }

   public static void drawColorFan(float f, float g, int i) {
      begin2D();
      GL11.glBegin(6);
      GL11.glColor3f(0.0F, 0.0F, 1.0F);
      GL11.glVertex2f(f, g);

      for (int j : range(90, 185, 5)) {
         GL11.glColor3f(
            (float)Math.sin(Math.toRadians(j - 90)) * (float)Math.sqrt(2.0), (float)Math.cos(Math.toRadians(j - 90)) * (float)Math.sqrt(2.0), 0.0F
         );
         GL11.glVertex2f((int)(Math.cos(Math.toRadians(j)) * i), (int)(Math.sin(Math.toRadians(j)) * i));
      }

      GL11.glEnd();
      end2D();
   }

   public static int[] range(int i, int j, int k) {
      int l = (int)Math.ceil((double)(j - i) / k);
      int[] ints = new int[l];

      for (int m = 0; m < l; m++) {
         ints[m] = m * k + i;
      }

      return ints;
   }

   public static void drawVerticalLine(float f, float g, float h, int i) {
      if (h < g) {
         float f2 = g;
         g = h;
         h = f2;
      }

      drawRect(f, g + 1.0F, f + 1.0F, h, i);
   }

   public static void drawHorizontalLine(double d, double d2, double d3, int i) {
      if (d2 < d) {
         double e = d;
         d = d2;
         d2 = e;
      }

      drawRect(d, d3, d2 + 1.0, d3 + 1.0, i);
   }

   public static void drawGradientRect(float f, float g, float h, float f2, int i, int j) {
      float f3 = (i >> 24 & 0xFF) / 255.0F;
      float f4 = (i >> 16 & 0xFF) / 255.0F;
      float f5 = (i >> 8 & 0xFF) / 255.0F;
      float f6 = (i & 0xFF) / 255.0F;
      float f7 = (j >> 24 & 0xFF) / 255.0F;
      float f8 = (j >> 16 & 0xFF) / 255.0F;
      float f9 = (j >> 8 & 0xFF) / 255.0F;
      float f10 = (j & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(h, g, 0.0).color(f4, f5, f6, f3).endVertex();
      worldRenderer.pos(f, g, 0.0).color(f4, f5, f6, f3).endVertex();
      worldRenderer.pos(f, f2, 0.0).color(f8, f9, f10, f7).endVertex();
      worldRenderer.pos(h, f2, 0.0).color(f8, f9, f10, f7).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void drawFourColorRect(float f, float g, float h, float f2, int i, int j, int k, int l) {
      float f3 = (i >> 24 & 0xFF) / 255.0F;
      float f4 = (i >> 16 & 0xFF) / 255.0F;
      float f5 = (i >> 8 & 0xFF) / 255.0F;
      float f6 = (i & 0xFF) / 255.0F;
      float f7 = (k >> 24 & 0xFF) / 255.0F;
      float f8 = (k >> 16 & 0xFF) / 255.0F;
      float f9 = (k >> 8 & 0xFF) / 255.0F;
      float f10 = (k & 0xFF) / 255.0F;
      float f11 = (j >> 24 & 0xFF) / 255.0F;
      float f12 = (j >> 16 & 0xFF) / 255.0F;
      float f13 = (j >> 8 & 0xFF) / 255.0F;
      float f14 = (j & 0xFF) / 255.0F;
      float f15 = (l >> 24 & 0xFF) / 255.0F;
      float f16 = (l >> 16 & 0xFF) / 255.0F;
      float f17 = (l >> 8 & 0xFF) / 255.0F;
      float f18 = (l & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(h, g, 0.0).color(f12, f13, f14, f11).endVertex();
      worldRenderer.pos(f, g, 0.0).color(f4, f5, f6, f3).endVertex();
      worldRenderer.pos(f, f2, 0.0).color(f8, f9, f10, f7).endVertex();
      worldRenderer.pos(h, f2, 0.0).color(f16, f17, f18, f15).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void drawRectOutline(double d, double d2, double d3, double d4, int i) {
      begin2D();
      setColor(i);
      rectOutlineVertices(d, d2, d3, d4);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   public static void rectVertices(double d, double d2, double d3, double d4) {
      a(d, d2, d3, d4, 7);
   }

   public static void rectOutlineVertices(double d, double d2, double d3, double d4) {
      a(d, d2, d3, d4, 2);
   }

   private static void a(double d, double d2, double d3, double d4, int i) {
      GL11.glBegin(i);
      GL11.glVertex2d(d, d4);
      GL11.glVertex2d(d3, d4);
      GL11.glVertex2d(d3, d2);
      GL11.glVertex2d(d, d2);
      GL11.glEnd();
   }

   public static void begin2D() {
      GL11.glPushMatrix();
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glDepthMask(true);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glHint(3155, 4354);
   }

   public static void end2D() {
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDisable(2848);
      GL11.glHint(3154, 4352);
      GL11.glHint(3155, 4352);
      GL11.glPopMatrix();
   }

   public static void drawTexturedRect(float f, float g, float h, float f2, float f3, float f4, float f5, float f6) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(4, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(f + h, g, 0.0).tex(f5, f4).endVertex();
      worldRenderer.pos(f, g, 0.0).tex(f3, f4).endVertex();
      worldRenderer.pos(f, g + f2, 0.0).tex(f3, f6).endVertex();
      worldRenderer.pos(f, g + f2, 0.0).tex(f3, f6).endVertex();
      worldRenderer.pos(f + h, g + f2, 0.0).tex(f5, f6).endVertex();
      worldRenderer.pos(f + h, g, 0.0).tex(f5, f4).endVertex();
      tessellator.draw();
   }

   public static void drawLine(float f, float g, float h, float f2, float f3, int i) {
      begin2D();
      setColor(i);
      lineVertices(f, g, h, f2, f3);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   public static void c(float f, float g, float h, float f2, float f3, int i) {
      drawLine(f, g, f2, g + h, f2, i);
      drawLine(f, g, f2 + f3, g + h, f2 + f3, i);
      drawLine(f, g, f2 - 0.5F, g, f2 + f3 + 1.0F, i);
      drawLine(f, g + h, f2 - 0.5F, g + h, f2 + f3 + 1.0F, i);
   }

   public static void O(float f, float g, float h, float f2, float f3, int i) {
      drawLine(f, g, f2, g + h, f2, i);
      drawLine(f, g, f2 + f3, g + h, f2 + f3, i);
      drawLine(f, g, f2, g, f2 + f3 + 0.5F, i);
      drawLine(f, g + h, f2, g + h, f2 + f3, i);
   }

   public static void lineVertices(float f, float g, float h, float f2, float f3) {
      GL11.glLineWidth(f);
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(1, DefaultVertexFormats.POSITION);
      worldRenderer.pos(g, h, 0.0).endVertex();
      worldRenderer.pos(f2, f3, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
   }

   public static void setColor(int i) {
      E(i, i, i, i);
   }

   public static void l(int i, int j, int k, int l) {
      E(i, j, k, l);
   }

   private static void E(int i, int j, int k, int l) {
      float f = (i >> 16 & 0xFF) / 255.0F;
      float g = (j >> 8 & 0xFF) / 255.0F;
      float h = (k & 0xFF) / 255.0F;
      float f2 = (l >> 24 & 0xFF) / 255.0F;
      GL11.glColor4f(f, g, h, f2);
   }

   public static void drawTriangle(double d, double d2, double d3, double d4, double d5, double d6, int i) {
      float f = (i >> 16 & 0xFF) / 255.0F;
      float g = (i >> 8 & 0xFF) / 255.0F;
      float h = (i & 0xFF) / 255.0F;
      float f2 = (i >> 24 & 0xFF) / 255.0F;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.disableCull();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(f, g, h, f2);
      GL11.glBegin(6);
      if ((!(d2 > d4) || !(d < d5)) && (!(d2 < d4) || !(d > d5))) {
         GL11.glVertex2d(d, d2);
         GL11.glVertex2d(d3, d4);
         GL11.glVertex2d(d5, d6);
      } else {
         GL11.glVertex2d(d5, d6);
         GL11.glVertex2d(d3, d4);
         GL11.glVertex2d(d, d2);
      }

      GL11.glEnd();
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   public static void drawBorderedGradientRect(float f, float g, float h, float f2, float f3, int i, int j, int k) {
      begin2D();
      drawGradientRect(f, g, h, f2, k, j);
      setColor(i);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(f3);
      GL11.glBegin(3);
      GL11.glVertex2f(f, g);
      GL11.glVertex2f(f, f2);
      GL11.glVertex2f(h, f2);
      GL11.glVertex2f(h, g);
      GL11.glVertex2f(f, g);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      end2D();
   }

   public static void drawVerticalGradient(double d, double d2, double d3, double d4, int i, int j) {
      begin2D();
      GL11.glShadeModel(7425);
      GL11.glBegin(7);
      setColor(i);
      GL11.glVertex2d(d, d4);
      GL11.glVertex2d(d3, d4);
      setColor(j);
      GL11.glVertex2d(d3, d2);
      GL11.glVertex2d(d, d2);
      GL11.glEnd();
      GL11.glShadeModel(7424);
      end2D();
   }

   public static void drawHorizontalGradient(double d, double d2, double d3, double d4, int i, int j) {
      begin2D();
      GL11.glShadeModel(7425);
      GL11.glBegin(7);
      GL11.glVertex2d(d, d4);
      setColor(j);
      GL11.glVertex2d(d3, d4);
      GL11.glVertex2d(d3, d2);
      setColor(i);
      GL11.glVertex2d(d, d2);
      GL11.glEnd();
      GL11.glShadeModel(7424);
      end2D();
   }

   public static void drawWorldCircle(double d, double d2, double d3, double d4, float f, Color color) {
      GlStateManager.pushMatrix();
      GL11.glLineWidth(f);
      GL11.glBegin(2);
      double e = Math.tan(Math.PI / 180.0);
      double d5 = Math.cos(Math.PI / 180.0);
      double d6 = d4;
      double d7 = 0.0;

      for (int i = 0; i <= 360.0; i++) {
         setColor(color.getRGB());
         GL11.glVertex3d(d + d6, d2, d3 + d7);
         double d8 = -d7;
         double d9 = d6;
         d6 += d8 * e;
         d7 += d9 * e;
         d6 *= d5;
         d7 *= d5;
      }

      GL11.glEnd();
      GlStateManager.popMatrix();
   }

   public static float getScaledWidth() {
      return Display.getWidth() / HUD.hudScale.getValue();
   }

   public static float getScaledHeight() {
      return Display.getHeight() / HUD.hudScale.getValue();
   }
}
