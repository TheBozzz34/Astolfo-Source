package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPortalFX extends EntityFX {
   private float portalParticleScale;
   private double portalPosX;
   private double portalPosY;
   private double portalPosZ;
   private static final long ab = 30202652017112L;

   public EntityPortalFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.motionX = var8;
      this.motionY = var10;
      this.motionZ = var12;
      this.portalPosX = this.posX = var2;
      this.portalPosY = this.posY = var4;
      this.portalPosZ = this.posZ = var6;
      float var16 = this.rand.nextFloat() * 0.6F + 0.4F;
      this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F * var16;
      this.particleGreen *= 0.3F;
      this.particleRed *= 0.9F;
      this.particleMaxAge = (int)(Math.random() * 10.0) + 40;
      this.noClip = true;
      this.setParticleTextureIndex((int)(Math.random() * 8.0));
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (this.particleAge + var3) / this.particleMaxAge;
      var9 = 1.0F - var9;
      var9 *= var9;
      var9 = 1.0F - var9;
      this.particleScale = this.portalParticleScale * var9;
      super.renderParticle(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public int getBrightnessForRender(float var1) {
      int var5 = super.getBrightnessForRender(var1);
      float var6 = (float)this.particleAge / this.particleMaxAge;
      var6 *= var6;
      var6 *= var6;
      int var7 = var5 & 0xFF;
      int var8 = var5 >> 16 & 0xFF;
      var8 += (int)(var6 * 15.0F * 16.0F);
      if (var8 > 240) {
         var8 = 240;
      }

      return var7 | var8 << 16;
   }

   @Override
   public float getBrightness(float var1) {
      float var2 = super.getBrightness(var1);
      float var3 = (float)this.particleAge / this.particleMaxAge;
      var3 = var3 * var3 * var3 * var3;
      return var2 * (1.0F - var3) + var3;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float var4 = (float)this.particleAge / this.particleMaxAge;
      var4 = -var4 + var4 * var4 * 2.0F;
      var4 = 1.0F - var4;
      this.posX = this.portalPosX + this.motionX * var4;
      this.posY = this.portalPosY + this.motionY * var4 + (1.0F - var4);
      this.posZ = this.portalPosZ + this.motionZ * var4;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityPortalFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
