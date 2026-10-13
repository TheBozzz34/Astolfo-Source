package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsTarget extends EntityAIBase {
   private EntityCreature theEntity;
   private EntityLivingBase targetEntity;
   private double movePosX;
   private double movePosY;
   private double movePosZ;
   private double speed;
   private float maxTargetDistance;
   private static final long a = 109585642635975L;

   public EntityAIMoveTowardsTarget(EntityCreature var1, double var2, float var4) {
      this.theEntity = var1;
      this.speed = var2;
      this.maxTargetDistance = var4;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      this.targetEntity = this.theEntity.getAttackTarget();
      if (this.targetEntity == null) {
         return false;
      }

      if (this.targetEntity.getDistanceSqToEntity(this.theEntity) > this.maxTargetDistance * this.maxTargetDistance) {
         return false;
      }

      Vec3 var4 = RandomPositionGenerator.findRandomTargetBlockTowards(
         this.theEntity, 16, 7, new Vec3(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ)
      );
      if (var4 == null) {
         return false;
      }

      this.movePosX = var4.xCoord;
      this.movePosY = var4.yCoord;
      this.movePosZ = var4.zCoord;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.theEntity.getNavigator().noPath()
         && this.targetEntity.isEntityAlive()
         && this.targetEntity.getDistanceSqToEntity(this.theEntity) < this.maxTargetDistance * this.maxTargetDistance;
   }

   @Override
   public void resetTask() {
      this.targetEntity = null;
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
   }
}
