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

   public EntityFirework$SparkFX(World world, double d, double d2, double d3, double d4, double d5, double d6, EffectRenderer effectRenderer) {
      super(world, d, d2, d3);
      this.motionX = d4;
      this.motionY = d5;
      this.motionZ = d6;
      this.field_92047_az = effectRenderer;
      this.particleScale *= 0.75F;
      this.particleMaxAge = 48 + this.rand.nextInt(12);
      this.noClip = false;
   }

   public void setTrail(boolean trailIn) {
      this.trail = trailIn;
   }

   public void setTwinkle(boolean twinkleIn) {
      this.twinkle = twinkleIn;
   }

   public void setColour(int colour) {
      float f = ((colour & 0xFF0000) >> 16) / 255.0F;
      float g = ((colour & 0xFF00) >> 8) / 255.0F;
      float h = ((colour & 0xFF) >> 0) / 255.0F;
      float f2 = 1.0F;
      this.setRBGColorF(f * f2, g * f2, h * f2);
   }

   public void setFadeColour(int faceColour) {
      this.fadeColourRed = ((faceColour & 0xFF0000) >> 16) / 255.0F;
      this.fadeColourGreen = ((faceColour & 0xFF00) >> 8) / 255.0F;
      this.fadeColourBlue = ((faceColour & 0xFF) >> 0) / 255.0F;
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
      if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
         super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
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
         EntityFirework$SparkFX entityFirework$SparkFX = new EntityFirework$SparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.field_92047_az);
         entityFirework$SparkFX.setAlphaF(0.99F);
         entityFirework$SparkFX.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
         entityFirework$SparkFX.particleAge = entityFirework$SparkFX.particleMaxAge / 2;
         if (this.hasFadeColour) {
            entityFirework$SparkFX.hasFadeColour = true;
            entityFirework$SparkFX.fadeColourRed = this.fadeColourRed;
            entityFirework$SparkFX.fadeColourGreen = this.fadeColourGreen;
            entityFirework$SparkFX.fadeColourBlue = this.fadeColourBlue;
         }

         entityFirework$SparkFX.twinkle = this.twinkle;
         this.field_92047_az.addEffect(entityFirework$SparkFX);
      }
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 15728880;
   }

   @Override
   public float getBrightness(float partialTicks) {
      return 1.0F;
   }
}
