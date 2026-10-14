package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
   public EntityTameable theDefendingTameable;
   public EntityLivingBase theOwnerAttacker;
   private int field_142051_e;
   private static final long a = 65458153212031L;

   public EntityAIOwnerHurtByTarget(EntityTameable theDefendingTameableIn) {
      super(theDefendingTameableIn, false);
      this.theDefendingTameable = theDefendingTameableIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theDefendingTameable.isTamed()) {
         return false;
      }

      EntityLivingBase entityLivingBase = this.theDefendingTameable.getOwner();
      if (entityLivingBase == null) {
         return false;
      }

      this.theOwnerAttacker = entityLivingBase.getAITarget();
      int i = entityLivingBase.getRevengeTimer();
      return i != this.field_142051_e
         && this.isSuitableTarget(this.theOwnerAttacker, false)
         && this.theDefendingTameable.shouldAttackEntity(this.theOwnerAttacker, entityLivingBase);
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theOwnerAttacker);
      EntityLivingBase entityLivingBase = this.theDefendingTameable.getOwner();
      if (entityLivingBase != null) {
         this.field_142051_e = entityLivingBase.getRevengeTimer();
      }

      super.startExecuting();
   }
}
