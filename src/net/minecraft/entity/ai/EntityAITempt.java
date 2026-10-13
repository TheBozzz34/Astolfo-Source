package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAITempt extends EntityAIBase {
   private EntityCreature temptedEntity;
   private double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double pitch;
   private double yaw;
   private EntityPlayer temptingPlayer;
   private int delayTemptCounter;
   private boolean isRunning;
   private Item temptItem;
   private boolean scaredByPlayerMovement;
   private boolean avoidWater;
   private static final long a = 37570715784492L;
   private static final long e = 5379365337588301924L;

   public EntityAITempt(EntityCreature var1, double var2, Item var4, boolean var5) {
      this.temptedEntity = var1;
      this.speed = var2;
      this.temptItem = var4;
      this.scaredByPlayerMovement = var5;
      this.setMutexBits(3);
      if (!(var1.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      if (this.delayTemptCounter > 0) {
         this.delayTemptCounter--;
         return false;
      }

      this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, 10.0);
      if (this.temptingPlayer == null) {
         return false;
      }

      ItemStack var4 = this.temptingPlayer.getCurrentEquippedItem();
      return var4 == null ? false : var4.getItem() == this.temptItem;
   }

   @Override
   public boolean continueExecuting() {
      if (this.scaredByPlayerMovement) {
         if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0) {
            if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002) {
               return false;
            }

            if (Math.abs(this.temptingPlayer.rotationPitch - this.pitch) > 5.0 || Math.abs(this.temptingPlayer.rotationYaw - this.yaw) > 5.0) {
               return false;
            }
         } else {
            this.targetX = this.temptingPlayer.posX;
            this.targetY = this.temptingPlayer.posY;
            this.targetZ = this.temptingPlayer.posZ;
         }

         this.pitch = this.temptingPlayer.rotationPitch;
         this.yaw = this.temptingPlayer.rotationYaw;
      }

      return this.shouldExecute();
   }

   @Override
   public void startExecuting() {
      this.targetX = this.temptingPlayer.posX;
      this.targetY = this.temptingPlayer.posY;
      this.targetZ = this.temptingPlayer.posZ;
      this.isRunning = true;
      this.avoidWater = ((PathNavigateGround)this.temptedEntity.getNavigator()).S();
      ((PathNavigateGround)this.temptedEntity.getNavigator()).setAvoidsWater(false);
   }

   @Override
   public void resetTask() {
      this.temptingPlayer = null;
      this.temptedEntity.getNavigator().clearPathEntity();
      this.delayTemptCounter = (int)e;
      this.isRunning = false;
      ((PathNavigateGround)this.temptedEntity.getNavigator()).setAvoidsWater(this.avoidWater);
   }

   @Override
   public void updateTask() {
      this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, this.temptedEntity.getVerticalFaceSpeed());
      if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25) {
         this.temptedEntity.getNavigator().clearPathEntity();
      } else {
         this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
