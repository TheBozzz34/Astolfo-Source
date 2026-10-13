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
   private static final long a = 64989882639786L;

   public static void drawRect(double var0, double var2, double var4, double var6, int var8) {
      begin2D();
      setColor(var8);
      rectVertices(var0, var2, var4, var6);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   @ScriptName(j = "drw")
   public static void j(double var0, double var2, double var4, double var6, int var8, int var9, int var10, int var11) {
      begin2D();
      GL11.glColor4f(var8 / 255.0F, var9 / 255.0F, var10 / 255.0F, var11 / 255.0F);
      rectVertices(var0, var2, var4, var6);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   public static void drawThinBorderedRect(float var0, float var1, float var2, float var3, int var4, int var5) {
      begin2D();
      var0 *= 2.0F;
      var2 *= 2.0F;
      var1 *= 2.0F;
      var3 *= 2.0F;
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      drawVerticalLine(var0, var1, var3 - 1.0F, var5);
      drawVerticalLine(var2 - 1.0F, var1, var3, var5);
      drawHorizontalLine(var0, var2 - 1.0F, var1, var5);
      drawHorizontalLine(var0, var2 - 2.0F, var3 - 1.0F, var5);
      drawRect(var0 + 1.0F, var1 + 1.0F, var2 - 1.0F, var3 - 1.0F, var4);
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      end2D();
   }

   public static void drawArrow(double var0, double var2, double var4, boolean var6, int var7) {
      begin2D();
      GL11.glEnable(2881);
      setColor(var7);
      arrowVertices(var0, var2, var4, var6);
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

   public static void drawArc(double var0, double var2, int var4, int var5, double var6, int var8) {
      float var12 = (var8 >> 24 & 0xFF) / 255.0F;
      float var13 = (var8 >> 16 & 0xFF) / 255.0F;
      float var14 = (var8 >> 8 & 0xFF) / 255.0F;
      float var15 = (var8 & 0xFF) / 255.0F;
      begin2D();
      GlStateManager.color(var13, var14, var15, var12);
      GlStateManager.translate(var0, var2, 0.0);
      GlStateManager.rotate(var5, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-var0, -var2, 0.0);
      GL11.glBegin(3);

      for (int var16 = 0; var16 <= var4; var16++) {
         double var17 = Math.sin(var16 * Math.PI / 180.0) * var6;
         double var19 = Math.cos(var16 * Math.PI / 180.0) * var6;
         GL11.glVertex2d(var0 + var17, var2 + var19);
      }

      GL11.glEnd();
      end2D();
   }

   public static void drawFilledArc(double var0, double var2, int var4, int var5, double var6, int var8) {
      float var12 = (var8 >> 24 & 0xFF) / 255.0F;
      float var13 = (var8 >> 16 & 0xFF) / 255.0F;
      float var14 = (var8 >> 8 & 0xFF) / 255.0F;
      float var15 = (var8 & 0xFF) / 255.0F;
      begin2D();
      GlStateManager.color(var13, var14, var15, var12);
      GlStateManager.translate(var0, var2, 0.0);
      GlStateManager.rotate(var5, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-var0, -var2, 0.0);
      GL11.glBegin(6);
      GL11.glVertex2d(var0, var2);

      for (int var16 = 0; var16 <= var4; var16++) {
         double var17 = Math.sin(var16 * Math.PI / 180.0) * var6;
         double var19 = Math.cos(var16 * Math.PI / 180.0) * var6;
         GL11.glVertex2d(var0 + var17, var2 + var19);
      }

      GL11.glEnd();
      end2D();
   }

   public static void drawCircle(double var0, double var2, double var4, int var6) {
      drawArc(var0, var2, 360, 0, var4, var6);
      drawFilledArc(var0, var2, 360, 0, var4, var6);
   }

   public static void arrowVertices(double var0, double var2, double var4, boolean var6) {
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      var0 *= 2.0;
      var2 *= 2.0;
      var4 *= 2.0;
      GL11.glBegin(4);
      if (var6) {
         GL11.glVertex2d(var0 + var4, var2 + var4 / 2.0);
         GL11.glVertex2d(var0, var2);
         GL11.glVertex2d(var0, var2 + var4);
      } else {
         GL11.glVertex2d(var0 + var4, var2 - var4);
         GL11.glVertex2d(var0, var2 - var4);
         GL11.glVertex2d(var0 + var4 / 2.0, var2);
      }

      GL11.glEnd();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
   }

   public static void drawBorderedRect(float var0, float var1, float var2, float var3, float var4, int var5, int var6) {
      begin2D();
      setColor(var5);
      rectVertices(var0 + var4, var1 + var4, var2 - var4, var3 - var4);
      setColor(var6);
      rectVertices(var0 + var4, var1, var2 - var4, var1 + var4);
      rectVertices(var0, var1, var0 + var4, var3);
      rectVertices(var2 - var4, var1, var2, var3);
      rectVertices(var0 + var4, var3 - var4, var2 - var4, var3);
      end2D();
   }

   public static void drawColorFan(float var0, float var1, int var2) {
      begin2D();
      GL11.glBegin(6);
      GL11.glColor3f(0.0F, 0.0F, 1.0F);
      GL11.glVertex2f(var0, var1);

      for (int var9 : range(90, 185, 5)) {
         GL11.glColor3f(
            (float)Math.sin(Math.toRadians(var9 - 90)) * (float)Math.sqrt(2.0), (float)Math.cos(Math.toRadians(var9 - 90)) * (float)Math.sqrt(2.0), 0.0F
         );
         GL11.glVertex2f((int)(Math.cos(Math.toRadians(var9)) * var2), (int)(Math.sin(Math.toRadians(var9)) * var2));
      }

      GL11.glEnd();
      end2D();
   }

   public static int[] range(int var0, int var1, int var2) {
      int var6 = (int)Math.ceil((double)(var1 - var0) / var2);
      int[] var7 = new int[var6];

      for (int var8 = 0; var8 < var6; var8++) {
         var7[var8] = var8 * var2 + var0;
      }

      return var7;
   }

   public static void drawVerticalLine(float var0, float var1, float var2, int var3) {
      if (var2 < var1) {
         float var7 = var1;
         var1 = var2;
         var2 = var7;
      }

      drawRect(var0, var1 + 1.0F, var0 + 1.0F, var2, var3);
   }

   public static void drawHorizontalLine(double var0, double var2, double var4, int var6) {
      if (var2 < var0) {
         double var10 = var0;
         var0 = var2;
         var2 = var10;
      }

      drawRect(var0, var4, var2 + 1.0, var4 + 1.0, var6);
   }

   public static void drawGradientRect(float var0, float var1, float var2, float var3, int var4, int var5) {
      float var9 = (var4 >> 24 & 0xFF) / 255.0F;
      float var10 = (var4 >> 16 & 0xFF) / 255.0F;
      float var11 = (var4 >> 8 & 0xFF) / 255.0F;
      float var12 = (var4 & 0xFF) / 255.0F;
      float var13 = (var5 >> 24 & 0xFF) / 255.0F;
      float var14 = (var5 >> 16 & 0xFF) / 255.0F;
      float var15 = (var5 >> 8 & 0xFF) / 255.0F;
      float var16 = (var5 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator var17 = Tessellator.getInstance();
      WorldRenderer var18 = var17.getWorldRenderer();
      var18.begin(7, DefaultVertexFormats.POSITION_COLOR);
      var18.pos(var2, var1, 0.0).color(var10, var11, var12, var9).endVertex();
      var18.pos(var0, var1, 0.0).color(var10, var11, var12, var9).endVertex();
      var18.pos(var0, var3, 0.0).color(var14, var15, var16, var13).endVertex();
      var18.pos(var2, var3, 0.0).color(var14, var15, var16, var13).endVertex();
      var17.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void drawFourColorRect(float var0, float var1, float var2, float var3, int var4, int var5, int var6, int var7) {
      float var12 = (var4 >> 24 & 0xFF) / 255.0F;
      float var13 = (var4 >> 16 & 0xFF) / 255.0F;
      float var14 = (var4 >> 8 & 0xFF) / 255.0F;
      float var15 = (var4 & 0xFF) / 255.0F;
      float var16 = (var6 >> 24 & 0xFF) / 255.0F;
      float var17 = (var6 >> 16 & 0xFF) / 255.0F;
      float var18 = (var6 >> 8 & 0xFF) / 255.0F;
      float var19 = (var6 & 0xFF) / 255.0F;
      float var20 = (var5 >> 24 & 0xFF) / 255.0F;
      float var21 = (var5 >> 16 & 0xFF) / 255.0F;
      float var22 = (var5 >> 8 & 0xFF) / 255.0F;
      float var23 = (var5 & 0xFF) / 255.0F;
      float var24 = (var7 >> 24 & 0xFF) / 255.0F;
      float var25 = (var7 >> 16 & 0xFF) / 255.0F;
      float var26 = (var7 >> 8 & 0xFF) / 255.0F;
      float var27 = (var7 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator var28 = Tessellator.getInstance();
      WorldRenderer var29 = var28.getWorldRenderer();
      var29.begin(7, DefaultVertexFormats.POSITION_COLOR);
      var29.pos(var2, var1, 0.0).color(var21, var22, var23, var20).endVertex();
      var29.pos(var0, var1, 0.0).color(var13, var14, var15, var12).endVertex();
      var29.pos(var0, var3, 0.0).color(var17, var18, var19, var16).endVertex();
      var29.pos(var2, var3, 0.0).color(var25, var26, var27, var24).endVertex();
      var28.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void drawRectOutline(double var0, double var2, double var4, double var6, int var8) {
      begin2D();
      setColor(var8);
      rectOutlineVertices(var0, var2, var4, var6);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   public static void rectVertices(double var0, double var2, double var4, double var6) {
      a(var0, var2, var4, var6, 7);
   }

   public static void rectOutlineVertices(double var0, double var2, double var4, double var6) {
      a(var0, var2, var4, var6, 2);
   }

   private static void a(double var0, double var2, double var4, double var6, int var8) {
      GL11.glBegin(var8);
      GL11.glVertex2d(var0, var6);
      GL11.glVertex2d(var4, var6);
      GL11.glVertex2d(var4, var2);
      GL11.glVertex2d(var0, var2);
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

   public static void drawTexturedRect(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      Tessellator var8 = Tessellator.getInstance();
      WorldRenderer var9 = var8.getWorldRenderer();
      var9.begin(4, DefaultVertexFormats.POSITION_TEX);
      var9.pos(var0 + var2, var1, 0.0).tex(var6, var5).endVertex();
      var9.pos(var0, var1, 0.0).tex(var4, var5).endVertex();
      var9.pos(var0, var1 + var3, 0.0).tex(var4, var7).endVertex();
      var9.pos(var0, var1 + var3, 0.0).tex(var4, var7).endVertex();
      var9.pos(var0 + var2, var1 + var3, 0.0).tex(var6, var7).endVertex();
      var9.pos(var0 + var2, var1, 0.0).tex(var6, var5).endVertex();
      var8.draw();
   }

   public static void drawLine(float var0, float var1, float var2, float var3, float var4, int var5) {
      begin2D();
      setColor(var5);
      lineVertices(var0, var1, var2, var3, var4);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      end2D();
   }

   public static void c(float var0, float var1, float var2, float var3, float var4, int var5) {
      drawLine(var0, var1, var3, var1 + var2, var3, var5);
      drawLine(var0, var1, var3 + var4, var1 + var2, var3 + var4, var5);
      drawLine(var0, var1, var3 - 0.5F, var1, var3 + var4 + 1.0F, var5);
      drawLine(var0, var1 + var2, var3 - 0.5F, var1 + var2, var3 + var4 + 1.0F, var5);
   }

   public static void O(float var0, float var1, float var2, float var3, float var4, int var5) {
      drawLine(var0, var1, var3, var1 + var2, var3, var5);
      drawLine(var0, var1, var3 + var4, var1 + var2, var3 + var4, var5);
      drawLine(var0, var1, var3, var1, var3 + var4 + 0.5F, var5);
      drawLine(var0, var1 + var2, var3, var1 + var2, var3 + var4, var5);
   }

   public static void lineVertices(float var0, float var1, float var2, float var3, float var4) {
      GL11.glLineWidth(var0);
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      Tessellator var7 = Tessellator.getInstance();
      WorldRenderer var8 = var7.getWorldRenderer();
      var8.begin(1, DefaultVertexFormats.POSITION);
      var8.pos(var1, var2, 0.0).endVertex();
      var8.pos(var3, var4, 0.0).endVertex();
      var7.draw();
      GlStateManager.enableTexture2D();
   }

   public static void setColor(int var0) {
      E(var0, var0, var0, var0);
   }

   public static void l(int var0, int var1, int var2, int var3) {
      E(var0, var1, var2, var3);
   }

   private static void E(int var0, int var1, int var2, int var3) {
      float var6 = (var0 >> 16 & 0xFF) / 255.0F;
      float var7 = (var1 >> 8 & 0xFF) / 255.0F;
      float var8 = (var2 & 0xFF) / 255.0F;
      float var9 = (var3 >> 24 & 0xFF) / 255.0F;
      GL11.glColor4f(var6, var7, var8, var9);
   }

   public static void drawTriangle(double var0, double var2, double var4, double var6, double var8, double var10, int var12) {
      float var16 = (var12 >> 16 & 0xFF) / 255.0F;
      float var17 = (var12 >> 8 & 0xFF) / 255.0F;
      float var18 = (var12 & 0xFF) / 255.0F;
      float var19 = (var12 >> 24 & 0xFF) / 255.0F;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.disableCull();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(var16, var17, var18, var19);
      GL11.glBegin(6);
      if ((!(var2 > var6) || !(var0 < var8)) && (!(var2 < var6) || !(var0 > var8))) {
         GL11.glVertex2d(var0, var2);
         GL11.glVertex2d(var4, var6);
         GL11.glVertex2d(var8, var10);
      } else {
         GL11.glVertex2d(var8, var10);
         GL11.glVertex2d(var4, var6);
         GL11.glVertex2d(var0, var2);
      }

      GL11.glEnd();
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   public static void drawBorderedGradientRect(float var0, float var1, float var2, float var3, float var4, int var5, int var6, int var7) {
      begin2D();
      drawGradientRect(var0, var1, var2, var3, var7, var6);
      setColor(var5);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(var4);
      GL11.glBegin(3);
      GL11.glVertex2f(var0, var1);
      GL11.glVertex2f(var0, var3);
      GL11.glVertex2f(var2, var3);
      GL11.glVertex2f(var2, var1);
      GL11.glVertex2f(var0, var1);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      end2D();
   }

   public static void drawVerticalGradient(double var0, double var2, double var4, double var6, int var8, int var9) {
      begin2D();
      GL11.glShadeModel(7425);
      GL11.glBegin(7);
      setColor(var8);
      GL11.glVertex2d(var0, var6);
      GL11.glVertex2d(var4, var6);
      setColor(var9);
      GL11.glVertex2d(var4, var2);
      GL11.glVertex2d(var0, var2);
      GL11.glEnd();
      GL11.glShadeModel(7424);
      end2D();
   }

   public static void drawHorizontalGradient(double var0, double var2, double var4, double var6, int var8, int var9) {
      begin2D();
      GL11.glShadeModel(7425);
      GL11.glBegin(7);
      GL11.glVertex2d(var0, var6);
      setColor(var9);
      GL11.glVertex2d(var4, var6);
      GL11.glVertex2d(var4, var2);
      setColor(var8);
      GL11.glVertex2d(var0, var2);
      GL11.glEnd();
      GL11.glShadeModel(7424);
      end2D();
   }

   public static void drawWorldCircle(double var0, double var2, double var4, double var6, float var8, Color var9) {
      GlStateManager.pushMatrix();
      GL11.glLineWidth(var8);
      GL11.glBegin(2);
      double var13 = Math.tan(Math.PI / 180.0);
      double var15 = Math.cos(Math.PI / 180.0);
      double var17 = var6;
      double var19 = 0.0;

      for (int var21 = 0; var21 <= 360.0; var21++) {
         setColor(var9.getRGB());
         GL11.glVertex3d(var0 + var17, var2, var4 + var19);
         double var22 = -var19;
         double var24 = var17;
         var17 += var22 * var13;
         var19 += var24 * var13;
         var17 *= var15;
         var19 *= var15;
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
