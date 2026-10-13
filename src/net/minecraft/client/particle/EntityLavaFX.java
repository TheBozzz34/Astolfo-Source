package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityLavaFX extends EntityFX {
   private float lavaParticleScale;
   private static final long ab = 121112547858675L;

   public EntityLavaFX(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.motionX *= 0.8F;
      this.motionY *= 0.8F;
      this.motionZ *= 0.8F;
      this.motionY = this.rand.nextFloat() * 0.4F + 0.05F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleScale = this.particleScale * (this.rand.nextFloat() * 2.0F + 0.2F);
      this.lavaParticleScale = this.particleScale;
      this.particleMaxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = false;
      this.setParticleTextureIndex(49);
   }

   @Override
   public int getBrightnessForRender(float var1) {
      float var4 = (this.particleAge + var1) / this.particleMaxAge;
      var4 = MathHelper.clamp_float(var4, 0.0F, 1.0F);
      int var5 = super.getBrightnessForRender(var1);
      short var6 = 240;
      int var7 = var5 >> 16 & 0xFF;
      return var6 | var7 << 16;
   }

   @Override
   public float getBrightness(float var1) {
      return 1.0F;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (this.particleAge + var3) / this.particleMaxAge;
      this.particleScale = this.lavaParticleScale * (1.0F - var9 * var9);
      super.renderParticle(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      float var4 = (float)this.particleAge / this.particleMaxAge;
      if (this.rand.nextFloat() > var4) {
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
      }

      this.motionY -= 0.03;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.999F;
      this.motionY *= 0.999F;
      this.motionZ *= 0.999F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityLavaFX(var2, var3, var5, var7);
      }
   }
}
