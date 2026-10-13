package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase {
   private EntityCreature entityObj;
   private VillageDoorInfo frontDoor;
   private static final long a = 108734573617280L;
   private static final long c = -8268501786482966512L;

   public EntityAIRestrictOpenDoor(EntityCreature var1) {
      this.entityObj = var1;
      if (!(var1.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      if (this.entityObj.worldObj.isDaytime()) {
         return false;
      }

      BlockPos var4 = new BlockPos(this.entityObj);
      Village var5 = this.entityObj.worldObj.getVillageCollection().getNearestVillage(var4, (int)c);
      if (var5 == null) {
         return false;
      }

      this.frontDoor = var5.getNearestDoor(var4);
      return this.frontDoor == null ? false : this.frontDoor.getDistanceToInsideBlockSq(var4) < 2.25;
   }

   @Override
   public boolean continueExecuting() {
      return this.entityObj.worldObj.isDaytime()
         ? false
         : !this.frontDoor.getIsDetachedFromVillageFlag() && this.frontDoor.func_179850_c(new BlockPos(this.entityObj));
   }

   @Override
   public void startExecuting() {
      ((PathNavigateGround)this.entityObj.getNavigator()).setBreakDoors(false);
      ((PathNavigateGround)this.entityObj.getNavigator()).D(false);
   }

   @Override
   public void resetTask() {
      ((PathNavigateGround)this.entityObj.getNavigator()).setBreakDoors(true);
      ((PathNavigateGround)this.entityObj.getNavigator()).D(true);
      this.frontDoor = null;
   }

   @Override
   public void updateTask() {
      this.frontDoor.incrementDoorOpeningRestrictionCounter();
   }
}
