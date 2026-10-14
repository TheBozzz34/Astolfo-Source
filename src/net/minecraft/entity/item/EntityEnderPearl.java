package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable {
   public EntityLivingBase field_181555_c;
   private static final long ab = 116284785634040L;
   private static final long hb = 2154663317195456544L;

   public EntityEnderPearl(World worldIn) {
      super(worldIn);
   }

   public EntityEnderPearl(World worldIn, EntityLivingBase entityLivingBase) {
      super(worldIn, entityLivingBase);
      this.field_181555_c = entityLivingBase;
   }

   public EntityEnderPearl(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public void onImpact(MovingObjectPosition movingObjectPosition) {
      EntityLivingBase entityLivingBase = this.getThrower();
      if (movingObjectPosition.entityHit != null) {
         if (movingObjectPosition.entityHit == this.field_181555_c) {
            return;
         }

         movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, entityLivingBase), 0.0F);
      }

      for (int i = 0; i < (int)hb; i++) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.PORTAL,
               this.posX,
               this.posY + this.rand.nextDouble() * 2.0,
               this.posZ,
               this.rand.nextGaussian(),
               0.0,
               this.rand.nextGaussian()
            );
      }

      if (!this.worldObj.isRemote) {
         if (entityLivingBase instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityLivingBase;
            if (entityPlayerMP.playerNetServerHandler.getNetworkManager().isChannelOpen() && entityPlayerMP.worldObj == this.worldObj && !entityPlayerMP.isPlayerSleeping()) {
               if (this.rand.nextFloat() < 0.05F && this.worldObj.getGameRules().getBoolean("doMobSpawning")) {
                  EntityEndermite entityEndermite = new EntityEndermite(this.worldObj);
                  entityEndermite.setSpawnedByPlayer(true);
                  entityEndermite.setLocationAndAngles(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, entityLivingBase.rotationYaw, entityLivingBase.rotationPitch);
                  this.worldObj.spawnEntityInWorld(entityEndermite);
               }

               if (entityLivingBase.E()) {
                  entityLivingBase.mountEntity((Entity)null);
               }

               entityLivingBase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
               entityLivingBase.fallDistance = 0.0F;
               entityLivingBase.attackEntityFrom(DamageSource.fall, 5.0F);
            }
         } else if (entityLivingBase != null) {
            entityLivingBase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            entityLivingBase.fallDistance = 0.0F;
         }

         this.setDead();
      }
   }

   @Override
   public void onUpdate() {
      EntityLivingBase entityLivingBase = this.getThrower();
      if (entityLivingBase != null && entityLivingBase instanceof EntityPlayer && !entityLivingBase.isEntityAlive()) {
         this.setDead();
      } else {
         super.onUpdate();
      }
   }
}
