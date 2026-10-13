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

   public MobAppearance(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
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
         EntityGuardian var3 = new EntityGuardian(this.worldObj);
         var3.setElder();
         this.entity = var3;
      }
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      long var9 = ab ^ 44170092490190L;
      if (this.entity != null) {
         RenderManager var11 = Minecraft.getMinecraft().getRenderManager();
         var11.setRenderPosition(EntityFX.interpPosX, EntityFX.interpPosY, EntityFX.interpPosZ);
         long var10000 = 8016719729169694640L ^ var9;
         float var12 = 0.42553192F;
         float var13 = (this.particleAge + var3) / this.particleMaxAge;
         GlStateManager.depthMask(true);
         GlStateManager.enableBlend();
         GlStateManager.enableDepth();
         GlStateManager.blendFunc(770, 771);
         float var14 = 240.0F;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var14, var14);
         GlStateManager.pushMatrix();
         float var15 = 0.05F + 0.5F * MathHelper.sin(var13 * (float) Math.PI);
         GlStateManager.color(1.0F, 1.0F, 1.0F, var15);
         GlStateManager.translate(0.0F, 1.8F, 0.0F);
         GlStateManager.rotate(180.0F - var2.rotationYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(60.0F - 150.0F * var13 - var2.rotationPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.4F, -1.5F);
         GlStateManager.scale(var12, var12, var12);
         this.entity.rotationYaw = this.entity.prevRotationYaw = 0.0F;
         this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0.0F;
         var11.renderEntityWithPosYaw(this.entity, 0.0, 0.0, 0.0, 0.0F, var3);
         GlStateManager.popMatrix();
         GlStateManager.enableDepth();
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new MobAppearance(var2, var3, var5, var7);
      }
   }
}
