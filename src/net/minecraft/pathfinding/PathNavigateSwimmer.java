package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate {
   private static final long b = 21694624065353L;
   private static final long c = -8874399540258537466L;

   public PathNavigateSwimmer(EntityLiving var1, World var2) {
      super(var1, var2);
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
      Vec3 var4 = this.getEntityPosition();
      float var5 = this.theEntity.width * this.theEntity.width;
      int var6 = (int)c;
      if (var4.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < var5) {
         this.currentPath.incrementPathIndex();
      }

      for (int var7 = Math.min(this.currentPath.getCurrentPathIndex() + var6, this.currentPath.getCurrentPathLength() - 1);
         var7 > this.currentPath.getCurrentPathIndex();
         var7--
      ) {
         Vec3 var8 = this.currentPath.getVectorFromIndex(this.theEntity, var7);
         if (var8.squareDistanceTo(var4) <= 36.0 && this.isDirectPathBetweenPoints(var4, var8, 0, 0, 0)) {
            this.currentPath.setCurrentPathIndex(var7);
            break;
         }
      }

      this.checkForStuck(var4);
   }

   @Override
   public void removeSunnyPath() {
      super.removeSunnyPath();
   }

   @Override
   public boolean isDirectPathBetweenPoints(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      MovingObjectPosition var9 = this.worldObj
         .rayTraceBlocks(var1, new Vec3(var2.xCoord, var2.yCoord + this.theEntity.height * 0.5, var2.zCoord), false, true, false);
      return var9 == null || var9.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
   }
}
