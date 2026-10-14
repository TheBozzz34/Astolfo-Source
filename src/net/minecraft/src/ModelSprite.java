package net.minecraft.src;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelSprite {
   private ModelRenderer modelRenderer = null;
   private int textureOffsetX = 0;
   private int textureOffsetY = 0;
   private float posX = 0.0F;
   private float posY = 0.0F;
   private float posZ = 0.0F;
   private int sizeX = 0;
   private int sizeY = 0;
   private int sizeZ = 0;
   private float sizeAdd = 0.0F;
   private float minU = 0.0F;
   private float minV = 0.0F;
   private float maxU = 0.0F;
   private float maxV = 0.0F;
   private static final long b = 139037705526000L;

   public ModelSprite(
      ModelRenderer modelRenderer, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int sizeX, int sizeY, int sizeZ, float sizeAdd
   ) {
      this.modelRenderer = modelRenderer;
      this.textureOffsetX = textureOffsetX;
      this.textureOffsetY = textureOffsetY;
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      this.sizeZ = sizeZ;
      this.sizeAdd = sizeAdd;
      this.minU = textureOffsetX / modelRenderer.textureWidth;
      this.minV = textureOffsetY / modelRenderer.textureHeight;
      this.maxU = (textureOffsetX + sizeX) / modelRenderer.textureWidth;
      this.maxV = (textureOffsetY + sizeY) / modelRenderer.textureHeight;
   }

   public void render(Tessellator tessellator, float scale) {
      GlStateManager.translate(this.posX * scale, this.posY * scale, this.posZ * scale);
      float f = this.minU;
      float g = this.maxU;
      float h = this.minV;
      float f2 = this.maxV;
      if (this.modelRenderer.mirror) {
         f = this.maxU;
         g = this.minU;
      }

      if (this.modelRenderer.mirrorV) {
         h = this.maxV;
         f2 = this.minV;
      }

      renderItemIn2D(
         tessellator, f, h, g, f2, this.sizeX, this.sizeY, scale * this.sizeZ, this.modelRenderer.textureWidth, this.modelRenderer.textureHeight
      );
      GlStateManager.translate(-this.posX * scale, -this.posY * scale, -this.posZ * scale);
   }

   public static void renderItemIn2D(
      Tessellator tess, float minU, float minV, float maxU, float maxV, int sizeX, int sizeY, float width, float texWidth, float texHeight
   ) {
      if (width < 6.25E-4F) {
         width = 6.25E-4F;
      }

      float f = maxU - minU;
      float g = maxV - minV;
      double d = MathHelper.abs(f) * (texWidth / 16.0F);
      double e = MathHelper.abs(g) * (texHeight / 16.0F);
      WorldRenderer worldRenderer = tess.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, e, 0.0).tex(minU, maxV).endVertex();
      worldRenderer.pos(d, e, 0.0).tex(maxU, maxV).endVertex();
      worldRenderer.pos(d, 0.0, 0.0).tex(maxU, minV).endVertex();
      worldRenderer.pos(0.0, 0.0, 0.0).tex(minU, minV).endVertex();
      tess.draw();
      GL11.glNormal3f(0.0F, 0.0F, 1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, 0.0, width).tex(minU, minV).endVertex();
      worldRenderer.pos(d, 0.0, width).tex(maxU, minV).endVertex();
      worldRenderer.pos(d, e, width).tex(maxU, maxV).endVertex();
      worldRenderer.pos(0.0, e, width).tex(minU, maxV).endVertex();
      tess.draw();
      float h = 0.5F * f / sizeX;
      float f2 = 0.5F * g / sizeY;
      GL11.glNormal3f(-1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int i = 0; i < sizeX; i++) {
         float f3 = (float)i / sizeX;
         float f4 = minU + f * f3 + h;
         worldRenderer.pos(f3 * d, e, width).tex(f4, maxV).endVertex();
         worldRenderer.pos(f3 * d, e, 0.0).tex(f4, maxV).endVertex();
         worldRenderer.pos(f3 * d, 0.0, 0.0).tex(f4, minV).endVertex();
         worldRenderer.pos(f3 * d, 0.0, width).tex(f4, minV).endVertex();
      }

      tess.draw();
      GL11.glNormal3f(1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int j = 0; j < sizeX; j++) {
         float f5 = (float)j / sizeX;
         float f6 = minU + f * f5 + h;
         float f7 = f5 + 1.0F / sizeX;
         worldRenderer.pos(f7 * d, 0.0, width).tex(f6, minV).endVertex();
         worldRenderer.pos(f7 * d, 0.0, 0.0).tex(f6, minV).endVertex();
         worldRenderer.pos(f7 * d, e, 0.0).tex(f6, maxV).endVertex();
         worldRenderer.pos(f7 * d, e, width).tex(f6, maxV).endVertex();
      }

      tess.draw();
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int k = 0; k < sizeY; k++) {
         float f8 = (float)k / sizeY;
         float f9 = minV + g * f8 + f2;
         float f10 = f8 + 1.0F / sizeY;
         worldRenderer.pos(0.0, f10 * e, width).tex(minU, f9).endVertex();
         worldRenderer.pos(d, f10 * e, width).tex(maxU, f9).endVertex();
         worldRenderer.pos(d, f10 * e, 0.0).tex(maxU, f9).endVertex();
         worldRenderer.pos(0.0, f10 * e, 0.0).tex(minU, f9).endVertex();
      }

      tess.draw();
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int l = 0; l < sizeY; l++) {
         float f11 = (float)l / sizeY;
         float f12 = minV + g * f11 + f2;
         worldRenderer.pos(d, f11 * e, width).tex(maxU, f12).endVertex();
         worldRenderer.pos(0.0, f11 * e, width).tex(minU, f12).endVertex();
         worldRenderer.pos(0.0, f11 * e, 0.0).tex(minU, f12).endVertex();
         worldRenderer.pos(d, f11 * e, 0.0).tex(maxU, f12).endVertex();
      }

      tess.draw();
   }
}
