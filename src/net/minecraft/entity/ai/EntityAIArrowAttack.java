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

   public EntityAIArrowAttack(IRangedAttackMob attacker, double movespeed, int i, float f) {
      this(attacker, movespeed, i, i, f);
   }

   public EntityAIArrowAttack(IRangedAttackMob attacker, double movespeed, int i, int maxAttackTime, float maxAttackDistanceIn) {
      if (!(attacker instanceof EntityLivingBase)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      }

      this.rangedAttackEntityHost = attacker;
      this.entityHost = (EntityLiving)attacker;
      this.entityMoveSpeed = movespeed;
      this.field_96561_g = i;
      this.maxRangedAttackTime = maxAttackTime;
      this.field_96562_i = maxAttackDistanceIn;
      this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entityLivingBase = this.entityHost.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      this.attackTarget = entityLivingBase;
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
      double d = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
      boolean bl = this.entityHost.getEntitySenses().canSee(this.attackTarget);
      if (bl) {
         this.field_75318_f++;
      } else {
         this.field_75318_f = 0;
      }

      if (d <= this.maxAttackDistance && this.field_75318_f >= 20) {
         this.entityHost.getNavigator().clearPathEntity();
      } else {
         this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
      }

      this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
      if (--this.rangedAttackTime == 0) {
         if (d > this.maxAttackDistance || !bl) {
            return;
         }

         float f = MathHelper.sqrt_double(d) / this.field_96562_i;
         float g = MathHelper.clamp_float(f, 0.1F, 1.0F);
         this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, g);
         this.rangedAttackTime = MathHelper.floor_float(f * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
      } else if (this.rangedAttackTime < 0) {
         float h = MathHelper.sqrt_double(d) / this.field_96562_i;
         this.rangedAttackTime = MathHelper.floor_float(h * (this.maxRangedAttackTime - this.field_96561_g) + this.field_96561_g);
      }
   }
}
