package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;

public abstract class EntityAIDoorInteract extends EntityAIBase {
   public EntityLiving theEntity;
   public BlockPos doorPosition = BlockPos.ORIGIN;
   public BlockDoor doorBlock;
   public boolean hasStoppedDoorInteraction;
   public float entityPositionX;
   public float entityPositionZ;
   private static final long b = 104376205134183L;

   public EntityAIDoorInteract(EntityLiving var1) {
      this.theEntity = var1;
      if (!(var1.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theEntity.isCollidedHorizontally) {
         return false;
      }

      PathNavigateGround var4 = (PathNavigateGround)this.theEntity.getNavigator();
      PathEntity var5 = var4.getPath();
      if (var5 != null && !var5.isFinished() && var4.getEnterDoors()) {
         for (int var6 = 0; var6 < Math.min(var5.getCurrentPathIndex() + 2, var5.getCurrentPathLength()); var6++) {
            PathPoint var7 = var5.getPathPointFromIndex(var6);
            this.doorPosition = new BlockPos(var7.xCoord, var7.yCoord + 1, var7.zCoord);
            if (this.theEntity.getDistanceSq(this.doorPosition.getX(), this.theEntity.posY, this.doorPosition.getZ()) <= 2.25) {
               this.doorBlock = this.getBlockDoor(this.doorPosition);
               if (this.doorBlock != null) {
                  return true;
               }
            }
         }

         this.doorPosition = new BlockPos(this.theEntity).up();
         this.doorBlock = this.getBlockDoor(this.doorPosition);
         return this.doorBlock != null;
      } else {
         return false;
      }
   }

   @Override
   public boolean continueExecuting() {
      return !this.hasStoppedDoorInteraction;
   }

   @Override
   public void startExecuting() {
      this.hasStoppedDoorInteraction = false;
      this.entityPositionX = (float)(this.doorPosition.getX() + 0.5F - this.theEntity.posX);
      this.entityPositionZ = (float)(this.doorPosition.getZ() + 0.5F - this.theEntity.posZ);
   }

   @Override
   public void updateTask() {
      float var3 = (float)(this.doorPosition.getX() + 0.5F - this.theEntity.posX);
      float var4 = (float)(this.doorPosition.getZ() + 0.5F - this.theEntity.posZ);
      float var5 = this.entityPositionX * var3 + this.entityPositionZ * var4;
      if (var5 < 0.0F) {
         this.hasStoppedDoorInteraction = true;
      }
   }

   private BlockDoor getBlockDoor(BlockPos var1) {
      Block var5 = this.theEntity.worldObj.getBlockState(var1).getBlock();
      return var5 instanceof BlockDoor && var5.getMaterial() == Material.wood ? (BlockDoor)var5 : null;
   }
}
