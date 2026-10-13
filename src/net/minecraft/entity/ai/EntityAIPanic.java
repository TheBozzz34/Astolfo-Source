package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIPanic extends EntityAIBase {
   private EntityCreature theEntityCreature;
   public double speed;
   private double randPosX;
   private double randPosY;
   private double randPosZ;
   private static final long a = 60329686559242L;

   public EntityAIPanic(EntityCreature var1, double var2) {
      this.theEntityCreature = var1;
      this.speed = var2;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (this.theEntityCreature.getAITarget() == null && !this.theEntityCreature.isBurning()) {
         return false;
      }

      Vec3 var4 = RandomPositionGenerator.findRandomTarget(this.theEntityCreature, 5, 4);
      if (var4 == null) {
         return false;
      }

      this.randPosX = var4.xCoord;
      this.randPosY = var4.yCoord;
      this.randPosZ = var4.zCoord;
      return true;
   }

   @Override
   public void startExecuting() {
      this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
   }

   @Override
   public boolean continueExecuting() {
      return !this.theEntityCreature.getNavigator().noPath();
   }
}
