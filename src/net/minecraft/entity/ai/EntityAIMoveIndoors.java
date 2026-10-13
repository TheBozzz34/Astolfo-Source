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

   public EntityAIMoveIndoors(EntityCreature var1) {
      this.entityObj = var1;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      BlockPos var4 = new BlockPos(this.entityObj);
      if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining() && !this.entityObj.worldObj.getBiomeGenForCoords(var4).canRain())
         && !this.entityObj.worldObj.provider.getHasNoSky()) {
         if (this.entityObj.getRNG().nextInt(50) != 0) {
            return false;
         }

         if (this.insidePosX != -1 && this.entityObj.getDistanceSq(this.insidePosX, this.entityObj.posY, this.insidePosZ) < 4.0) {
            return false;
         }

         Village var5 = this.entityObj.worldObj.getVillageCollection().getNearestVillage(var4, 14);
         if (var5 == null) {
            return false;
         }

         this.doorInfo = var5.getDoorInfo(var4);
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
      BlockPos var4 = this.doorInfo.getInsideBlockPos();
      int var5 = var4.getX();
      int var6 = var4.getY();
      int var7 = var4.getZ();
      if (this.entityObj.getDistanceSq(var4) > 256.0) {
         Vec3 var8 = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, new Vec3(var5 + 0.5, var6, var7 + 0.5));
         if (var8 != null) {
            this.entityObj.getNavigator().tryMoveToXYZ(var8.xCoord, var8.yCoord, var8.zCoord, 1.0);
         }
      } else {
         this.entityObj.getNavigator().tryMoveToXYZ(var5 + 0.5, var6, var7 + 0.5, 1.0);
      }
   }

   @Override
   public void resetTask() {
      this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
      this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
      this.doorInfo = null;
   }
}
