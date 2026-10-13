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

   public EntityAITarget(EntityCreature var1, boolean var2) {
      this(var1, var2, false);
   }

   public EntityAITarget(EntityCreature var1, boolean var2, boolean var3) {
      this.taskOwner = var1;
      this.shouldCheckSight = var2;
      this.nearbyOnly = var3;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase var4 = this.taskOwner.getAttackTarget();
      if (var4 == null) {
         return false;
      }

      if (!var4.isEntityAlive()) {
         return false;
      }

      Team var5 = this.taskOwner.getTeam();
      Team var6 = var4.getTeam();
      if (var5 != null && var6 == var5) {
         return false;
      }

      double var7 = this.getTargetDistance();
      if (this.taskOwner.getDistanceSqToEntity(var4) > var7 * var7) {
         return false;
      }

      if (this.shouldCheckSight) {
         if (this.taskOwner.getEntitySenses().canSee(var4)) {
            this.targetUnseenTicks = 0;
         } else if (++this.targetUnseenTicks > 60) {
            return false;
         }
      }

      return !(var4 instanceof EntityPlayer) || !((EntityPlayer)var4).capabilities.disableDamage;
   }

   public double getTargetDistance() {
      IAttributeInstance var4 = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
      return var4 == null ? 16.0 : var4.getAttributeValue();
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

   public static boolean isSuitableTarget(EntityLiving var0, EntityLivingBase var1, boolean var2, boolean var3) {
      if (var1 == null) {
         return false;
      }

      if (var1 == var0) {
         return false;
      }

      if (!var1.isEntityAlive()) {
         return false;
      }

      if (!var0.canAttackClass((Class<? extends EntityLivingBase>)var1.getClass())) {
         return false;
      }

      Team var7 = var0.getTeam();
      Team var8 = var1.getTeam();
      if (var7 != null && var8 == var7) {
         return false;
      }

      if (var0 instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)var0).getOwnerId())) {
         if (var1 instanceof IEntityOwnable && ((IEntityOwnable)var0).getOwnerId().equals(((IEntityOwnable)var1).getOwnerId())) {
            return false;
         }

         if (var1 == ((IEntityOwnable)var0).getOwner()) {
            return false;
         }
      } else if (var1 instanceof EntityPlayer && !var2 && ((EntityPlayer)var1).capabilities.disableDamage) {
         return false;
      }

      return !var3 || var0.getEntitySenses().canSee(var1);
   }

   public boolean isSuitableTarget(EntityLivingBase var1, boolean var2) {
      if (!isSuitableTarget(this.taskOwner, var1, var2, this.shouldCheckSight)) {
         return false;
      }

      if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(var1))) {
         return false;
      }

      if (this.nearbyOnly) {
         if (--this.targetSearchDelay <= 0) {
            this.targetSearchStatus = 0;
         }

         if (this.targetSearchStatus == 0) {
            this.targetSearchStatus = this.canEasilyReach(var1) ? 1 : 2;
         }

         if (this.targetSearchStatus == 2) {
            return false;
         }
      }

      return true;
   }

   private boolean canEasilyReach(EntityLivingBase var1) {
      this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
      PathEntity var5 = this.taskOwner.getNavigator().getPathToEntityLiving(var1);
      if (var5 == null) {
         return false;
      }

      PathPoint var6 = var5.getFinalPathPoint();
      if (var6 == null) {
         return false;
      }

      int var7 = var6.xCoord - MathHelper.floor_double(var1.posX);
      int var8 = var6.zCoord - MathHelper.floor_double(var1.posZ);
      return var7 * var7 + var8 * var8 <= 2.25;
   }
}
