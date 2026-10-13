package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
   public EntityLiving leaper;
   public EntityLivingBase leapTarget;
   public float leapMotionY;
   private static final long a = 74954997067625L;

   public EntityAILeapAtTarget(EntityLiving var1, float var2) {
      this.leaper = var1;
      this.leapMotionY = var2;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      this.leapTarget = this.leaper.getAttackTarget();
      if (this.leapTarget == null) {
         return false;
      }

      double var4 = this.leaper.getDistanceSqToEntity(this.leapTarget);
      return !(var4 >= 4.0) || !(var4 <= 16.0) ? false : (!this.leaper.onGround ? false : this.leaper.getRNG().nextInt(5) == 0);
   }

   @Override
   public boolean continueExecuting() {
      return !this.leaper.onGround;
   }

   @Override
   public void startExecuting() {
      double var4 = this.leapTarget.posX - this.leaper.posX;
      double var6 = this.leapTarget.posZ - this.leaper.posZ;
      float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
      this.leaper.motionX = this.leaper.motionX + (var4 / var8 * 0.5 * 0.8F + this.leaper.motionX * 0.2F);
      this.leaper.motionZ = this.leaper.motionZ + (var6 / var8 * 0.5 * 0.8F + this.leaper.motionZ * 0.2F);
      this.leaper.motionY = this.leapMotionY;
   }
}
