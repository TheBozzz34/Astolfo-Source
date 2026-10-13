package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   public static final UUID FLEEING_SPEED_MODIFIER_UUID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
   public static final AttributeModifier FLEEING_SPEED_MODIFIER = new AttributeModifier(FLEEING_SPEED_MODIFIER_UUID, "Fleeing speed bonus", 2.0, 2)
      .setSaved(false);
   private BlockPos homePosition = BlockPos.ORIGIN;
   private float maximumHomeDistance = -1.0F;
   private EntityAIBase aiBase = new EntityAIMoveTowardsRestriction(this, 1.0);
   private boolean isMovementAITaskSet;
   private static final long bb = 85327421829967L;

   public EntityCreature(World var1) {
      super(var1);
   }

   public float getBlockPathWeight(BlockPos var1) {
      return 0.0F;
   }

   @Override
   public boolean getCanSpawnHere() {
      return super.getCanSpawnHere() && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F;
   }

   public boolean u() {
      return !this.navigator.noPath();
   }

   public boolean isWithinHomeDistanceCurrentPosition() {
      return this.isWithinHomeDistanceFromPosition(new BlockPos(this));
   }

   public boolean isWithinHomeDistanceFromPosition(BlockPos var1) {
      return this.maximumHomeDistance == -1.0F ? true : this.homePosition.distanceSq(var1) < this.maximumHomeDistance * this.maximumHomeDistance;
   }

   public void setHomePosAndDistance(BlockPos var1, int var2) {
      this.homePosition = var1;
      this.maximumHomeDistance = var2;
   }

   public BlockPos getHomePosition() {
      return this.homePosition;
   }

   public float getMaximumHomeDistance() {
      return this.maximumHomeDistance;
   }

   public void detachHome() {
      this.maximumHomeDistance = -1.0F;
   }

   public boolean hasHome() {
      return this.maximumHomeDistance != -1.0F;
   }

   @Override
   public void updateLeashedState() {
      super.updateLeashedState();
      if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().worldObj == this.worldObj) {
         Entity var4 = this.getLeashedToEntity();
         this.setHomePosAndDistance(new BlockPos((int)var4.posX, (int)var4.posY, (int)var4.posZ), 5);
         float var5 = this.getDistanceToEntity(var4);
         if (this instanceof EntityTameable && ((EntityTameable)this).isSitting()) {
            if (var5 > 10.0F) {
               this.clearLeashed(true, true);
            }

            return;
         }

         if (!this.isMovementAITaskSet) {
            this.tasks.addTask(2, this.aiBase);
            if (this.getNavigator() instanceof PathNavigateGround) {
               ((PathNavigateGround)this.getNavigator()).setAvoidsWater(false);
            }

            this.isMovementAITaskSet = true;
         }

         this.func_142017_o(var5);
         if (var5 > 4.0F) {
            this.getNavigator().tryMoveToEntityLiving(var4, 1.0);
         }

         if (var5 > 6.0F) {
            double var6 = (var4.posX - this.posX) / var5;
            double var8 = (var4.posY - this.posY) / var5;
            double var10 = (var4.posZ - this.posZ) / var5;
            this.motionX = this.motionX + var6 * Math.abs(var6) * 0.4;
            this.motionY = this.motionY + var8 * Math.abs(var8) * 0.4;
            this.motionZ = this.motionZ + var10 * Math.abs(var10) * 0.4;
         }

         if (var5 > 10.0F) {
            this.clearLeashed(true, true);
         }
      } else if (!this.getLeashed() && this.isMovementAITaskSet) {
         this.isMovementAITaskSet = false;
         this.tasks.removeTask(this.aiBase);
         if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
         }

         this.detachHome();
      }
   }

   public void func_142017_o(float var1) {
   }
}
