package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
   public EntityLiving leaper;
   public EntityLivingBase leapTarget;
   public float leapMotionY;
   private static final long a = 74954997067625L;

   public EntityAILeapAtTarget(EntityLiving leapingEntity, float leapMotionYIn) {
      this.leaper = leapingEntity;
      this.leapMotionY = leapMotionYIn;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      this.leapTarget = this.leaper.getAttackTarget();
      if (this.leapTarget == null) {
         return false;
      }

      double d = this.leaper.getDistanceSqToEntity(this.leapTarget);
      return !(d >= 4.0) || !(d <= 16.0) ? false : (!this.leaper.onGround ? false : this.leaper.getRNG().nextInt(5) == 0);
   }

   @Override
   public boolean continueExecuting() {
      return !this.leaper.onGround;
   }

   @Override
   public void startExecuting() {
      double d = this.leapTarget.posX - this.leaper.posX;
      double e = this.leapTarget.posZ - this.leaper.posZ;
      float f = MathHelper.sqrt_double(d * d + e * e);
      this.leaper.motionX = this.leaper.motionX + (d / f * 0.5 * 0.8F + this.leaper.motionX * 0.2F);
      this.leaper.motionZ = this.leaper.motionZ + (e / f * 0.5 * 0.8F + this.leaper.motionZ * 0.2F);
      this.leaper.motionY = this.leapMotionY;
   }
}
