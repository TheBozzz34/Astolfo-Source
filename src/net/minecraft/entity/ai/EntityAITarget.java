package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase {
   public final EntityCreature taskOwner;
   public boolean shouldCheckSight;
   private boolean nearbyOnly;
   private int targetSearchStatus;
   private int targetSearchDelay;
   private int targetUnseenTicks;
   private static final long b = 20786316917796L;

   public EntityAITarget(EntityCreature creature, boolean checkSight) {
      this(creature, checkSight, false);
   }

   public EntityAITarget(EntityCreature creature, boolean checkSight, boolean onlyNearby) {
      this.taskOwner = creature;
      this.shouldCheckSight = checkSight;
      this.nearbyOnly = onlyNearby;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase entityLivingBase = this.taskOwner.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      if (!entityLivingBase.isEntityAlive()) {
         return false;
      }

      Team team = this.taskOwner.getTeam();
      Team team2 = entityLivingBase.getTeam();
      if (team != null && team2 == team) {
         return false;
      }

      double d = this.getTargetDistance();
      if (this.taskOwner.getDistanceSqToEntity(entityLivingBase) > d * d) {
         return false;
      }

      if (this.shouldCheckSight) {
         if (this.taskOwner.getEntitySenses().canSee(entityLivingBase)) {
            this.targetUnseenTicks = 0;
         } else if (++this.targetUnseenTicks > 60) {
            return false;
         }
      }

      return !(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).capabilities.disableDamage;
   }

   public double getTargetDistance() {
      IAttributeInstance iAttributeInstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
      return iAttributeInstance == null ? 16.0 : iAttributeInstance.getAttributeValue();
   }

   @Override
   public void startExecuting() {
      this.targetSearchStatus = 0;
      this.targetSearchDelay = 0;
      this.targetUnseenTicks = 0;
   }

   @Override
   public void resetTask() {
      this.taskOwner.setAttackTarget((EntityLivingBase)null);
   }

   public static boolean isSuitableTarget(EntityLiving attacker, EntityLivingBase target, boolean includeInvincibles, boolean checkSight) {
      if (target == null) {
         return false;
      }

      if (target == attacker) {
         return false;
      }

      if (!target.isEntityAlive()) {
         return false;
      }

      if (!attacker.canAttackClass((Class<? extends EntityLivingBase>)target.getClass())) {
         return false;
      }

      Team team = attacker.getTeam();
      Team team2 = target.getTeam();
      if (team != null && team2 == team) {
         return false;
      }

      if (attacker instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)attacker).getOwnerId())) {
         if (target instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)target).getOwnerId())) {
            return false;
         }

         if (target == ((IEntityOwnable)attacker).getOwner()) {
            return false;
         }
      } else if (target instanceof EntityPlayer && !includeInvincibles && ((EntityPlayer)target).capabilities.disableDamage) {
         return false;
      }

      return !checkSight || attacker.getEntitySenses().canSee(target);
   }

   public boolean isSuitableTarget(EntityLivingBase target, boolean includeInvincibles) {
      if (!isSuitableTarget(this.taskOwner, target, includeInvincibles, this.shouldCheckSight)) {
         return false;
      }

      if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(target))) {
         return false;
      }

      if (this.nearbyOnly) {
         if (--this.targetSearchDelay <= 0) {
            this.targetSearchStatus = 0;
         }

         if (this.targetSearchStatus == 0) {
            this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
         }

         if (this.targetSearchStatus == 2) {
            return false;
         }
      }

      return true;
   }

   private boolean canEasilyReach(EntityLivingBase target) {
      this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
      PathEntity pathEntity = this.taskOwner.getNavigator().getPathToEntityLiving(target);
      if (pathEntity == null) {
         return false;
      }

      PathPoint pathPoint = pathEntity.getFinalPathPoint();
      if (pathPoint == null) {
         return false;
      }

      int i = pathPoint.xCoord - MathHelper.floor_double(target.posX);
      int j = pathPoint.zCoord - MathHelper.floor_double(target.posZ);
      return i * i + j * j <= 2.25;
   }
}
