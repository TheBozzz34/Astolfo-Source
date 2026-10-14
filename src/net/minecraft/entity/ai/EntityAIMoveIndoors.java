package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase {
   private EntityCreature entityObj;
   private VillageDoorInfo doorInfo;
   private int insidePosX = -1;
   private int insidePosZ = -1;
   private static final long a = 91877819116121L;

   public EntityAIMoveIndoors(EntityCreature entityObjIn) {
      this.entityObj = entityObjIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      BlockPos blockPos = new BlockPos(this.entityObj);
      if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining() && !this.entityObj.worldObj.getBiomeGenForCoords(blockPos).canRain())
         && !this.entityObj.worldObj.provider.getHasNoSky()) {
         if (this.entityObj.getRNG().nextInt(50) != 0) {
            return false;
         }

         if (this.insidePosX != -1 && this.entityObj.getDistanceSq(this.insidePosX, this.entityObj.posY, this.insidePosZ) < 4.0) {
            return false;
         }

         Village village = this.entityObj.worldObj.getVillageCollection().getNearestVillage(blockPos, 14);
         if (village == null) {
            return false;
         }

         this.doorInfo = village.getDoorInfo(blockPos);
         return this.doorInfo != null;
      } else {
         return false;
      }
   }

   @Override
   public boolean continueExecuting() {
      return !this.entityObj.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.insidePosX = -1;
      BlockPos blockPos = this.doorInfo.getInsideBlockPos();
      int i = blockPos.getX();
      int j = blockPos.getY();
      int k = blockPos.getZ();
      if (this.entityObj.getDistanceSq(blockPos) > 256.0) {
         Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, new Vec3(i + 0.5, j, k + 0.5));
         if (vec3 != null) {
            this.entityObj.getNavigator().tryMoveToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord, 1.0);
         }
      } else {
         this.entityObj.getNavigator().tryMoveToXYZ(i + 0.5, j, k + 0.5, 1.0);
      }
   }

   @Override
   public void resetTask() {
      this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
      this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
      this.doorInfo = null;
   }
}
