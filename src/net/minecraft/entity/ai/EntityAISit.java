package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISit extends EntityAIBase {
   private EntityTameable theEntity;
   private boolean isSitting;

   public EntityAISit(EntityTameable entityIn) {
      this.theEntity = entityIn;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theEntity.isTamed()) {
         return false;
      }

      if (this.theEntity.isInWater()) {
         return false;
      }

      if (!this.theEntity.onGround) {
         return false;
      }

      EntityLivingBase entityLivingBase = this.theEntity.getOwner();
      return entityLivingBase == null ? true : (this.theEntity.getDistanceSqToEntity(entityLivingBase) < 144.0 && entityLivingBase.getAITarget() != null ? false : this.isSitting);
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().clearPathEntity();
      this.theEntity.setSitting(true);
   }

   @Override
   public void resetTask() {
      this.theEntity.setSitting(false);
   }

   public void setSitting(boolean sitting) {
      this.isSitting = sitting;
   }
}
