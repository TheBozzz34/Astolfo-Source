package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderGuardian extends RenderLiving<EntityGuardian> {
   private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
   public int field_177115_a = ((ModelGuardian)this.mainModel).func_178706_a();

   public RenderGuardian(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelGuardian(), 0.5F);
   }

   public boolean shouldRender(EntityGuardian livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      }

      if (livingEntity.hasTargetedEntity()) {
         EntityLivingBase entityLivingBase = livingEntity.getTargetedEntity();
         if (entityLivingBase != null) {
            Vec3 vec3 = this.func_177110_a(entityLivingBase, entityLivingBase.height * 0.5, 1.0F);
            Vec3 vec32 = this.func_177110_a(livingEntity, livingEntity.getEyeHeight(), 1.0F);
            if (camera.isBoundingBoxInFrustum(AxisAlignedBB.fromBounds(vec32.xCoord, vec32.yCoord, vec32.zCoord, vec3.xCoord, vec3.yCoord, vec3.zCoord))) {
               return true;
            }
         }
      }

      return false;
   }

   private Vec3 func_177110_a(EntityLivingBase entityLivingBaseIn, double d, float g) {
      double e = entityLivingBaseIn.lastTickPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.lastTickPosX) * g;
      double d2 = d + entityLivingBaseIn.lastTickPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.lastTickPosY) * g;
      double d3 = entityLivingBaseIn.lastTickPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.lastTickPosZ) * g;
      return new Vec3(e, d2, d3);
   }

   public void doRender(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (this.field_177115_a != ((ModelGuardian)this.mainModel).func_178706_a()) {
         this.mainModel = new ModelGuardian();
         this.field_177115_a = ((ModelGuardian)this.mainModel).func_178706_a();
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      EntityLivingBase entityLivingBase = entity.getTargetedEntity();
      if (entityLivingBase != null) {
         float f = entity.func_175477_p(partialTicks);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         this.bindTexture(GUARDIAN_BEAM_TEXTURE);
         GL11.glTexParameterf(3553, 10242, 10497.0F);
         GL11.glTexParameterf(3553, 10243, 10497.0F);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         float g = 240.0F;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, g, g);
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         float h = (float)entity.worldObj.getTotalWorldTime() + partialTicks;
         float f2 = h * 0.5F % 1.0F;
         float f3 = entity.getEyeHeight();
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)x, (float)y + f3, (float)z);
         Vec3 vec3 = this.func_177110_a(entityLivingBase, entityLivingBase.height * 0.5, partialTicks);
         Vec3 vec32 = this.func_177110_a(entity, f3, partialTicks);
         Vec3 vec33 = vec3.subtract(vec32);
         double d = vec33.lengthVector() + 1.0;
         vec33 = vec33.normalize();
         float f4 = (float)Math.acos(vec33.yCoord);
         float f5 = (float)Math.atan2(vec33.zCoord, vec33.xCoord);
         GlStateManager.rotate(((float) (Math.PI / 2) + -f5) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(f4 * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         byte b = 1;
         double e = h * 0.05 * (1.0 - (b & 1) * 2.5);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         float f6 = f * f;
         int i = 64 + (int)(f6 * 240.0F);
         int j = 32 + (int)(f6 * 192.0F);
         int k = 128 - (int)(f6 * 64.0F);
         double d2 = b * 0.2;
         double d3 = d2 * 1.41;
         double d4 = 0.0 + Math.cos(e + (Math.PI * 3.0 / 4.0)) * d3;
         double d5 = 0.0 + Math.sin(e + (Math.PI * 3.0 / 4.0)) * d3;
         double d6 = 0.0 + Math.cos(e + (Math.PI / 4)) * d3;
         double d7 = 0.0 + Math.sin(e + (Math.PI / 4)) * d3;
         double d8 = 0.0 + Math.cos(e + (Math.PI * 5.0 / 4.0)) * d3;
         double d9 = 0.0 + Math.sin(e + (Math.PI * 5.0 / 4.0)) * d3;
         double d10 = 0.0 + Math.cos(e + (Math.PI * 7.0 / 4.0)) * d3;
         double d11 = 0.0 + Math.sin(e + (Math.PI * 7.0 / 4.0)) * d3;
         double d12 = 0.0 + Math.cos(e + Math.PI) * d2;
         double d13 = 0.0 + Math.sin(e + Math.PI) * d2;
         double d14 = 0.0 + Math.cos(e + 0.0) * d2;
         double d15 = 0.0 + Math.sin(e + 0.0) * d2;
         double d16 = 0.0 + Math.cos(e + (Math.PI / 2)) * d2;
         double d17 = 0.0 + Math.sin(e + (Math.PI / 2)) * d2;
         double d18 = 0.0 + Math.cos(e + (Math.PI * 3.0 / 2.0)) * d2;
         double d19 = 0.0 + Math.sin(e + (Math.PI * 3.0 / 2.0)) * d2;
         double d20 = -1.0F + f2;
         double d21 = d * (0.5 / d2) + d20;
         worldRenderer.pos(d12, d, d13).tex(0.4999, d21).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d12, 0.0, d13).tex(0.4999, d20).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d14, 0.0, d15).tex(0.0, d20).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d14, d, d15).tex(0.0, d21).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d16, d, d17).tex(0.4999, d21).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d16, 0.0, d17).tex(0.4999, d20).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d18, 0.0, d19).tex(0.0, d20).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d18, d, d19).tex(0.0, d21).color(i, j, k, 255).endVertex();
         double d22 = 0.0;
         if (entity.ticksExisted % 2 == 0) {
            d22 = 0.5;
         }

         worldRenderer.pos(d4, d, d5).tex(0.5, d22 + 0.5).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d6, d, d7).tex(1.0, d22 + 0.5).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d10, d, d11).tex(1.0, d22).color(i, j, k, 255).endVertex();
         worldRenderer.pos(d8, d, d9).tex(0.5, d22).color(i, j, k, 255).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }
   }

   public void preRenderCallback(EntityGuardian entitylivingbaseIn, float partialTickTime) {
      if (entitylivingbaseIn.isElder()) {
         GlStateManager.scale(2.35F, 2.35F, 2.35F);
      }
   }

   public ResourceLocation getEntityTexture(EntityGuardian entity) {
      return entity.isElder() ? GUARDIAN_ELDER_TEXTURE : GUARDIAN_TEXTURE;
   }
}
