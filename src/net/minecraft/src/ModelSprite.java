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

   public ModelSprite(ModelRenderer var1, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10) {
      this.modelRenderer = var1;
      this.textureOffsetX = var2;
      this.textureOffsetY = var3;
      this.posX = var4;
      this.posY = var5;
      this.posZ = var6;
      this.sizeX = var7;
      this.sizeY = var8;
      this.sizeZ = var9;
      this.sizeAdd = var10;
      this.minU = var2 / var1.textureWidth;
      this.minV = var3 / var1.textureHeight;
      this.maxU = (var2 + var7) / var1.textureWidth;
      this.maxV = (var3 + var8) / var1.textureHeight;
   }

   public void render(Tessellator var1, float var2) {
      GlStateManager.translate(this.posX * var2, this.posY * var2, this.posZ * var2);
      float var6 = this.minU;
      float var7 = this.maxU;
      float var8 = this.minV;
      float var9 = this.maxV;
      if (this.modelRenderer.mirror) {
         var6 = this.maxU;
         var7 = this.minU;
      }

      if (this.modelRenderer.mirrorV) {
         var8 = this.maxV;
         var9 = this.minV;
      }

      renderItemIn2D(var1, var6, var8, var7, var9, this.sizeX, this.sizeY, var2 * this.sizeZ, this.modelRenderer.textureWidth, this.modelRenderer.textureHeight);
      GlStateManager.translate(-this.posX * var2, -this.posY * var2, -this.posZ * var2);
   }

   public static void renderItemIn2D(Tessellator var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, float var9) {
      if (var7 < 6.25E-4F) {
         var7 = 6.25E-4F;
      }

      float var13 = var3 - var1;
      float var14 = var4 - var2;
      double var15 = MathHelper.abs(var13) * (var8 / 16.0F);
      double var17 = MathHelper.abs(var14) * (var9 / 16.0F);
      WorldRenderer var19 = var0.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      var19.begin(7, DefaultVertexFormats.POSITION_TEX);
      var19.pos(0.0, var17, 0.0).tex(var1, var4).endVertex();
      var19.pos(var15, var17, 0.0).tex(var3, var4).endVertex();
      var19.pos(var15, 0.0, 0.0).tex(var3, var2).endVertex();
      var19.pos(0.0, 0.0, 0.0).tex(var1, var2).endVertex();
      var0.draw();
      GL11.glNormal3f(0.0F, 0.0F, 1.0F);
      var19.begin(7, DefaultVertexFormats.POSITION_TEX);
      var19.pos(0.0, 0.0, var7).tex(var1, var2).endVertex();
      var19.pos(var15, 0.0, var7).tex(var3, var2).endVertex();
      var19.pos(var15, var17, var7).tex(var3, var4).endVertex();
      var19.pos(0.0, var17, var7).tex(var1, var4).endVertex();
      var0.draw();
      float var20 = 0.5F * var13 / var5;
      float var21 = 0.5F * var14 / var6;
      GL11.glNormal3f(-1.0F, 0.0F, 0.0F);
      var19.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int var22 = 0; var22 < var5; var22++) {
         float var23 = (float)var22 / var5;
         float var24 = var1 + var13 * var23 + var20;
         var19.pos(var23 * var15, var17, var7).tex(var24, var4).endVertex();
         var19.pos(var23 * var15, var17, 0.0).tex(var24, var4).endVertex();
         var19.pos(var23 * var15, 0.0, 0.0).tex(var24, var2).endVertex();
         var19.pos(var23 * var15, 0.0, var7).tex(var24, var2).endVertex();
      }

      var0.draw();
      GL11.glNormal3f(1.0F, 0.0F, 0.0F);
      var19.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int var26 = 0; var26 < var5; var26++) {
         float var29 = (float)var26 / var5;
         float var32 = var1 + var13 * var29 + var20;
         float var25 = var29 + 1.0F / var5;
         var19.pos(var25 * var15, 0.0, var7).tex(var32, var2).endVertex();
         var19.pos(var25 * var15, 0.0, 0.0).tex(var32, var2).endVertex();
         var19.pos(var25 * var15, var17, 0.0).tex(var32, var4).endVertex();
         var19.pos(var25 * var15, var17, var7).tex(var32, var4).endVertex();
      }

      var0.draw();
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      var19.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int var27 = 0; var27 < var6; var27++) {
         float var30 = (float)var27 / var6;
         float var33 = var2 + var14 * var30 + var21;
         float var35 = var30 + 1.0F / var6;
         var19.pos(0.0, var35 * var17, var7).tex(var1, var33).endVertex();
         var19.pos(var15, var35 * var17, var7).tex(var3, var33).endVertex();
         var19.pos(var15, var35 * var17, 0.0).tex(var3, var33).endVertex();
         var19.pos(0.0, var35 * var17, 0.0).tex(var1, var33).endVertex();
      }

      var0.draw();
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      var19.begin(7, DefaultVertexFormats.POSITION_TEX);

      for (int var28 = 0; var28 < var6; var28++) {
         float var31 = (float)var28 / var6;
         float var34 = var2 + var14 * var31 + var21;
         var19.pos(var15, var31 * var17, var7).tex(var3, var34).endVertex();
         var19.pos(0.0, var31 * var17, var7).tex(var1, var34).endVertex();
         var19.pos(0.0, var31 * var17, 0.0).tex(var1, var34).endVertex();
         var19.pos(var15, var31 * var17, 0.0).tex(var3, var34).endVertex();
      }

      var0.draw();
   }
}
