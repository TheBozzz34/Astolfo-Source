package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui {
   public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
   public float zLevel;

   public static void f(double d, double d2, double d3, double d4, int i) {
      if (d4 < d3) {
         double e = d3;
         d3 = d4;
         d4 = e;
      }

      H(d, d3, d2, d4, i);
   }

   public static void v(double d, double d2, double d3, double d4, int i) {
      if (d2 < d) {
         double e = d;
         d = d2;
         d2 = e;
      }

      H(d, d3, d2 + 1.0, d4, i);
   }

   public static void H(double d, double d2, double d3, double d4, int i) {
      if (d < d3) {
         double e = d;
         d = d3;
         d3 = e;
      }

      if (d2 < d4) {
         double d5 = d2;
         d2 = d4;
         d4 = d5;
      }

      float f = (i >> 24 & 0xFF) / 255.0F;
      float g = (i >> 16 & 0xFF) / 255.0F;
      float h = (i >> 8 & 0xFF) / 255.0F;
      float f2 = (i & 0xFF) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(g, h, f2, f);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(d, d4, 0.0).endVertex();
      worldRenderer.pos(d3, d4, 0.0).endVertex();
      worldRenderer.pos(d3, d2, 0.0).endVertex();
      worldRenderer.pos(d, d2, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void S(int i, int j, int k, int l) {
      if (j < i) {
         int m = i;
         i = j;
         j = m;
      }

      drawRect(i, k, j + 1, k + 1, l);
   }

   public static void J(int i, int j, int k, int l) {
      if (k < j) {
         int m = j;
         j = k;
         k = m;
      }

      drawRect(i, j + 1, i + 1, k, l);
   }

   public static void drawRect(int left, int top, int right, int bottom, int color) {
      if (left < right) {
         int i = left;
         left = right;
         right = i;
      }

      if (top < bottom) {
         int j = top;
         top = bottom;
         bottom = j;
      }

      float f = (color >> 24 & 0xFF) / 255.0F;
      float g = (color >> 16 & 0xFF) / 255.0F;
      float h = (color >> 8 & 0xFF) / 255.0F;
      float f2 = (color & 0xFF) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(g, h, f2, f);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(left, bottom, 0.0).endVertex();
      worldRenderer.pos(right, bottom, 0.0).endVertex();
      worldRenderer.pos(right, top, 0.0).endVertex();
      worldRenderer.pos(left, top, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (startColor >> 24 & 0xFF) / 255.0F;
      float g = (startColor >> 16 & 0xFF) / 255.0F;
      float h = (startColor >> 8 & 0xFF) / 255.0F;
      float f2 = (startColor & 0xFF) / 255.0F;
      float f3 = (endColor >> 24 & 0xFF) / 255.0F;
      float f4 = (endColor >> 16 & 0xFF) / 255.0F;
      float f5 = (endColor >> 8 & 0xFF) / 255.0F;
      float f6 = (endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(right, top, this.zLevel).color(g, h, f2, f).endVertex();
      worldRenderer.pos(left, top, this.zLevel).color(g, h, f2, f).endVertex();
      worldRenderer.pos(left, bottom, this.zLevel).color(f4, f5, f6, f3).endVertex();
      worldRenderer.pos(right, bottom, this.zLevel).color(f4, f5, f6, f3).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void d(FontRenderer fontRenderer, String string, int i, int j, int k) {
      fontRenderer.drawStringWithShadow(string, i - fontRenderer.getStringWidth(string) / 2, j, k);
   }

   public void I(FontRenderer fontRenderer, String string, int i, int j, int k) {
      fontRenderer.drawStringWithShadow(string, i, j, k);
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      float g = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x + 0, y + height, this.zLevel).tex((textureX + 0) * f, (textureY + height) * g).endVertex();
      worldRenderer.pos(x + width, y + height, this.zLevel).tex((textureX + width) * f, (textureY + height) * g).endVertex();
      worldRenderer.pos(x + width, y + 0, this.zLevel).tex((textureX + width) * f, (textureY + 0) * g).endVertex();
      worldRenderer.pos(x + 0, y + 0, this.zLevel).tex((textureX + 0) * f, (textureY + 0) * g).endVertex();
      tessellator.draw();
   }

   public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
      float f = 0.00390625F;
      float g = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(xCoord + 0.0F, yCoord + maxV, this.zLevel).tex((minU + 0) * f, (minV + maxV) * g).endVertex();
      worldRenderer.pos(xCoord + maxU, yCoord + maxV, this.zLevel).tex((minU + maxU) * f, (minV + maxV) * g).endVertex();
      worldRenderer.pos(xCoord + maxU, yCoord + 0.0F, this.zLevel).tex((minU + maxU) * f, (minV + 0) * g).endVertex();
      worldRenderer.pos(xCoord + 0.0F, yCoord + 0.0F, this.zLevel).tex((minU + 0) * f, (minV + 0) * g).endVertex();
      tessellator.draw();
   }

   public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(xCoord + 0, yCoord + heightIn, this.zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
      worldRenderer.pos(xCoord + widthIn, yCoord + heightIn, this.zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
      worldRenderer.pos(xCoord + widthIn, yCoord + 0, this.zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
      worldRenderer.pos(xCoord + 0, yCoord + 0, this.zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
      tessellator.draw();
   }

   public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
      float f = 1.0F / textureWidth;
      float g = 1.0F / textureHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x, y + height, 0.0).tex(u * f, (v + height) * g).endVertex();
      worldRenderer.pos(x + width, y + height, 0.0).tex((u + width) * f, (v + height) * g).endVertex();
      worldRenderer.pos(x + width, y, 0.0).tex((u + width) * f, v * g).endVertex();
      worldRenderer.pos(x, y, 0.0).tex(u * f, v * g).endVertex();
      tessellator.draw();
   }

   public static void drawScaledCustomSizeModalRect(
      int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight
   ) {
      float f = 1.0F / tileWidth;
      float g = 1.0F / tileHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x, y + height, 0.0).tex(u * f, (v + vHeight) * g).endVertex();
      worldRenderer.pos(x + width, y + height, 0.0).tex((u + uWidth) * f, (v + vHeight) * g).endVertex();
      worldRenderer.pos(x + width, y, 0.0).tex((u + uWidth) * f, v * g).endVertex();
      worldRenderer.pos(x, y, 0.0).tex(u * f, v * g).endVertex();
      tessellator.draw();
   }
}
