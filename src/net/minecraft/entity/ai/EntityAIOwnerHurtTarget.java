package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
   public EntityTameable theEntityTameable;
   public EntityLivingBase theTarget;
   private int field_142050_e;

   public EntityAIOwnerHurtTarget(EntityTameable theEntityTameableIn) {
      super(theEntityTameableIn, false);
      this.theEntityTameable = theEntityTameableIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theEntityTameable.isTamed()) {
         return false;
      }

      EntityLivingBase entityLivingBase = this.theEntityTameable.getOwner();
      if (entityLivingBase == null) {
         return false;
      }

      this.theTarget = entityLivingBase.getLastAttacker();
      int i = entityLivingBase.getLastAttackerTime();
      return i != this.field_142050_e && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.shouldAttackEntity(this.theTarget, entityLivingBase);
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theTarget);
      EntityLivingBase entityLivingBase = this.theEntityTameable.getOwner();
      if (entityLivingBase != null) {
         this.field_142050_e = entityLivingBase.getLastAttackerTime();
      }

      super.startExecuting();
   }
}
