package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow extends Render<EntityArrow> {
   private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");
   private static final long a = 139517658063056L;

   public RenderArrow(RenderManager var1) {
      super(var1);
   }

   public void doRender(EntityArrow var1, double var2, double var4, double var6, float var8, float var9) {
      this.bindEntityTexture(var1);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
      GlStateManager.rotate(var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var9 - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var9, 0.0F, 0.0F, 1.0F);
      Tessellator var13 = Tessellator.getInstance();
      WorldRenderer var14 = var13.getWorldRenderer();
      byte var15 = 0;
      float var16 = 0.0F;
      float var17 = 0.5F;
      float var18 = (0 + var15 * 10) / 32.0F;
      float var19 = (5 + var15 * 10) / 32.0F;
      float var20 = 0.0F;
      float var21 = 0.15625F;
      float var22 = (5 + var15 * 10) / 32.0F;
      float var23 = (10 + var15 * 10) / 32.0F;
      float var24 = 0.05625F;
      GlStateManager.enableRescaleNormal();
      float var25 = var1.arrowShake - var9;
      if (var25 > 0.0F) {
         float var26 = -MathHelper.sin(var25 * 3.0F) * var25;
         GlStateManager.rotate(var26, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(var24, var24, var24);
      GlStateManager.translate(-4.0F, 0.0F, 0.0F);
      GL11.glNormal3f(var24, 0.0F, 0.0F);
      var14.begin(7, DefaultVertexFormats.POSITION_TEX);
      var14.pos(-7.0, -2.0, -2.0).tex(var20, var22).endVertex();
      var14.pos(-7.0, -2.0, 2.0).tex(var21, var22).endVertex();
      var14.pos(-7.0, 2.0, 2.0).tex(var21, var23).endVertex();
      var14.pos(-7.0, 2.0, -2.0).tex(var20, var23).endVertex();
      var13.draw();
      GL11.glNormal3f(-var24, 0.0F, 0.0F);
      var14.begin(7, DefaultVertexFormats.POSITION_TEX);
      var14.pos(-7.0, 2.0, -2.0).tex(var20, var22).endVertex();
      var14.pos(-7.0, 2.0, 2.0).tex(var21, var22).endVertex();
      var14.pos(-7.0, -2.0, 2.0).tex(var21, var23).endVertex();
      var14.pos(-7.0, -2.0, -2.0).tex(var20, var23).endVertex();
      var13.draw();

      for (int var27 = 0; var27 < 4; var27++) {
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, var24);
         var14.begin(7, DefaultVertexFormats.POSITION_TEX);
         var14.pos(-8.0, -2.0, 0.0).tex(var16, var18).endVertex();
         var14.pos(8.0, -2.0, 0.0).tex(var17, var18).endVertex();
         var14.pos(8.0, 2.0, 0.0).tex(var17, var19).endVertex();
         var14.pos(-8.0, 2.0, 0.0).tex(var16, var19).endVertex();
         var13.draw();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityArrow var1) {
      return arrowTextures;
   }
}
