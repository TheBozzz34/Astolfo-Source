package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PathNavigateClimber extends PathNavigateGround {
   private BlockPos targetPosition;
   private static final long b = 132614594248554L;

   public PathNavigateClimber(EntityLiving var1, World var2) {
      super(var1, var2);
   }

   @Override
   public PathEntity getPathToPos(BlockPos var1) {
      this.targetPosition = var1;
      return super.getPathToPos(var1);
   }

   @Override
   public PathEntity getPathToEntityLiving(Entity var1) {
      this.targetPosition = new BlockPos(var1);
      return super.getPathToEntityLiving(var1);
   }

   @Override
   public boolean tryMoveToEntityLiving(Entity var1, double var2) {
      PathEntity var7 = this.getPathToEntityLiving(var1);
      if (var7 != null) {
         return this.setPath(var7, var2);
      }

      this.targetPosition = new BlockPos(var1);
      this.speed = var2;
      return true;
   }

   @Override
   public void onUpdateNavigation() {
      if (!this.noPath()) {
         super.onUpdateNavigation();
      } else if (this.targetPosition != null) {
         double var4 = this.theEntity.width * this.theEntity.width;
         if (!(this.theEntity.getDistanceSqToCenter(this.targetPosition) >= var4)
            || !(this.theEntity.posY <= this.targetPosition.getY())
               && !(
                  this.theEntity
                        .getDistanceSqToCenter(
                           new BlockPos(this.targetPosition.getX(), MathHelper.floor_double(this.theEntity.posY), this.targetPosition.getZ())
                        )
                     >= var4
               )) {
            this.targetPosition = null;
         } else {
            this.theEntity.getMoveHelper().setMoveTo(this.targetPosition.getX(), this.targetPosition.getY(), this.targetPosition.getZ(), this.speed);
         }
      }
   }
}
