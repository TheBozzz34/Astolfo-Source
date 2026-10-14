package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityAuraFX extends EntityFX {
   private static final long ab = 66706792733843L;

   public EntityAuraFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double speedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, speedIn);
      float f = this.rand.nextFloat() * 0.1F + 0.2F;
      this.particleRed = f;
      this.particleGreen = f;
      this.particleBlue = f;
      this.setParticleTextureIndex(0);
      this.setSize(0.02F, 0.02F);
      this.particleScale = this.particleScale * (this.rand.nextFloat() * 0.6F + 0.5F);
      this.motionX *= 0.02F;
      this.motionY *= 0.02F;
      this.motionZ *= 0.02F;
      this.particleMaxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = true;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.99;
      this.motionY *= 0.99;
      this.motionZ *= 0.99;
      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityAuraFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }

   public static class HappyVillagerFactory implements IParticleFactory {
      private static final long a = 97747470728048L;
      private static final long b = 1490818423008002130L;

      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         EntityAuraFX entityAuraFX = new EntityAuraFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
         entityAuraFX.setParticleTextureIndex((int)b);
         entityAuraFX.setRBGColorF(1.0F, 1.0F, 1.0F);
         return entityAuraFX;
      }
   }
}
