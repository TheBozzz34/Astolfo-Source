package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityAuraFX extends EntityFX {
   private static final long ab = 66706792733843L;

   public EntityAuraFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      float var14 = this.rand.nextFloat() * 0.1F + 0.2F;
      this.particleRed = var14;
      this.particleGreen = var14;
      this.particleBlue = var14;
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
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityAuraFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class HappyVillagerFactory implements IParticleFactory {
      private static final long a = 97747470728048L;
      private static final long b = 1490818423008002130L;

      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntityAuraFX var18 = new EntityAuraFX(var2, var3, var5, var7, var9, var11, var13);
         var18.setParticleTextureIndex((int)b);
         var18.setRBGColorF(1.0F, 1.0F, 1.0F);
         return var18;
      }
   }
}
