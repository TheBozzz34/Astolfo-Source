package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowParent extends EntityAIBase {
   public EntityAnimal childAnimal;
   public EntityAnimal parentAnimal;
   public double moveSpeed;
   private int delayCounter;

   public EntityAIFollowParent(EntityAnimal animal, double speed) {
      this.childAnimal = animal;
      this.moveSpeed = speed;
   }

   @Override
   public boolean shouldExecute() {
      if (this.childAnimal.getGrowingAge() >= 0) {
         return false;
      }

      List list = this.childAnimal.worldObj.getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.getEntityBoundingBox().expand(8.0, 4.0, 8.0));
      EntityAnimal entityAnimal = null;
      double d = Double.MAX_VALUE;

      for (Object object : list) {
         EntityAnimal entityAnimal2 = (EntityAnimal)object;
         if (entityAnimal2.getGrowingAge() >= 0) {
            double e = this.childAnimal.getDistanceSqToEntity(entityAnimal2);
            if (e <= d) {
               d = e;
               entityAnimal = entityAnimal2;
            }
         }
      }

      if (entityAnimal == null) {
         return false;
      }

      if (d < 9.0) {
         return false;
      }

      this.parentAnimal = entityAnimal;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      if (this.childAnimal.getGrowingAge() >= 0) {
         return false;
      }

      if (!this.parentAnimal.isEntityAlive()) {
         return false;
      }

      double d = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);
      return d >= 9.0 && d <= 256.0;
   }

   @Override
   public void startExecuting() {
      this.delayCounter = 0;
   }

   @Override
   public void resetTask() {
      this.parentAnimal = null;
   }

   @Override
   public void updateTask() {
      if (--this.delayCounter <= 0) {
         this.delayCounter = 10;
         this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.moveSpeed);
      }
   }
}
