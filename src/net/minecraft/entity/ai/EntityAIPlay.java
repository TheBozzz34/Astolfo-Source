package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.Vec3;

public class EntityAIPlay extends EntityAIBase {
   private EntityVillager villagerObj;
   private EntityLivingBase targetVillager;
   private double speed;
   private int playTime;
   private static final long a = 14407324811403L;

   public EntityAIPlay(EntityVillager var1, double var2) {
      this.villagerObj = var1;
      this.speed = var2;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (this.villagerObj.getGrowingAge() >= 0) {
         return false;
      }

      if (this.villagerObj.getRNG().nextInt(400) != 0) {
         return false;
      }

      List var4 = this.villagerObj.worldObj.getEntitiesWithinAABB(EntityVillager.class, this.villagerObj.getEntityBoundingBox().expand(6.0, 3.0, 6.0));
      double var5 = Double.MAX_VALUE;

      for (Object var8$raw : var4) {
         EntityVillager var8 = (EntityVillager)var8$raw;
         if (var8 != this.villagerObj && !var8.isPlaying() && var8.getGrowingAge() < 0) {
            double var9 = var8.getDistanceSqToEntity(this.villagerObj);
            if (var9 <= var5) {
               var5 = var9;
               this.targetVillager = var8;
            }
         }
      }

      if (this.targetVillager == null) {
         Vec3 var11 = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);
         if (var11 == null) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean continueExecuting() {
      return this.playTime > 0;
   }

   @Override
   public void startExecuting() {
      if (this.targetVillager != null) {
         this.villagerObj.setPlaying(true);
      }

      this.playTime = 1000;
   }

   @Override
   public void resetTask() {
      this.villagerObj.setPlaying(false);
      this.targetVillager = null;
   }

   @Override
   public void updateTask() {
      this.playTime--;
      if (this.targetVillager != null) {
         if (this.villagerObj.getDistanceSqToEntity(this.targetVillager) > 4.0) {
            this.villagerObj.getNavigator().tryMoveToEntityLiving(this.targetVillager, this.speed);
         }
      } else if (this.villagerObj.getNavigator().noPath()) {
         Vec3 var4 = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);
         if (var4 == null) {
            return;
         }

         this.villagerObj.getNavigator().tryMoveToXYZ(var4.xCoord, var4.yCoord, var4.zCoord, this.speed);
      }
   }
}
