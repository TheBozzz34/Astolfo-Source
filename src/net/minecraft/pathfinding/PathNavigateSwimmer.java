package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate {
   private static final long b = 21694624065353L;
   private static final long c = -8874399540258537466L;

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
      int i = (int)c;
      if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < f) {
         this.currentPath.incrementPathIndex();
      }

      for (int j = Math.min(this.currentPath.getCurrentPathIndex() + i, this.currentPath.getCurrentPathLength() - 1);
         j > this.currentPath.getCurrentPathIndex();
         j--
      ) {
         Vec3 vec32 = this.currentPath.getVectorFromIndex(this.theEntity, j);
         if (vec32.squareDistanceTo(vec3) <= 36.0 && this.isDirectPathBetweenPoints(vec3, vec32, 0, 0, 0)) {
            this.currentPath.setCurrentPathIndex(j);
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
