package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFlameFX extends EntityFX {
   private float flameScale;
   private static final long ab = 105404034110430L;

   public EntityFlameFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.motionX = this.motionX * 0.01F + var8;
      this.motionY = this.motionY * 0.01F + var10;
      this.motionZ = this.motionZ * 0.01F + var12;
      this.posX = this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
      this.posY = this.posY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
      this.posZ = this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
      this.flameScale = this.particleScale;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
      this.noClip = true;
      this.setParticleTextureIndex(48);
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (this.particleAge + var3) / this.particleMaxAge;
      this.particleScale = this.flameScale * (1.0F - var9 * var9 * 0.5F);
      super.renderParticle(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public int getBrightnessForRender(float var1) {
      float var5 = (this.particleAge + var1) / this.particleMaxAge;
      var5 = MathHelper.clamp_float(var5, 0.0F, 1.0F);
      int var6 = super.getBrightnessForRender(var1);
      int var7 = var6 & 0xFF;
      int var8 = var6 >> 16 & 0xFF;
      var7 += (int)(var5 * 15.0F * 16.0F);
      if (var7 > 240) {
         var7 = 240;
      }

      return var7 | var8 << 16;
   }

   @Override
   public float getBrightness(float var1) {
      float var2 = (this.particleAge + var1) / this.particleMaxAge;
      var2 = MathHelper.clamp_float(var2, 0.0F, 1.0F);
      float var3 = super.getBrightness(var1);
      return var3 * var2 + (1.0F - var2);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.96F;
      this.motionY *= 0.96F;
      this.motionZ *= 0.96F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityFlameFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
