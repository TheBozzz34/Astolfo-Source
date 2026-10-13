package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCrit2FX extends EntityFX {
   public float field_174839_a;
   private static final long ab = 50670306076521L;
   private static final long cb = -994973854663180223L;

   public EntityCrit2FX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6, var8, var10, var12, 1.0F);
   }

   public EntityCrit2FX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += var8 * 0.4;
      this.motionY += var10 * 0.4;
      this.motionZ += var12 * 0.4;
      this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.3F + 0.6F);
      this.particleScale *= 0.75F;
      this.particleScale *= var14;
      this.field_174839_a = this.particleScale;
      this.particleMaxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
      this.particleMaxAge = (int)(this.particleMaxAge * var14);
      this.noClip = false;
      this.setParticleTextureIndex((int)cb);
      this.onUpdate();
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (this.particleAge + var3) / this.particleMaxAge * 32.0F;
      var9 = MathHelper.clamp_float(var9, 0.0F, 1.0F);
      this.particleScale = this.field_174839_a * var9;
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

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.particleGreen = (float)(this.particleGreen * 0.96);
      this.particleBlue = (float)(this.particleBlue * 0.9);
      this.motionX *= 0.7F;
      this.motionY *= 0.7F;
      this.motionZ *= 0.7F;
      this.motionY -= 0.02F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityCrit2FX(var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class MagicFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntityCrit2FX var16 = new EntityCrit2FX(var2, var3, var5, var7, var9, var11, var13);
         var16.setRBGColorF(var16.getRedColorF() * 0.3F, var16.getGreenColorF() * 0.8F, var16.getBlueColorF());
         var16.nextTextureIndexX();
         return var16;
      }
   }
}
