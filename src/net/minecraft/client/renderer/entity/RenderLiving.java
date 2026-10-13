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

   public RenderLiving(RenderManager var1, ModelBase var2, float var3) {
      super(var1, var2, var3);
   }

   public boolean canRenderName(T var1) {
      return super.canRenderName((T)var1) && (var1.getAlwaysRenderNameTagForRender() || var1.hasCustomName() && var1 == this.renderManager.pointedEntity);
   }

   public boolean shouldRender(T var1, ICamera var2, double var3, double var5, double var7) {
      if (super.shouldRender((T)var1, var2, var3, var5, var7)) {
         return true;
      } else if (var1.getLeashed() && var1.getLeashedToEntity() != null) {
         Entity var12 = var1.getLeashedToEntity();
         return var2.isBoundingBoxInFrustum(var12.getEntityBoundingBox());
      } else {
         return false;
      }
   }

   public void doRender(T var1, double var2, double var4, double var6, float var8, float var9) {
      super.doRender((T)var1, var2, var4, var6, var8, var9);
      this.renderLeash((T)var1, var2, var4, var6, var8, var9);
   }

   public void setLightmap(T var1, float var2) {
      int var5 = var1.getBrightnessForRender(var2);
      int var6 = var5 % 65536;
      int var7 = var5 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var6 / 1.0F, var7 / 1.0F);
   }

   private double interpolateValue(double var1, double var3, double var5) {
      return var1 + (var3 - var1) * var5;
   }

   public void renderLeash(T var1, double var2, double var4, double var6, float var8, float var9) {
      if (!Config.isShaders() || !Shaders.isShadowPass) {
         Entity var13 = var1.getLeashedToEntity();
         if (var13 != null) {
            var4 -= (1.6 - var1.height) * 0.5;
            Tessellator var14 = Tessellator.getInstance();
            WorldRenderer var15 = var14.getWorldRenderer();
            double var16 = this.interpolateValue(var13.prevRotationYaw, var13.rotationYaw, var9 * 0.5F) * (float) (Math.PI / 180.0);
            double var18 = this.interpolateValue(var13.prevRotationPitch, var13.rotationPitch, var9 * 0.5F) * (float) (Math.PI / 180.0);
            double var20 = Math.cos(var16);
            double var22 = Math.sin(var16);
            double var24 = Math.sin(var18);
            if (var13 instanceof EntityHanging) {
               var20 = 0.0;
               var22 = 0.0;
               var24 = -1.0;
            }

            double var26 = Math.cos(var18);
            double var28 = this.interpolateValue(var13.prevPosX, var13.posX, var9) - var20 * 0.7 - var22 * 0.5 * var26;
            double var30 = this.interpolateValue(var13.prevPosY + var13.getEyeHeight() * 0.7, var13.posY + var13.getEyeHeight() * 0.7, var9)
               - var24 * 0.5
               - 0.25;
            double var32 = this.interpolateValue(var13.prevPosZ, var13.posZ, var9) - var22 * 0.7 + var20 * 0.5 * var26;
            double var34 = this.interpolateValue(var1.prevRenderYawOffset, var1.renderYawOffset, var9) * (float) (Math.PI / 180.0) + (Math.PI / 2);
            var20 = Math.cos(var34) * var1.width * 0.4;
            var22 = Math.sin(var34) * var1.width * 0.4;
            double var36 = this.interpolateValue(var1.prevPosX, var1.posX, var9) + var20;
            double var38 = this.interpolateValue(var1.prevPosY, var1.posY, var9);
            double var40 = this.interpolateValue(var1.prevPosZ, var1.posZ, var9) + var22;
            var2 += var20;
            var6 += var22;
            double var42 = (float)(var28 - var36);
            double var44 = (float)(var30 - var38);
            double var46 = (float)(var32 - var40);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            if (Config.isShaders()) {
               Shaders.beginLeash();
            }

            var15.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int var51 = 0; var51 <= 24; var51++) {
               float var52 = 0.5F;
               float var53 = 0.4F;
               float var54 = 0.3F;
               if (var51 % 2 == 0) {
                  var52 *= 0.7F;
                  var53 *= 0.7F;
                  var54 *= 0.7F;
               }

               float var55 = var51 / 24.0F;
               var15.pos(var2 + var42 * var55 + 0.0, var4 + var44 * (var55 * var55 + var55) * 0.5 + ((24.0F - var51) / 18.0F + 0.125F), var6 + var46 * var55)
                  .color(var52, var53, var54, 1.0F)
                  .endVertex();
               var15.pos(
                     var2 + var42 * var55 + 0.025,
                     var4 + var44 * (var55 * var55 + var55) * 0.5 + ((24.0F - var51) / 18.0F + 0.125F) + 0.025,
                     var6 + var46 * var55
                  )
                  .color(var52, var53, var54, 1.0F)
                  .endVertex();
            }

            var14.draw();
            var15.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int var61 = 0; var61 <= 24; var61++) {
               float var62 = 0.5F;
               float var63 = 0.4F;
               float var64 = 0.3F;
               if (var61 % 2 == 0) {
                  var62 *= 0.7F;
                  var63 *= 0.7F;
                  var64 *= 0.7F;
               }

               float var65 = var61 / 24.0F;
               var15.pos(
                     var2 + var42 * var65 + 0.0,
                     var4 + var44 * (var65 * var65 + var65) * 0.5 + ((24.0F - var61) / 18.0F + 0.125F) + 0.025,
                     var6 + var46 * var65
                  )
                  .color(var62, var63, var64, 1.0F)
                  .endVertex();
               var15.pos(
                     var2 + var42 * var65 + 0.025,
                     var4 + var44 * (var65 * var65 + var65) * 0.5 + ((24.0F - var61) / 18.0F + 0.125F),
                     var6 + var46 * var65 + 0.025
                  )
                  .color(var62, var63, var64, 1.0F)
                  .endVertex();
            }

            var14.draw();
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
