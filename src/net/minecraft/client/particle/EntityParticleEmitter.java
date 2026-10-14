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

   public EntityParticleEmitter(World worldIn, Entity entity, EnumParticleTypes particleTypesIn) {
      super(worldIn, entity.posX, entity.getEntityBoundingBox().minY + entity.height / 2.0F, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
      this.attachedEntity = entity;
      this.lifetime = 3;
      this.particleTypes = particleTypesIn;
      this.onUpdate();
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
   }

   @Override
   public void onUpdate() {
      for (int i = 0; i < 16; i++) {
         double d = this.rand.nextFloat() * 2.0F - 1.0F;
         double e = this.rand.nextFloat() * 2.0F - 1.0F;
         double d2 = this.rand.nextFloat() * 2.0F - 1.0F;
         if (d * d + e * e + d2 * d2 <= 1.0) {
            double d3 = this.attachedEntity.posX + d * this.attachedEntity.width / 4.0;
            double d4 = this.attachedEntity.getEntityBoundingBox().minY + this.attachedEntity.height / 2.0F + e * this.attachedEntity.height / 4.0;
            double d5 = this.attachedEntity.posZ + d2 * this.attachedEntity.width / 4.0;
            this.worldObj.spawnParticle(this.particleTypes, false, d3, d4, d5, d, e + 0.2, d2);
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
