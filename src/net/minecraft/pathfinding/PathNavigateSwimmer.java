package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate {
   public PathNavigateSwimmer(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   @Override
   public PathFinder getPathFinder() {
      return new PathFinder(new SwimNodeProcessor());
   }

   @Override
   public boolean canNavigate() {
      return this.isInLiquid();
   }

   @Override
   public Vec3 getEntityPosition() {
      return new Vec3(this.theEntity.posX, this.theEntity.posY + this.theEntity.height * 0.5, this.theEntity.posZ);
   }

   @Override
   public void pathFollow() {
      Vec3 vec3 = this.getEntityPosition();
      float f = this.theEntity.width * this.theEntity.width;
      byte b = 6;
      if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < f) {
         this.currentPath.incrementPathIndex();
      }

      for (int i = Math.min(this.currentPath.getCurrentPathIndex() + b, this.currentPath.getCurrentPathLength() - 1);
         i > this.currentPath.getCurrentPathIndex();
         i--
      ) {
         Vec3 vec32 = this.currentPath.getVectorFromIndex(this.theEntity, i);
         if (vec32.squareDistanceTo(vec3) <= 36.0 && this.isDirectPathBetweenPoints(vec3, vec32, 0, 0, 0)) {
            this.currentPath.setCurrentPathIndex(i);
            break;
         }
      }

      this.checkForStuck(vec3);
   }

   @Override
   public void removeSunnyPath() {
      super.removeSunnyPath();
   }

   @Override
   public boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
      MovingObjectPosition movingObjectPosition = this.worldObj
         .rayTraceBlocks(posVec31, new Vec3(posVec32.xCoord, posVec32.yCoord + this.theEntity.height * 0.5, posVec32.zCoord), false, true, false);
      return movingObjectPosition == null || movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
   }
}
