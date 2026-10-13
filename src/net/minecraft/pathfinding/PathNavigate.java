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
   private static final long a = 123618380790522L;
   private static final long h = -9137255176611037084L;

   public PathNavigate(EntityLiving var1, World var2) {
      this.theEntity = var1;
      this.worldObj = var2;
      this.pathSearchRange = var1.getEntityAttribute(SharedMonsterAttributes.followRange);
      this.pathFinder = this.getPathFinder();
   }

   public abstract PathFinder getPathFinder();

   public void setSpeed(double var1) {
      this.speed = var1;
   }

   public float getPathSearchRange() {
      return (float)this.pathSearchRange.getAttributeValue();
   }

   public final PathEntity getPathToXYZ(double var1, double var3, double var5) {
      return this.getPathToPos(new BlockPos(MathHelper.floor_double(var1), (int)var3, MathHelper.floor_double(var5)));
   }

   public PathEntity getPathToPos(BlockPos var1) {
      if (!this.canNavigate()) {
         return null;
      }

      float var5 = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos var6 = new BlockPos(this.theEntity);
      int var7 = (int)(var5 + 8.0F);
      ChunkCache var8 = new ChunkCache(this.worldObj, var6.add(-var7, -var7, -var7), var6.add(var7, var7, var7), 0);
      PathEntity var9 = this.pathFinder.createEntityPathTo(var8, this.theEntity, var1, var5);
      this.worldObj.theProfiler.endSection();
      return var9;
   }

   public boolean tryMoveToXYZ(double var1, double var3, double var5, double var7) {
      PathEntity var9 = this.getPathToXYZ(MathHelper.floor_double(var1), (int)var3, MathHelper.floor_double(var5));
      return this.setPath(var9, var7);
   }

   public void setHeightRequirement(float var1) {
      this.heightRequirement = var1;
   }

   public PathEntity getPathToEntityLiving(Entity var1) {
      if (!this.canNavigate()) {
         return null;
      }

      float var5 = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos var6 = new BlockPos(this.theEntity).up();
      int var7 = (int)(var5 + 16.0F);
      ChunkCache var8 = new ChunkCache(this.worldObj, var6.add(-var7, -var7, -var7), var6.add(var7, var7, var7), 0);
      PathEntity var9 = this.pathFinder.createEntityPathTo(var8, this.theEntity, var1, var5);
      this.worldObj.theProfiler.endSection();
      return var9;
   }

   public boolean tryMoveToEntityLiving(Entity var1, double var2) {
      PathEntity var6 = this.getPathToEntityLiving(var1);
      return var6 != null ? this.setPath(var6, var2) : false;
   }

   public boolean setPath(PathEntity var1, double var2) {
      if (var1 == null) {
         this.currentPath = null;
         return false;
      }

      if (!var1.isSamePath(this.currentPath)) {
         this.currentPath = var1;
      }

      this.removeSunnyPath();
      if (this.currentPath.getCurrentPathLength() == 0) {
         return false;
      }

      this.speed = var2;
      Vec3 var7 = this.getEntityPosition();
      this.ticksAtLastPos = this.totalTicks;
      this.lastPosCheck = var7;
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
            Vec3 var4 = this.getEntityPosition();
            Vec3 var5 = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());
            if (var4.yCoord > var5.yCoord
               && !this.theEntity.onGround
               && MathHelper.floor_double(var4.xCoord) == MathHelper.floor_double(var5.xCoord)
               && MathHelper.floor_double(var4.zCoord) == MathHelper.floor_double(var5.zCoord)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         if (!this.noPath()) {
            Vec3 var11 = this.currentPath.getPosition(this.theEntity);
            if (var11 != null) {
               AxisAlignedBB var12 = new AxisAlignedBB(var11.xCoord, var11.yCoord, var11.zCoord, var11.xCoord, var11.yCoord, var11.zCoord)
                  .expand(0.5, 0.5, 0.5);
               List var6 = this.worldObj.getCollidingBoundingBoxes(this.theEntity, var12.addCoord(0.0, -1.0, 0.0));
               double var7 = -1.0;
               var12 = var12.offset(0.0, 1.0, 0.0);

               for (Object var10$raw : var6) {
                  AxisAlignedBB var10 = (AxisAlignedBB)var10$raw;
                  var7 = var10.calculateYOffset(var12, var7);
               }

               this.theEntity.getMoveHelper().setMoveTo(var11.xCoord, var11.yCoord + var7, var11.zCoord, this.speed);
            }
         }
      }
   }

   public void pathFollow() {
      Vec3 var5 = this.getEntityPosition();
      int var6 = this.currentPath.getCurrentPathLength();

      for (int var7 = this.currentPath.getCurrentPathIndex(); var7 < this.currentPath.getCurrentPathLength(); var7++) {
         if (this.currentPath.getPathPointFromIndex(var7).yCoord != (int)var5.yCoord) {
            var6 = var7;
            break;
         }
      }

      float var12 = this.theEntity.width * this.theEntity.width * this.heightRequirement;

      for (int var8 = this.currentPath.getCurrentPathIndex(); var8 < var6; var8++) {
         Vec3 var9 = this.currentPath.getVectorFromIndex(this.theEntity, var8);
         if (var5.squareDistanceTo(var9) < var12) {
            this.currentPath.setCurrentPathIndex(var8 + 1);
         }
      }

      int var13 = MathHelper.ceiling_float_int(this.theEntity.width);
      int var14 = (int)this.theEntity.height + 1;
      int var10 = var13;

      for (int var11 = var6 - 1; var11 >= this.currentPath.getCurrentPathIndex(); var11--) {
         if (this.isDirectPathBetweenPoints(var5, this.currentPath.getVectorFromIndex(this.theEntity, var11), var13, var14, var10)) {
            this.currentPath.setCurrentPathIndex(var11);
            break;
         }
      }

      this.checkForStuck(var5);
   }

   public void checkForStuck(Vec3 var1) {
      if (this.totalTicks - this.ticksAtLastPos > (int)h) {
         if (var1.squareDistanceTo(this.lastPosCheck) < 2.25) {
            this.clearPathEntity();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck = var1;
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

   public abstract boolean isDirectPathBetweenPoints(Vec3 var1, Vec3 var2, int var3, int var4, int var5);
}
