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
   private static final long ab = 74123780237238L;

   public static void f(double var0, double var2, double var4, double var6, int var8) {
      if (var6 < var4) {
         double var12 = var4;
         var4 = var6;
         var6 = var12;
      }

      H(var0, var4, var2, var6, var8);
   }

   public static void v(double var0, double var2, double var4, double var6, int var8) {
      if (var2 < var0) {
         double var12 = var0;
         var0 = var2;
         var2 = var12;
      }

      H(var0, var4, var2 + 1.0, var6, var8);
   }

   public static void H(double var0, double var2, double var4, double var6, int var8) {
      long var9 = ab ^ 59168275237780L;
      if (var0 < var4) {
         double var12 = var0;
         var0 = var4;
         var4 = var12;
      }

      if (var2 < var6) {
         double var18 = var2;
         var2 = var6;
         var6 = var18;
      }

      float var19 = (var8 >> 24 & 0xFF) / 255.0F;
      float var13 = (var8 >> 16 & 0xFF) / 255.0F;
      float var14 = (var8 >> 8 & 0xFF) / 255.0F;
      float var15 = (var8 & 0xFF) / 255.0F;
      Tessellator var16 = Tessellator.getInstance();
      long var10000 = 4214691924892108555L ^ var9;
      WorldRenderer var17 = var16.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(var13, var14, var15, var19);
      var17.begin(7, DefaultVertexFormats.POSITION);
      var17.pos(var0, var6, 0.0).endVertex();
      var17.pos(var4, var6, 0.0).endVertex();
      var17.pos(var4, var2, 0.0).endVertex();
      var17.pos(var0, var2, 0.0).endVertex();
      var16.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void S(int var0, int var1, int var2, int var3) {
      if (var1 < var0) {
         int var7 = var0;
         var0 = var1;
         var1 = var7;
      }

      drawRect(var0, var2, var1 + 1, var2 + 1, var3);
   }

   public static void J(int var0, int var1, int var2, int var3) {
      if (var2 < var1) {
         int var7 = var1;
         var1 = var2;
         var2 = var7;
      }

      drawRect(var0, var1 + 1, var0 + 1, var2, var3);
   }

   public static void drawRect(int var0, int var1, int var2, int var3, int var4) {
      long var5 = ab ^ 19418921007255L;
      if (var0 < var2) {
         int var8 = var0;
         var0 = var2;
         var2 = var8;
      }

      if (var1 < var3) {
         int var14 = var1;
         var1 = var3;
         var3 = var14;
      }

      float var15 = (var4 >> 24 & 0xFF) / 255.0F;
      float var9 = (var4 >> 16 & 0xFF) / 255.0F;
      float var10 = (var4 >> 8 & 0xFF) / 255.0F;
      float var11 = (var4 & 0xFF) / 255.0F;
      Tessellator var12 = Tessellator.getInstance();
      long var10000 = 4214722192065344520L ^ var5;
      WorldRenderer var13 = var12.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(var9, var10, var11, var15);
      var13.begin(7, DefaultVertexFormats.POSITION);
      var13.pos(var0, var3, 0.0).endVertex();
      var13.pos(var2, var3, 0.0).endVertex();
      var13.pos(var2, var1, 0.0).endVertex();
      var13.pos(var0, var1, 0.0).endVertex();
      var12.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public void drawGradientRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var9 = (var5 >> 24 & 0xFF) / 255.0F;
      float var10 = (var5 >> 16 & 0xFF) / 255.0F;
      float var11 = (var5 >> 8 & 0xFF) / 255.0F;
      float var12 = (var5 & 0xFF) / 255.0F;
      float var13 = (var6 >> 24 & 0xFF) / 255.0F;
      float var14 = (var6 >> 16 & 0xFF) / 255.0F;
      float var15 = (var6 >> 8 & 0xFF) / 255.0F;
      float var16 = (var6 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator var17 = Tessellator.getInstance();
      WorldRenderer var18 = var17.getWorldRenderer();
      var18.begin(7, DefaultVertexFormats.POSITION_COLOR);
      var18.pos(var3, var2, this.zLevel).color(var10, var11, var12, var9).endVertex();
      var18.pos(var1, var2, this.zLevel).color(var10, var11, var12, var9).endVertex();
      var18.pos(var1, var4, this.zLevel).color(var14, var15, var16, var13).endVertex();
      var18.pos(var3, var4, this.zLevel).color(var14, var15, var16, var13).endVertex();
      var17.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void d(FontRenderer var1, String var2, int var3, int var4, int var5) {
      var1.drawStringWithShadow(var2, var3 - var1.getStringWidth(var2) / 2, var4, var5);
   }

   public void I(FontRenderer var1, String var2, int var3, int var4, int var5) {
      var1.drawStringWithShadow(var2, var3, var4, var5);
   }

   public void drawTexturedModalRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var9 = 0.00390625F;
      float var10 = 0.00390625F;
      Tessellator var11 = Tessellator.getInstance();
      WorldRenderer var12 = var11.getWorldRenderer();
      var12.begin(7, DefaultVertexFormats.POSITION_TEX);
      var12.pos(var1 + 0, var2 + var6, this.zLevel).tex((var3 + 0) * var9, (var4 + var6) * var10).endVertex();
      var12.pos(var1 + var5, var2 + var6, this.zLevel).tex((var3 + var5) * var9, (var4 + var6) * var10).endVertex();
      var12.pos(var1 + var5, var2 + 0, this.zLevel).tex((var3 + var5) * var9, (var4 + 0) * var10).endVertex();
      var12.pos(var1 + 0, var2 + 0, this.zLevel).tex((var3 + 0) * var9, (var4 + 0) * var10).endVertex();
      var11.draw();
   }

   public void drawTexturedModalRect(float var1, float var2, int var3, int var4, int var5, int var6) {
      float var9 = 0.00390625F;
      float var10 = 0.00390625F;
      Tessellator var11 = Tessellator.getInstance();
      WorldRenderer var12 = var11.getWorldRenderer();
      var12.begin(7, DefaultVertexFormats.POSITION_TEX);
      var12.pos(var1 + 0.0F, var2 + var6, this.zLevel).tex((var3 + 0) * var9, (var4 + var6) * var10).endVertex();
      var12.pos(var1 + var5, var2 + var6, this.zLevel).tex((var3 + var5) * var9, (var4 + var6) * var10).endVertex();
      var12.pos(var1 + var5, var2 + 0.0F, this.zLevel).tex((var3 + var5) * var9, (var4 + 0) * var10).endVertex();
      var12.pos(var1 + 0.0F, var2 + 0.0F, this.zLevel).tex((var3 + 0) * var9, (var4 + 0) * var10).endVertex();
      var11.draw();
   }

   public void drawTexturedModalRect(int var1, int var2, TextureAtlasSprite var3, int var4, int var5) {
      Tessellator var8 = Tessellator.getInstance();
      WorldRenderer var9 = var8.getWorldRenderer();
      var9.begin(7, DefaultVertexFormats.POSITION_TEX);
      var9.pos(var1 + 0, var2 + var5, this.zLevel).tex(var3.getMinU(), var3.getMaxV()).endVertex();
      var9.pos(var1 + var4, var2 + var5, this.zLevel).tex(var3.getMaxU(), var3.getMaxV()).endVertex();
      var9.pos(var1 + var4, var2 + 0, this.zLevel).tex(var3.getMaxU(), var3.getMinV()).endVertex();
      var9.pos(var1 + 0, var2 + 0, this.zLevel).tex(var3.getMinU(), var3.getMinV()).endVertex();
      var8.draw();
   }

   public static void drawModalRectWithCustomSizedTexture(int var0, int var1, float var2, float var3, int var4, int var5, float var6, float var7) {
      float var10 = 1.0F / var6;
      float var11 = 1.0F / var7;
      Tessellator var12 = Tessellator.getInstance();
      WorldRenderer var13 = var12.getWorldRenderer();
      var13.begin(7, DefaultVertexFormats.POSITION_TEX);
      var13.pos(var0, var1 + var5, 0.0).tex(var2 * var10, (var3 + var5) * var11).endVertex();
      var13.pos(var0 + var4, var1 + var5, 0.0).tex((var2 + var4) * var10, (var3 + var5) * var11).endVertex();
      var13.pos(var0 + var4, var1, 0.0).tex((var2 + var4) * var10, var3 * var11).endVertex();
      var13.pos(var0, var1, 0.0).tex(var2 * var10, var3 * var11).endVertex();
      var12.draw();
   }

   public static void drawScaledCustomSizeModalRect(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, float var9) {
      float var12 = 1.0F / var8;
      float var13 = 1.0F / var9;
      Tessellator var14 = Tessellator.getInstance();
      WorldRenderer var15 = var14.getWorldRenderer();
      var15.begin(7, DefaultVertexFormats.POSITION_TEX);
      var15.pos(var0, var1 + var7, 0.0).tex(var2 * var12, (var3 + var5) * var13).endVertex();
      var15.pos(var0 + var6, var1 + var7, 0.0).tex((var2 + var4) * var12, (var3 + var5) * var13).endVertex();
      var15.pos(var0 + var6, var1, 0.0).tex((var2 + var4) * var12, var3 * var13).endVertex();
      var15.pos(var0, var1, 0.0).tex(var2 * var12, var3 * var13).endVertex();
      var14.draw();
   }
}
