package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.src.Config;
import shadersmod.client.Shaders;

public abstract class RenderLiving<T extends EntityLiving> extends RendererLivingEntity<T> {
   private static final long I = 23306386573185L;

   public RenderLiving(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
      super(rendermanagerIn, modelbaseIn, shadowsizeIn);
   }

   public boolean canRenderName(T entity) {
      return super.canRenderName((T)entity)
         && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
   }

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender((T)livingEntity, camera, camX, camY, camZ)) {
         return true;
      } else if (livingEntity.getLeashed() && livingEntity.getLeashedToEntity() != null) {
         Entity entity = livingEntity.getLeashedToEntity();
         return camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox());
      } else {
         return false;
      }
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
      this.renderLeash((T)entity, x, y, z, entityYaw, partialTicks);
   }

   public void setLightmap(T entityLivingIn, float partialTicks) {
      int i = entityLivingIn.getBrightnessForRender(partialTicks);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
   }

   private double interpolateValue(double startIn, double end, double pct) {
      return startIn + (end - startIn) * pct;
   }

   public void renderLeash(T entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!Config.isShaders() || !Shaders.isShadowPass) {
         Entity entity = entityLivingIn.getLeashedToEntity();
         if (entity != null) {
            y -= (1.6 - entityLivingIn.height) * 0.5;
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            double d = this.interpolateValue(entity.prevRotationYaw, entity.rotationYaw, partialTicks * 0.5F) * (float) (Math.PI / 180.0);
            double e = this.interpolateValue(entity.prevRotationPitch, entity.rotationPitch, partialTicks * 0.5F) * (float) (Math.PI / 180.0);
            double d2 = Math.cos(d);
            double d3 = Math.sin(d);
            double d4 = Math.sin(e);
            if (entity instanceof EntityHanging) {
               d2 = 0.0;
               d3 = 0.0;
               d4 = -1.0;
            }

            double d5 = Math.cos(e);
            double d6 = this.interpolateValue(entity.prevPosX, entity.posX, partialTicks) - d2 * 0.7 - d3 * 0.5 * d5;
            double d7 = this.interpolateValue(entity.prevPosY + entity.getEyeHeight() * 0.7, entity.posY + entity.getEyeHeight() * 0.7, partialTicks)
               - d4 * 0.5
               - 0.25;
            double d8 = this.interpolateValue(entity.prevPosZ, entity.posZ, partialTicks) - d3 * 0.7 + d2 * 0.5 * d5;
            double d9 = this.interpolateValue(entityLivingIn.prevRenderYawOffset, entityLivingIn.renderYawOffset, partialTicks) * (float) (Math.PI / 180.0)
               + (Math.PI / 2);
            d2 = Math.cos(d9) * entityLivingIn.width * 0.4;
            d3 = Math.sin(d9) * entityLivingIn.width * 0.4;
            double d10 = this.interpolateValue(entityLivingIn.prevPosX, entityLivingIn.posX, partialTicks) + d2;
            double d11 = this.interpolateValue(entityLivingIn.prevPosY, entityLivingIn.posY, partialTicks);
            double d12 = this.interpolateValue(entityLivingIn.prevPosZ, entityLivingIn.posZ, partialTicks) + d3;
            x += d2;
            z += d3;
            double d13 = (float)(d6 - d10);
            double d14 = (float)(d7 - d11);
            double d15 = (float)(d8 - d12);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            if (Config.isShaders()) {
               Shaders.beginLeash();
            }

            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= 24; i++) {
               float f = 0.5F;
               float g = 0.4F;
               float h = 0.3F;
               if (i % 2 == 0) {
                  f *= 0.7F;
                  g *= 0.7F;
                  h *= 0.7F;
               }

               float f2 = i / 24.0F;
               worldRenderer.pos(x + d13 * f2 + 0.0, y + d14 * (f2 * f2 + f2) * 0.5 + ((24.0F - i) / 18.0F + 0.125F), z + d15 * f2)
                  .color(f, g, h, 1.0F)
                  .endVertex();
               worldRenderer.pos(x + d13 * f2 + 0.025, y + d14 * (f2 * f2 + f2) * 0.5 + ((24.0F - i) / 18.0F + 0.125F) + 0.025, z + d15 * f2)
                  .color(f, g, h, 1.0F)
                  .endVertex();
            }

            tessellator.draw();
            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int j = 0; j <= 24; j++) {
               float f3 = 0.5F;
               float f4 = 0.4F;
               float f5 = 0.3F;
               if (j % 2 == 0) {
                  f3 *= 0.7F;
                  f4 *= 0.7F;
                  f5 *= 0.7F;
               }

               float f6 = j / 24.0F;
               worldRenderer.pos(x + d13 * f6 + 0.0, y + d14 * (f6 * f6 + f6) * 0.5 + ((24.0F - j) / 18.0F + 0.125F) + 0.025, z + d15 * f6)
                  .color(f3, f4, f5, 1.0F)
                  .endVertex();
               worldRenderer.pos(x + d13 * f6 + 0.025, y + d14 * (f6 * f6 + f6) * 0.5 + ((24.0F - j) / 18.0F + 0.125F), z + d15 * f6 + 0.025)
                  .color(f3, f4, f5, 1.0F)
                  .endVertex();
            }

            tessellator.draw();
            if (Config.isShaders()) {
               Shaders.endLeash();
            }

            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
         }
      }
   }
}
