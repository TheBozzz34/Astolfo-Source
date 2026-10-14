package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityEnchantmentTableParticleFX extends EntityFX {
   private float field_70565_a;
   private double coordX;
   private double coordY;
   private double coordZ;
   private static final long ab = 75907514823548L;

   public EntityEnchantmentTableParticleFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
      this.coordX = xCoordIn;
      this.coordY = yCoordIn;
      this.coordZ = zCoordIn;
      this.posX = this.prevPosX = xCoordIn + xSpeedIn;
      this.posY = this.prevPosY = yCoordIn + ySpeedIn;
      this.posZ = this.prevPosZ = zCoordIn + zSpeedIn;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.field_70565_a = this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
      this.particleGreen *= 0.9F;
      this.particleRed *= 0.9F;
      this.particleMaxAge = (int)(Math.random() * 10.0) + 30;
      this.noClip = true;
      this.setParticleTextureIndex((int)(Math.random() * 26.0 + 1.0 + 224.0));
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      int i = super.getBrightnessForRender(partialTicks);
      float f = (float)this.particleAge / this.particleMaxAge;
      f *= f;
      f *= f;
      int j = i & 0xFF;
      int k = i >> 16 & 0xFF;
      k += (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   @Override
   public float getBrightness(float partialTicks) {
      float f = super.getBrightness(partialTicks);
      float g = (float)this.particleAge / this.particleMaxAge;
      g *= g;
      g *= g;
      return f * (1.0F - g) + g;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float f = (float)this.particleAge / this.particleMaxAge;
      f = 1.0F - f;
      float g = 1.0F - f;
      g *= g;
      g *= g;
      this.posX = this.coordX + this.motionX * f;
      this.posY = this.coordY + this.motionY * f - g * 1.2F;
      this.posZ = this.coordZ + this.motionZ * f;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }
   }

   public static class EnchantmentTable implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityEnchantmentTableParticleFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
