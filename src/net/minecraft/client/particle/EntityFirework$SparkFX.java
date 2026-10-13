package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityFirework$SparkFX extends EntityFX {
   private int baseTextureIndex = 160;
   private boolean trail;
   private boolean twinkle;
   private final EffectRenderer field_92047_az;
   private float fadeColourRed;
   private float fadeColourGreen;
   private float fadeColourBlue;
   private boolean hasFadeColour;
   private static final long ab = 35166268065867L;

   public EntityFirework$SparkFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14) {
      super(var1, var2, var4, var6);
      this.motionX = var8;
      this.motionY = var10;
      this.motionZ = var12;
      this.field_92047_az = var14;
      this.particleScale *= 0.75F;
      this.particleMaxAge = 48 + this.rand.nextInt(12);
      this.noClip = false;
   }

   public void setTrail(boolean var1) {
      this.trail = var1;
   }

   public void setTwinkle(boolean var1) {
      this.twinkle = var1;
   }

   public void setColour(int var1) {
      float var4 = ((var1 & 0xFF0000) >> 16) / 255.0F;
      float var5 = ((var1 & 0xFF00) >> 8) / 255.0F;
      float var6 = ((var1 & 0xFF) >> 0) / 255.0F;
      float var7 = 1.0F;
      this.setRBGColorF(var4 * var7, var5 * var7, var6 * var7);
   }

   public void setFadeColour(int var1) {
      this.fadeColourRed = ((var1 & 0xFF0000) >> 16) / 255.0F;
      this.fadeColourGreen = ((var1 & 0xFF00) >> 8) / 255.0F;
      this.fadeColourBlue = ((var1 & 0xFF) >> 0) / 255.0F;
      this.hasFadeColour = true;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
         super.renderParticle(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      if (this.particleAge > this.particleMaxAge / 2) {
         this.setAlphaF(1.0F - ((float)this.particleAge - this.particleMaxAge / 2) / this.particleMaxAge);
         if (this.hasFadeColour) {
            this.particleRed = this.particleRed + (this.fadeColourRed - this.particleRed) * 0.2F;
            this.particleGreen = this.particleGreen + (this.fadeColourGreen - this.particleGreen) * 0.2F;
            this.particleBlue = this.particleBlue + (this.fadeColourBlue - this.particleBlue) * 0.2F;
         }
      }

      this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
      this.motionY -= 0.004;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.91F;
      this.motionY *= 0.91F;
      this.motionZ *= 0.91F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }

      if (this.trail && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
         EntityFirework$SparkFX var4 = new EntityFirework$SparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.field_92047_az);
         var4.setAlphaF(0.99F);
         var4.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
         var4.particleAge = var4.particleMaxAge / 2;
         if (this.hasFadeColour) {
            var4.hasFadeColour = true;
            var4.fadeColourRed = this.fadeColourRed;
            var4.fadeColourGreen = this.fadeColourGreen;
            var4.fadeColourBlue = this.fadeColourBlue;
         }

         var4.twinkle = this.twinkle;
         this.field_92047_az.addEffect(var4);
      }
   }

   @Override
   public int getBrightnessForRender(float var1) {
      return 15728880;
   }

   @Override
   public float getBrightness(float var1) {
      return 1.0F;
   }
}
