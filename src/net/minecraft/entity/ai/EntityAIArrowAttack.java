package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.MathHelper;

public class EntityAIArrowAttack extends EntityAIBase {
   private final EntityLiving entityHost;
   private final IRangedAttackMob rangedAttackEntityHost;
   private EntityLivingBase attackTarget;
   private int rangedAttackTime = -1;
   private double entityMoveSpeed;
   private int field_75318_f;
   private int field_96561_g;
   private int maxRangedAttackTime;
   private float field_96562_i;
   private float maxAttackDistance;
   private static final long a = 9568125403871L;
   private static final long d = 2284995487067013140L;

   public EntityAIArrowAttack(IRangedAttackMob var1, double var2, int var4, float var5) {
      this(var1, var2, var4, var4, var5);
   }

   public EntityAIArrowAttack(IRangedAttackMob var1, double var2, int var4, int var5, float var6) {
      if (!(var1 instanceof EntityLivingBase)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      }

      this.rangedAttackEntityHost = var1;
      this.entityHost = (EntityLiving)var1;
      this.entityMoveSpeed = var2;
      this.field_96561_g = var4;
      this.maxRangedAttackTime = var5;
      this.field_96562_i = var6;
      this.maxAttackDistance = var6 * var6;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase var4 = this.entityHost.getAttackTarget();
      if (var4 == null) {
         return false;
      }

      this.attackTarget = var4;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
   }

   @Override
   public void resetTask() {
      this.attackTarget = null;
      this.field_75318_f = 0;
      this.rangedAttackTime = -1;
   }

   @Override
   public void updateTask() {
      double var4 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
      boolean var6 = this.entityHost.getEntitySenses().canSee(this.attackTarget);
      if (var6) {
         this.field_75318_f++;
      } else {
         this.field_75318_f = 0;
      }

      if (var4 <= this.maxAttackDistance && this.field_75318_f >= (int)d) {
         this.entityHost.getNavigator().clearPathEntity();
      } else {
         this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
      }

      this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
      if (--this.rangedAttackTime == 0) {
         if (var4 > this.maxAttackDistance || !var6) {
            return;
         }

         float var7 = MathHelper.sqrt_double(var4) / this.field_96562_i;
         float var8 = MathHelper.clamp_float(var7, 0.1F, 1.0F);
         this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, var8);
         this.rangedAttackTime = MathHelper.floor_float(var7 * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
      } else if (this.rangedAttackTime < 0) {
         float var9 = MathHelper.sqrt_double(var4) / this.field_96562_i;
         this.rangedAttackTime = MathHelper.floor_float(var9 * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
      }
   }
}
