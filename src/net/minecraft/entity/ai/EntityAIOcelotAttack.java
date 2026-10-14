package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase {
   public World theWorld;
   public EntityLiving theEntity;
   public EntityLivingBase theVictim;
   public int attackCountdown;

   public EntityAIOcelotAttack(EntityLiving theEntityIn) {
      this.theEntity = theEntityIn;
      this.theWorld = theEntityIn.worldObj;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entityLivingBase = this.theEntity.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      this.theVictim = entityLivingBase;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.theVictim.isEntityAlive()
         ? false
         : (this.theEntity.getDistanceSqToEntity(this.theVictim) > 225.0 ? false : !this.theEntity.getNavigator().noPath() || this.shouldExecute());
   }

   @Override
   public void resetTask() {
      this.theVictim = null;
      this.theEntity.getNavigator().clearPathEntity();
   }

   @Override
   public void updateTask() {
      this.theEntity.getLookHelper().setLookPositionWithEntity(this.theVictim, 30.0F, 30.0F);
      double d = this.theEntity.width * 2.0F * this.theEntity.width * 2.0F;
      double e = this.theEntity.getDistanceSq(this.theVictim.posX, this.theVictim.getEntityBoundingBox().minY, this.theVictim.posZ);
      double d2 = 0.8;
      if (e > d && e < 16.0) {
         d2 = 1.33;
      } else if (e < 225.0) {
         d2 = 0.6;
      }

      this.theEntity.getNavigator().tryMoveToEntityLiving(this.theVictim, d2);
      this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
      if (e <= d && this.attackCountdown <= 0) {
         this.attackCountdown = 20;
         this.theEntity.attackEntityAsMob(this.theVictim);
      }
   }
}
