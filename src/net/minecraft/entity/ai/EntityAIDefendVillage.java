package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.village.Village;

public class EntityAIDefendVillage extends EntityAITarget {
   public EntityIronGolem irongolem;
   public EntityLivingBase villageAgressorTarget;
   private static final long a = 16294140577205L;
   private static final long c = -2646858752476577772L;

   public EntityAIDefendVillage(EntityIronGolem ironGolemIn) {
      super(ironGolemIn, false, true);
      this.irongolem = ironGolemIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      Village village = this.irongolem.getVillage();
      if (village == null) {
         return false;
      }

      this.villageAgressorTarget = village.findNearestVillageAggressor(this.irongolem);
      if (this.villageAgressorTarget instanceof EntityCreeper) {
         return false;
      }

      if (!this.isSuitableTarget(this.villageAgressorTarget, false)) {
         if (this.taskOwner.getRNG().nextInt((int)c) == 0) {
            this.villageAgressorTarget = village.getNearestTargetPlayer(this.irongolem);
            return this.isSuitableTarget(this.villageAgressorTarget, false);
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public void startExecuting() {
      this.irongolem.setAttackTarget(this.villageAgressorTarget);
      super.startExecuting();
   }
}
