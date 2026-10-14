package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MobAppearance extends EntityFX {
   private EntityLivingBase entity;
   private static final long ab = 125906275393812L;

   public MobAppearance(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.particleGravity = 0.0F;
      this.particleMaxAge = 30;
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.entity == null) {
         EntityGuardian entityGuardian = new EntityGuardian(this.worldObj);
         entityGuardian.setElder();
         this.entity = entityGuardian;
      }
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      long l = ab ^ 44170092490190L;
      if (this.entity != null) {
         RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
         renderManager.setRenderPosition(EntityFX.interpPosX, EntityFX.interpPosY, EntityFX.interpPosZ);
         long m = 8016719729169694640L ^ l;
         float f = 0.42553192F;
         float g = (this.particleAge + partialTicks) / this.particleMaxAge;
         GlStateManager.depthMask(true);
         GlStateManager.enableBlend();
         GlStateManager.enableDepth();
         GlStateManager.blendFunc(770, 771);
         float h = 240.0F;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, h, h);
         GlStateManager.pushMatrix();
         float f2 = 0.05F + 0.5F * MathHelper.sin(g * (float) Math.PI);
         GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
         GlStateManager.translate(0.0F, 1.8F, 0.0F);
         GlStateManager.rotate(180.0F - entityIn.rotationYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(60.0F - 150.0F * g - entityIn.rotationPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.4F, -1.5F);
         GlStateManager.scale(f, f, f);
         this.entity.rotationYaw = this.entity.prevRotationYaw = 0.0F;
         this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0.0F;
         renderManager.renderEntityWithPosYaw(this.entity, 0.0, 0.0, 0.0, 0.0F, partialTicks);
         GlStateManager.popMatrix();
         GlStateManager.enableDepth();
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new MobAppearance(worldIn, xCoordIn, yCoordIn, zCoordIn);
      }
   }
}
