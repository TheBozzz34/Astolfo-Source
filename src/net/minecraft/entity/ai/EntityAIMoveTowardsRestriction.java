package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase {
   private EntityCreature theEntity;
   private double movePosX;
   private double movePosY;
   private double movePosZ;
   private double movementSpeed;
   private static final long a = 35204732029101L;

   public EntityAIMoveTowardsRestriction(EntityCreature var1, double var2) {
      this.theEntity = var1;
      this.movementSpeed = var2;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (this.theEntity.isWithinHomeDistanceCurrentPosition()) {
         return false;
      }

      BlockPos var4 = this.theEntity.getHomePosition();
      Vec3 var5 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vec3(var4.getX(), var4.getY(), var4.getZ()));
      if (var5 == null) {
         return false;
      }

      this.movePosX = var5.xCoord;
      this.movePosY = var5.yCoord;
      this.movePosZ = var5.zCoord;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.theEntity.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
   }
}
