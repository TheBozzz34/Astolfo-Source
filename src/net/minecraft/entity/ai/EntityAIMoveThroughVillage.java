package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase {
   private EntityCreature theEntity;
   private double movementSpeed;
   private PathEntity entityPathNavigate;
   private VillageDoorInfo doorInfo;
   private boolean isNocturnal;
   private List<VillageDoorInfo> doorList = Lists.newArrayList();
   private static final long a = 140718183678574L;

   public EntityAIMoveThroughVillage(EntityCreature var1, double var2, boolean var4) {
      this.theEntity = var1;
      this.movementSpeed = var2;
      this.isNocturnal = var4;
      this.setMutexBits(1);
      if (!(var1.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      this.resizeDoorList();
      if (this.isNocturnal && this.theEntity.worldObj.isDaytime()) {
         return false;
      }

      Village var4 = this.theEntity.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this.theEntity), 0);
      if (var4 == null) {
         return false;
      }

      this.doorInfo = this.findNearestDoor(var4);
      if (this.doorInfo == null) {
         return false;
      }

      PathNavigateGround var5 = (PathNavigateGround)this.theEntity.getNavigator();
      boolean var6 = var5.getEnterDoors();
      var5.setBreakDoors(false);
      this.entityPathNavigate = var5.getPathToPos(this.doorInfo.getDoorBlockPos());
      var5.setBreakDoors(var6);
      if (this.entityPathNavigate != null) {
         return true;
      }

      Vec3 var7 = RandomPositionGenerator.findRandomTargetBlockTowards(
         this.theEntity,
         10,
         7,
         new Vec3(this.doorInfo.getDoorBlockPos().getX(), this.doorInfo.getDoorBlockPos().getY(), this.doorInfo.getDoorBlockPos().getZ())
      );
      if (var7 == null) {
         return false;
      }

      var5.setBreakDoors(false);
      this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(var7.xCoord, var7.yCoord, var7.zCoord);
      var5.setBreakDoors(var6);
      return this.entityPathNavigate != null;
   }

   @Override
   public boolean continueExecuting() {
      if (this.theEntity.getNavigator().noPath()) {
         return false;
      }

      float var4 = this.theEntity.width + 4.0F;
      return this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) > var4 * var4;
   }

   @Override
   public void startExecuting() {
      this.theEntity.getNavigator().setPath(this.entityPathNavigate, this.movementSpeed);
   }

   @Override
   public void resetTask() {
      if (this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) < 16.0) {
         this.doorList.add(this.doorInfo);
      }
   }

   private VillageDoorInfo findNearestDoor(Village var1) {
      VillageDoorInfo var5 = null;
      int var6 = Integer.MAX_VALUE;

      for (VillageDoorInfo var8 : var1.getVillageDoorInfoList()) {
         int var9 = var8.getDistanceSquared(
            MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ)
         );
         if (var9 < var6 && !this.doesDoorListContain(var8)) {
            var5 = var8;
            var6 = var9;
         }
      }

      return var5;
   }

   private boolean doesDoorListContain(VillageDoorInfo var1) {
      for (VillageDoorInfo var6 : this.doorList) {
         if (var1.getDoorBlockPos().equals(var6.getDoorBlockPos())) {
            return true;
         }
      }

      return false;
   }

   private void resizeDoorList() {
      if (this.doorList.size() > 15) {
         this.doorList.remove(0);
      }
   }
}
