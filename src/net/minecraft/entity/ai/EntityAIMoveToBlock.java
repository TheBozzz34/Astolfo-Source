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
   private static final long c = 101872267221811L;

   public EntityAIMoveToBlock(EntityCreature var1, double var2, int var4) {
      this.theEntity = var1;
      this.movementSpeed = var2;
      this.searchLength = var4;
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
      int var4 = this.searchLength;
      BlockPos var6 = new BlockPos(this.theEntity);

      for (int var7 = 0; var7 <= 1; var7 = (byte)(var7 > 0 ? -var7 : 1 - var7)) {
         for (int var8 = 0; var8 < var4; var8++) {
            for (int var9 = 0; var9 <= var8; var9 = (byte)(var9 > 0 ? -var9 : 1 - var9)) {
               for (int var10 = (byte)(var9 < var8 && var9 > -var8 ? var8 : 0); var10 <= var8; var10 = (byte)(var10 > 0 ? -var10 : 1 - var10)) {
                  BlockPos var11 = var6.add(var9, var7 - 1, var10);
                  if (this.theEntity.isWithinHomeDistanceFromPosition(var11) && this.shouldMoveTo(this.theEntity.worldObj, var11)) {
                     this.destinationBlock = var11;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public abstract boolean shouldMoveTo(World var1, BlockPos var2);
}
