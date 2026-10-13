package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakDoor extends EntityAIDoorInteract {
   private int breakingTime;
   private int previousBreakProgress = -1;
   private static final long a = 123573205414797L;

   public EntityAIBreakDoor(EntityLiving var1) {
      super(var1);
   }

   @Override
   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      }

      if (!this.theEntity.worldObj.getGameRules().getBoolean("mobGriefing")) {
         return false;
      }

      BlockDoor var4 = this.doorBlock;
      return !BlockDoor.isOpen(this.theEntity.worldObj, this.doorPosition);
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   @Override
   public boolean continueExecuting() {
      double var4 = this.theEntity.getDistanceSq(this.doorPosition);
      if (this.breakingTime <= 240) {
         BlockDoor var7 = this.doorBlock;
         if (!BlockDoor.isOpen(this.theEntity.worldObj, this.doorPosition) && var4 < 4.0) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void resetTask() {
      super.resetTask();
      this.theEntity.worldObj.sendBlockBreakProgress(this.theEntity.getEntityId(), this.doorPosition, -1);
   }

   @Override
   public void updateTask() {
      super.updateTask();
      if (this.theEntity.getRNG().nextInt(20) == 0) {
         this.theEntity.worldObj.playAuxSFX(1010, this.doorPosition, 0);
      }

      this.breakingTime++;
      int var4 = (int)(this.breakingTime / 240.0F * 10.0F);
      if (var4 != this.previousBreakProgress) {
         this.theEntity.worldObj.sendBlockBreakProgress(this.theEntity.getEntityId(), this.doorPosition, var4);
         this.previousBreakProgress = var4;
      }

      if (this.breakingTime == 240 && this.theEntity.worldObj.getDifficulty() == EnumDifficulty.HARD) {
         this.theEntity.worldObj.setBlockToAir(this.doorPosition);
         this.theEntity.worldObj.playAuxSFX(1012, this.doorPosition, 0);
         this.theEntity.worldObj.playAuxSFX(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
      }
   }
}
