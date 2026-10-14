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

   public RenderArrow(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.bindEntityTexture(entity);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      byte b = 0;
      float f = 0.0F;
      float g = 0.5F;
      float h = (0 + b * 10) / 32.0F;
      float f2 = (5 + b * 10) / 32.0F;
      float f3 = 0.0F;
      float f4 = 0.15625F;
      float f5 = (5 + b * 10) / 32.0F;
      float f6 = (10 + b * 10) / 32.0F;
      float f7 = 0.05625F;
      GlStateManager.enableRescaleNormal();
      float f8 = entity.arrowShake - partialTicks;
      if (f8 > 0.0F) {
         float f9 = -MathHelper.sin(f8 * 3.0F) * f8;
         GlStateManager.rotate(f9, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(f7, f7, f7);
      GlStateManager.translate(-4.0F, 0.0F, 0.0F);
      GL11.glNormal3f(f7, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-7.0, -2.0, -2.0).tex(f3, f5).endVertex();
      worldRenderer.pos(-7.0, -2.0, 2.0).tex(f4, f5).endVertex();
      worldRenderer.pos(-7.0, 2.0, 2.0).tex(f4, f6).endVertex();
      worldRenderer.pos(-7.0, 2.0, -2.0).tex(f3, f6).endVertex();
      tessellator.draw();
      GL11.glNormal3f(-f7, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-7.0, 2.0, -2.0).tex(f3, f5).endVertex();
      worldRenderer.pos(-7.0, 2.0, 2.0).tex(f4, f5).endVertex();
      worldRenderer.pos(-7.0, -2.0, 2.0).tex(f4, f6).endVertex();
      worldRenderer.pos(-7.0, -2.0, -2.0).tex(f3, f6).endVertex();
      tessellator.draw();

      for (int i = 0; i < 4; i++) {
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glNormal3f(0.0F, 0.0F, f7);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(-8.0, -2.0, 0.0).tex(f, h).endVertex();
         worldRenderer.pos(8.0, -2.0, 0.0).tex(g, h).endVertex();
         worldRenderer.pos(8.0, 2.0, 0.0).tex(g, f2).endVertex();
         worldRenderer.pos(-8.0, 2.0, 0.0).tex(f, f2).endVertex();
         tessellator.draw();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityArrow entity) {
      return arrowTextures;
   }
}
