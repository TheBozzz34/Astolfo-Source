package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
   public EntityTameable theEntityTameable;
   public EntityLivingBase theTarget;
   private int field_142050_e;
   private static final long a = 1341423869390L;

   public EntityAIOwnerHurtTarget(EntityTameable var1) {
      super(var1, false);
      this.theEntityTameable = var1;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theEntityTameable.isTamed()) {
         return false;
      }

      EntityLivingBase var4 = this.theEntityTameable.getOwner();
      if (var4 == null) {
         return false;
      }

      this.theTarget = var4.getLastAttacker();
      int var5 = var4.getLastAttackerTime();
      return var5 != this.field_142050_e && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.shouldAttackEntity(this.theTarget, var4);
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theTarget);
      EntityLivingBase var4 = this.theEntityTameable.getOwner();
      if (var4 != null) {
         this.field_142050_e = var4.getLastAttackerTime();
      }

      super.startExecuting();
   }
}
