package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIMoveToBlock extends EntityAIBase {
   private final EntityCreature theEntity;
   private final double movementSpeed;
   public int runDelay;
   private int timeoutCounter;
   private int field_179490_f;
   public BlockPos destinationBlock = BlockPos.ORIGIN;
   private boolean isAboveDestination;
   private int searchLength;

   public EntityAIMoveToBlock(EntityCreature creature, double speedIn, int length) {
      this.theEntity = creature;
      this.movementSpeed = speedIn;
      this.searchLength = length;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      if (this.runDelay > 0) {
         this.runDelay--;
         return false;
      } else {
         this.runDelay = 200 + this.theEntity.getRNG().nextInt(200);
         return this.searchForDestination();
      }
   }

   @Override
   public boolean continueExecuting() {
      return this.timeoutCounter >= -this.field_179490_f && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.theEntity.worldObj, this.destinationBlock);
   }

   @Override
   public void startExecuting() {
      this.theEntity
         .getNavigator()
         .tryMoveToXYZ(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5, this.movementSpeed);
      this.timeoutCounter = 0;
      this.field_179490_f = this.theEntity.getRNG().nextInt(this.theEntity.getRNG().nextInt(1200) + 1200) + 1200;
   }

   @Override
   public void resetTask() {
   }

   @Override
   public void updateTask() {
      if (this.theEntity.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0) {
         this.isAboveDestination = false;
         this.timeoutCounter++;
         if (this.timeoutCounter % 40 == 0) {
            this.theEntity
               .getNavigator()
               .tryMoveToXYZ(this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5, this.movementSpeed);
         }
      } else {
         this.isAboveDestination = true;
         this.timeoutCounter--;
      }
   }

   public boolean getIsAboveDestination() {
      return this.isAboveDestination;
   }

   private boolean searchForDestination() {
      int i = this.searchLength;
      BlockPos blockPos = new BlockPos(this.theEntity);

      for (int j = 0; j <= 1; j = (byte)(j > 0 ? -j : 1 - j)) {
         for (int k = 0; k < i; k++) {
            for (int l = 0; l <= k; l = (byte)(l > 0 ? -l : 1 - l)) {
               for (int m = (byte)(l < k && l > -k ? k : 0); m <= k; m = (byte)(m > 0 ? -m : 1 - m)) {
                  BlockPos blockPos2 = blockPos.add(l, j - 1, m);
                  if (this.theEntity.isWithinHomeDistanceFromPosition(blockPos2) && this.shouldMoveTo(this.theEntity.worldObj, blockPos2)) {
                     this.destinationBlock = blockPos2;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public abstract boolean shouldMoveTo(World worldIn, BlockPos pos);
}
