package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {
   public World worldObj;
   public EntityCreature attacker;
   public int attackTick;
   public double speedTowardsTarget;
   public boolean longMemory;
   public PathEntity entityPathEntity;
   public Class<? extends Entity> classTarget;
   private int delayCounter;
   private double targetX;
   private double targetY;
   private double targetZ;
   private static final long b = 92094053540383L;

   public EntityAIAttackOnCollide(EntityCreature var1, Class<? extends Entity> var2, double var3, boolean var5) {
      this(var1, var3, var5);
      this.classTarget = var2;
   }

   public EntityAIAttackOnCollide(EntityCreature var1, double var2, boolean var4) {
      this.attacker = var1;
      this.worldObj = var1.worldObj;
      this.speedTowardsTarget = var2;
      this.longMemory = var4;
      this.setMutexBits(3);
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase var4 = this.attacker.getAttackTarget();
      if (var4 == null) {
         return false;
      }

      if (!var4.isEntityAlive()) {
         return false;
      }

      if (this.classTarget != null && !this.classTarget.isAssignableFrom(var4.getClass())) {
         return false;
      }

      this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(var4);
      return this.entityPathEntity != null;
   }

   @Override
   public boolean continueExecuting() {
      EntityLivingBase var4 = this.attacker.getAttackTarget();
      return var4 == null
         ? false
         : (
            !var4.isEntityAlive()
               ? false
               : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(var4)))
         );
   }

   @Override
   public void startExecuting() {
      this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
      this.delayCounter = 0;
   }

   @Override
   public void resetTask() {
      this.attacker.getNavigator().clearPathEntity();
   }

   @Override
   public void updateTask() {
      EntityLivingBase var4 = this.attacker.getAttackTarget();
      this.attacker.getLookHelper().setLookPositionWithEntity(var4, 30.0F, 30.0F);
      double var5 = this.attacker.getDistanceSq(var4.posX, var4.getEntityBoundingBox().minY, var4.posZ);
      double var7 = this.func_179512_a(var4);
      this.delayCounter--;
      if ((this.longMemory || this.attacker.getEntitySenses().canSee(var4))
         && this.delayCounter <= 0
         && (
            this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
               || var4.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0
               || this.attacker.getRNG().nextFloat() < 0.05F
         )) {
         this.targetX = var4.posX;
         this.targetY = var4.getEntityBoundingBox().minY;
         this.targetZ = var4.posZ;
         this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
         if (var5 > 1024.0) {
            this.delayCounter += 10;
         } else if (var5 > 256.0) {
            this.delayCounter += 5;
         }

         if (!this.attacker.getNavigator().tryMoveToEntityLiving(var4, this.speedTowardsTarget)) {
            this.delayCounter += 15;
         }
      }

      this.attackTick = Math.max(this.attackTick - 1, 0);
      if (var5 <= var7 && this.attackTick <= 0) {
         this.attackTick = 20;
         if (this.attacker.getHeldItem() != null) {
            this.attacker.swingItem();
         }

         this.attacker.attackEntityAsMob(var4);
      }
   }

   public double func_179512_a(EntityLivingBase var1) {
      return this.attacker.width * 2.0F * this.attacker.width * 2.0F + var1.width;
   }
}
