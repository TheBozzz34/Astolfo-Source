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

   public EntityEnderPearl(World var1) {
      super(var1);
   }

   public EntityEnderPearl(World var1, EntityLivingBase var2) {
      super(var1, var2);
      this.field_181555_c = var2;
   }

   public EntityEnderPearl(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void onImpact(MovingObjectPosition var1) {
      EntityLivingBase var5 = this.getThrower();
      if (var1.entityHit != null) {
         if (var1.entityHit == this.field_181555_c) {
            return;
         }

         var1.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, var5), 0.0F);
      }

      for (int var6 = 0; var6 < (int)hb; var6++) {
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
         if (var5 instanceof EntityPlayerMP) {
            EntityPlayerMP var8 = (EntityPlayerMP)var5;
            if (var8.playerNetServerHandler.getNetworkManager().isChannelOpen() && var8.worldObj == this.worldObj && !var8.isPlayerSleeping()) {
               if (this.rand.nextFloat() < 0.05F && this.worldObj.getGameRules().getBoolean("doMobSpawning")) {
                  EntityEndermite var7 = new EntityEndermite(this.worldObj);
                  var7.setSpawnedByPlayer(true);
                  var7.setLocationAndAngles(var5.posX, var5.posY, var5.posZ, var5.rotationYaw, var5.rotationPitch);
                  this.worldObj.spawnEntityInWorld(var7);
               }

               if (var5.E()) {
                  var5.mountEntity((Entity)null);
               }

               var5.setPositionAndUpdate(this.posX, this.posY, this.posZ);
               var5.fallDistance = 0.0F;
               var5.attackEntityFrom(DamageSource.fall, 5.0F);
            }
         } else if (var5 != null) {
            var5.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            var5.fallDistance = 0.0F;
         }

         this.setDead();
      }
   }

   @Override
   public void onUpdate() {
      EntityLivingBase var4 = this.getThrower();
      if (var4 != null && var4 instanceof EntityPlayer && !var4.isEntityAlive()) {
         this.setDead();
      } else {
         super.onUpdate();
      }
   }
}
