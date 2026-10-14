package net.minecraft.pathfinding;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate {
   public EntityLiving theEntity;
   public World worldObj;
   public PathEntity currentPath;
   public double speed;
   private final IAttributeInstance pathSearchRange;
   private int totalTicks;
   private int ticksAtLastPos;
   private Vec3 lastPosCheck = new Vec3(0.0, 0.0, 0.0);
   private float heightRequirement = 1.0F;
   private final PathFinder pathFinder;

   public PathNavigate(EntityLiving entitylivingIn, World worldIn) {
      this.theEntity = entitylivingIn;
      this.worldObj = worldIn;
      this.pathSearchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.followRange);
      this.pathFinder = this.getPathFinder();
   }

   public abstract PathFinder getPathFinder();

   public void setSpeed(double speedIn) {
      this.speed = speedIn;
   }

   public float getPathSearchRange() {
      return (float)this.pathSearchRange.getAttributeValue();
   }

   public final PathEntity getPathToXYZ(double x, double y, double z) {
      return this.getPathToPos(new BlockPos(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z)));
   }

   public PathEntity getPathToPos(BlockPos pos) {
      if (!this.canNavigate()) {
         return null;
      }

      float f = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos blockPos = new BlockPos(this.theEntity);
      int i = (int)(f + 8.0F);
      ChunkCache chunkCache = new ChunkCache(this.worldObj, blockPos.add(-i, -i, -i), blockPos.add(i, i, i), 0);
      PathEntity pathEntity = this.pathFinder.createEntityPathTo(chunkCache, this.theEntity, pos, f);
      this.worldObj.theProfiler.endSection();
      return pathEntity;
   }

   public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
      PathEntity pathEntity = this.getPathToXYZ(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z));
      return this.setPath(pathEntity, speedIn);
   }

   public void setHeightRequirement(float jumpHeight) {
      this.heightRequirement = jumpHeight;
   }

   public PathEntity getPathToEntityLiving(Entity entityIn) {
      if (!this.canNavigate()) {
         return null;
      }

      float f = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos blockPos = new BlockPos(this.theEntity).up();
      int i = (int)(f + 16.0F);
      ChunkCache chunkCache = new ChunkCache(this.worldObj, blockPos.add(-i, -i, -i), blockPos.add(i, i, i), 0);
      PathEntity pathEntity = this.pathFinder.createEntityPathTo(chunkCache, this.theEntity, entityIn, f);
      this.worldObj.theProfiler.endSection();
      return pathEntity;
   }

   public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
      PathEntity pathEntity = this.getPathToEntityLiving(entityIn);
      return pathEntity != null ? this.setPath(pathEntity, speedIn) : false;
   }

   public boolean setPath(PathEntity pathentityIn, double speedIn) {
      if (pathentityIn == null) {
         this.currentPath = null;
         return false;
      }

      if (!pathentityIn.isSamePath(this.currentPath)) {
         this.currentPath = pathentityIn;
      }

      this.removeSunnyPath();
      if (this.currentPath.getCurrentPathLength() == 0) {
         return false;
      }

      this.speed = speedIn;
      Vec3 vec3 = this.getEntityPosition();
      this.ticksAtLastPos = this.totalTicks;
      this.lastPosCheck = vec3;
      return true;
   }

   public PathEntity getPath() {
      return this.currentPath;
   }

   public void onUpdateNavigation() {
      this.totalTicks++;
      if (!this.noPath()) {
         if (this.canNavigate()) {
            this.pathFollow();
         } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            Vec3 vec3 = this.getEntityPosition();
            Vec3 vec32 = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());
            if (vec3.yCoord > vec32.yCoord
               && !this.theEntity.onGround
               && MathHelper.floor_double(vec3.xCoord) == MathHelper.floor_double(vec32.xCoord)
               && MathHelper.floor_double(vec3.zCoord) == MathHelper.floor_double(vec32.zCoord)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         if (!this.noPath()) {
            Vec3 vec33 = this.currentPath.getPosition(this.theEntity);
            if (vec33 != null) {
               AxisAlignedBB axisAlignedBB = new AxisAlignedBB(vec33.xCoord, vec33.yCoord, vec33.zCoord, vec33.xCoord, vec33.yCoord, vec33.zCoord)
                  .expand(0.5, 0.5, 0.5);
               List list = this.worldObj.getCollidingBoundingBoxes(this.theEntity, axisAlignedBB.addCoord(0.0, -1.0, 0.0));
               double d = -1.0;
               axisAlignedBB = axisAlignedBB.offset(0.0, 1.0, 0.0);

               for (Object object : list) {
                  AxisAlignedBB axisAlignedBB2 = (AxisAlignedBB)object;
                  d = axisAlignedBB2.calculateYOffset(axisAlignedBB, d);
               }

               this.theEntity.getMoveHelper().setMoveTo(vec33.xCoord, vec33.yCoord + d, vec33.zCoord, this.speed);
            }
         }
      }
   }

   public void pathFollow() {
      Vec3 vec3 = this.getEntityPosition();
      int i = this.currentPath.getCurrentPathLength();

      for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); j++) {
         if (this.currentPath.getPathPointFromIndex(j).yCoord != (int)vec3.yCoord) {
            i = j;
            break;
         }
      }

      float f = this.theEntity.width * this.theEntity.width * this.heightRequirement;

      for (int k = this.currentPath.getCurrentPathIndex(); k < i; k++) {
         Vec3 vec32 = this.currentPath.getVectorFromIndex(this.theEntity, k);
         if (vec3.squareDistanceTo(vec32) < f) {
            this.currentPath.setCurrentPathIndex(k + 1);
         }
      }

      int l = MathHelper.ceiling_float_int(this.theEntity.width);
      int m = (int)this.theEntity.height + 1;
      int n = l;

      for (int i2 = i - 1; i2 >= this.currentPath.getCurrentPathIndex(); i2--) {
         if (this.isDirectPathBetweenPoints(vec3, this.currentPath.getVectorFromIndex(this.theEntity, i2), l, m, n)) {
            this.currentPath.setCurrentPathIndex(i2);
            break;
         }
      }

      this.checkForStuck(vec3);
   }

   public void checkForStuck(Vec3 positionVec3) {
      if (this.totalTicks - this.ticksAtLastPos > 100) {
         if (positionVec3.squareDistanceTo(this.lastPosCheck) < 2.25) {
            this.clearPathEntity();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck = positionVec3;
      }
   }

   public boolean noPath() {
      return this.currentPath == null || this.currentPath.isFinished();
   }

   public void clearPathEntity() {
      this.currentPath = null;
   }

   public abstract Vec3 getEntityPosition();

   public abstract boolean canNavigate();

   public boolean isInLiquid() {
      return this.theEntity.isInWater() || this.theEntity.isInLava();
   }

   public void removeSunnyPath() {
   }

   public abstract boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ);
}
