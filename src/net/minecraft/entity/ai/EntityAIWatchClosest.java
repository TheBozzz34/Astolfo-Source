package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase {
   public EntityLiving theWatcher;
   public Entity closestEntity;
   public float maxDistanceForPlayer;
   private int lookTime;
   private float chance;
   public Class<? extends Entity> watchedClass;
   private static final long b = 58930012790027L;

   public EntityAIWatchClosest(EntityLiving var1, Class<? extends Entity> var2, float var3) {
      this.theWatcher = var1;
      this.watchedClass = var2;
      this.maxDistanceForPlayer = var3;
      this.chance = 0.02F;
      this.setMutexBits(2);
   }

   public EntityAIWatchClosest(EntityLiving var1, Class<? extends Entity> var2, float var3, float var4) {
      this.theWatcher = var1;
      this.watchedClass = var2;
      this.maxDistanceForPlayer = var3;
      this.chance = var4;
      this.setMutexBits(2);
   }

   @Override
   public boolean shouldExecute() {
      if (this.theWatcher.getRNG().nextFloat() >= this.chance) {
         return false;
      }

      if (this.theWatcher.getAttackTarget() != null) {
         this.closestEntity = this.theWatcher.getAttackTarget();
      }

      if (this.watchedClass == EntityPlayer.class) {
         this.closestEntity = this.theWatcher.worldObj.getClosestPlayerToEntity(this.theWatcher, this.maxDistanceForPlayer);
      } else {
         this.closestEntity = this.theWatcher
            .worldObj
            .findNearestEntityWithinAABB(
               this.watchedClass, this.theWatcher.getEntityBoundingBox().expand(this.maxDistanceForPlayer, 3.0, this.maxDistanceForPlayer), this.theWatcher
            );
      }

      return this.closestEntity != null;
   }

   @Override
   public boolean continueExecuting() {
      return !this.closestEntity.isEntityAlive()
         ? false
         : (this.theWatcher.getDistanceSqToEntity(this.closestEntity) > this.maxDistanceForPlayer * this.maxDistanceForPlayer ? false : this.lookTime > 0);
   }

   @Override
   public void startExecuting() {
      this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
   }

   @Override
   public void resetTask() {
      this.closestEntity = null;
   }

   @Override
   public void updateTask() {
      this.theWatcher
         .getLookHelper()
         .setLookPosition(
            this.closestEntity.posX,
            this.closestEntity.posY + this.closestEntity.getEyeHeight(),
            this.closestEntity.posZ,
            10.0F,
            this.theWatcher.getVerticalFaceSpeed()
         );
      this.lookTime--;
   }
}
