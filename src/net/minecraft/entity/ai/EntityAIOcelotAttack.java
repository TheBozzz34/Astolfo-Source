package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase {
   public World theWorld;
   public EntityLiving theEntity;
   public EntityLivingBase theVictim;
   public int attackCountdown;
   private static final long a = 6667810211042L;
   private static final long b = -6304892637681811436L;

   public EntityAIOcelotAttack(EntityLiving var1) {
      this.theEntity = var1;
      this.theWorld = var1.worldObj;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase var4 = this.theEntity.getAttackTarget();
      if (var4 == null) {
         return false;
      }

      this.theVictim = var4;
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
      double var4 = this.theEntity.width * 2.0F * this.theEntity.width * 2.0F;
      double var6 = this.theEntity.getDistanceSq(this.theVictim.posX, this.theVictim.getEntityBoundingBox().minY, this.theVictim.posZ);
      double var8 = 0.8;
      if (var6 > var4 && var6 < 16.0) {
         var8 = 1.33;
      } else if (var6 < 225.0) {
         var8 = 0.6;
      }

      this.theEntity.getNavigator().tryMoveToEntityLiving(this.theVictim, var8);
      this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
      if (var6 <= var4 && this.attackCountdown <= 0) {
         this.attackCountdown = (int)b;
         this.theEntity.attackEntityAsMob(this.theVictim);
      }
   }
}
