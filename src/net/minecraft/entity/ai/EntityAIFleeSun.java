package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSun extends EntityAIBase {
   private EntityCreature theCreature;
   private double shelterX;
   private double shelterY;
   private double shelterZ;
   private double movementSpeed;
   private World theWorld;
   private static final long a = 106496827003007L;

   public EntityAIFleeSun(EntityCreature var1, double var2) {
      this.theCreature = var1;
      this.movementSpeed = var2;
      this.theWorld = var1.worldObj;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theWorld.isDaytime()) {
         return false;
      }

      if (!this.theCreature.isBurning()) {
         return false;
      }

      if (!this.theWorld.canSeeSky(new BlockPos(this.theCreature.posX, this.theCreature.getEntityBoundingBox().minY, this.theCreature.posZ))) {
         return false;
      }

      Vec3 var4 = this.findPossibleShelter();
      if (var4 == null) {
         return false;
      }

      this.shelterX = var4.xCoord;
      this.shelterY = var4.yCoord;
      this.shelterZ = var4.zCoord;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.theCreature.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
   }

   private Vec3 findPossibleShelter() {
      Random var4 = this.theCreature.getRNG();
      BlockPos var5 = new BlockPos(this.theCreature.posX, this.theCreature.getEntityBoundingBox().minY, this.theCreature.posZ);

      for (int var6 = 0; var6 < 10; var6++) {
         BlockPos var7 = var5.add(var4.nextInt(20) - 10, var4.nextInt(6) - 3, var4.nextInt(20) - 10);
         if (!this.theWorld.canSeeSky(var7) && this.theCreature.getBlockPathWeight(var7) < 0.0F) {
            return new Vec3(var7.getX(), var7.getY(), var7.getZ());
         }
      }

      return null;
   }
}
