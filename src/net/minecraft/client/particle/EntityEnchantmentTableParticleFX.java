package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityEnchantmentTableParticleFX extends EntityFX {
   private float field_70565_a;
   private double coordX;
   private double coordY;
   private double coordZ;
   private static final long ab = 75907514823548L;

   public EntityEnchantmentTableParticleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.motionX = var8;
      this.motionY = var10;
      this.motionZ = var12;
      this.coordX = var2;
      this.coordY = var4;
      this.coordZ = var6;
      this.posX = this.prevPosX = var2 + var8;
      this.posY = this.prevPosY = var4 + var10;
      this.posZ = this.prevPosZ = var6 + var12;
      float var16 = this.rand.nextFloat() * 0.6F + 0.4F;
      this.field_70565_a = this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F * var16;
      this.particleGreen *= 0.9F;
      this.particleRed *= 0.9F;
      this.particleMaxAge = (int)(Math.random() * 10.0) + 30;
      this.noClip = true;
      this.setParticleTextureIndex((int)(Math.random() * 26.0 + 1.0 + 224.0));
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
      var3 *= var3;
      var3 *= var3;
      return var2 * (1.0F - var3) + var3;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float var4 = (float)this.particleAge / this.particleMaxAge;
      var4 = 1.0F - var4;
      float var5 = 1.0F - var4;
      var5 *= var5;
      var5 *= var5;
      this.posX = this.coordX + this.motionX * var4;
      this.posY = this.coordY + this.motionY * var4 - var5 * 1.2F;
      this.posZ = this.coordZ + this.motionZ * var4;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }
   }

   public static class EnchantmentTable implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityEnchantmentTableParticleFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
