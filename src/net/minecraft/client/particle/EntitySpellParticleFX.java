package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySpellParticleFX extends EntityFX {
   private static final Random RANDOM = new Random();
   private int baseSpellTextureIndex = 128;
   private static final long ab = 43050016896991L;

   public EntitySpellParticleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.5 - RANDOM.nextDouble(), var10, 0.5 - RANDOM.nextDouble());
      this.motionY *= 0.2F;
      if (var8 == 0.0 && var12 == 0.0) {
         this.motionX *= 0.1F;
         this.motionZ *= 0.1F;
      }

      this.particleScale *= 0.75F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = false;
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (this.particleAge + var3) / this.particleMaxAge * 32.0F;
      var9 = MathHelper.clamp_float(var9, 0.0F, 1.0F);
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

      this.setParticleTextureIndex(this.baseSpellTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
      this.motionY += 0.004;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1;
         this.motionZ *= 1.1;
      }

      this.motionX *= 0.96F;
      this.motionY *= 0.96F;
      this.motionZ *= 0.96F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public void setBaseSpellTextureIndex(int var1) {
      this.baseSpellTextureIndex = var1;
   }

   public static class AmbientMobFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var16 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         var16.setAlphaF(0.15F);
         var16.setRBGColorF((float)var9, (float)var11, (float)var13);
         return var16;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class InstantFactory implements IParticleFactory {
      private static final long a = 13579469013033L;
      private static final long b = -8234851087085993840L;

      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var18 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         var18.setBaseSpellTextureIndex((int)b);
         return var18;
      }
   }

   public static class MobFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var16 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         var16.setRBGColorF((float)var9, (float)var11, (float)var13);
         return var16;
      }
   }

   public static class WitchFactory implements IParticleFactory {
      private static final long a = 55671489222971L;
      private static final long b = -7989356211055099760L;

      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var18 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         var18.setBaseSpellTextureIndex((int)b);
         float var19 = var2.rand.nextFloat() * 0.5F + 0.35F;
         var18.setRBGColorF(1.0F * var19, 0.0F * var19, 1.0F * var19);
         return var18;
      }
   }
}
