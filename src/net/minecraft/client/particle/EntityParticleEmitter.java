package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX {
   private Entity attachedEntity;
   private int age;
   private int lifetime;
   private EnumParticleTypes particleTypes;
   private static final long ab = 98676500082485L;

   public EntityParticleEmitter(World var1, Entity var2, EnumParticleTypes var3) {
      super(var1, var2.posX, var2.getEntityBoundingBox().minY + var2.height / 2.0F, var2.posZ, var2.motionX, var2.motionY, var2.motionZ);
      this.attachedEntity = var2;
      this.lifetime = 3;
      this.particleTypes = var3;
      this.onUpdate();
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   @Override
   public void onUpdate() {
      for (int var4 = 0; var4 < 16; var4++) {
         double var5 = this.rand.nextFloat() * 2.0F - 1.0F;
         double var7 = this.rand.nextFloat() * 2.0F - 1.0F;
         double var9 = this.rand.nextFloat() * 2.0F - 1.0F;
         if (var5 * var5 + var7 * var7 + var9 * var9 <= 1.0) {
            double var11 = this.attachedEntity.posX + var5 * this.attachedEntity.width / 4.0;
            double var13 = this.attachedEntity.getEntityBoundingBox().minY + this.attachedEntity.height / 2.0F + var7 * this.attachedEntity.height / 4.0;
            double var15 = this.attachedEntity.posZ + var9 * this.attachedEntity.width / 4.0;
            this.worldObj.spawnParticle(this.particleTypes, false, var11, var13, var15, var5, var7 + 0.2, var9);
         }
      }

      this.age++;
      if (this.age >= this.lifetime) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }
}
