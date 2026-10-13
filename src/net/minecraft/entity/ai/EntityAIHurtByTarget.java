package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
   private boolean entityCallsForHelp;
   private int revengeTimerOld;
   private final Class[] targetClasses;
   private static final long a = 51707186993422L;

   public EntityAIHurtByTarget(EntityCreature var1, boolean var2, Class... var3) {
      super(var1, false);
      this.entityCallsForHelp = var2;
      this.targetClasses = var3;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      int var4 = this.taskOwner.getRevengeTimer();
      return var4 != this.revengeTimerOld && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
      this.revengeTimerOld = this.taskOwner.getRevengeTimer();
      if (this.entityCallsForHelp) {
         double var4 = this.getTargetDistance();

         for (EntityCreature var7 : this.taskOwner
            .worldObj
            .getEntitiesWithinAABB(
               this.taskOwner.getClass(),
               new AxisAlignedBB(
                     this.taskOwner.posX,
                     this.taskOwner.posY,
                     this.taskOwner.posZ,
                     this.taskOwner.posX + 1.0,
                     this.taskOwner.posY + 1.0,
                     this.taskOwner.posZ + 1.0
                  )
                  .expand(var4, 10.0, var4)
            )) {
            if (this.taskOwner != var7 && var7.getAttackTarget() == null && !var7.isOnSameTeam(this.taskOwner.getAITarget())) {
               boolean var8 = false;

               for (Class var12 : this.targetClasses) {
                  if (var7.getClass() == var12) {
                     var8 = true;
                     break;
                  }
               }

               if (!var8) {
                  this.setEntityAttackTarget(var7, this.taskOwner.getAITarget());
               }
            }
         }
      }

      super.startExecuting();
   }

   public void setEntityAttackTarget(EntityCreature var1, EntityLivingBase var2) {
      var1.setAttackTarget(var2);
   }
}
