package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract {
   public boolean closeDoor;
   public int closeDoorTemporisation;
   private static final long a = 97499747308583L;
   private static final long e = -8221712812192301036L;

   public EntityAIOpenDoor(EntityLiving var1, boolean var2) {
      super(var1);
      this.theEntity = var1;
      this.closeDoor = var2;
   }

   @Override
   public boolean continueExecuting() {
      return this.closeDoor && this.closeDoorTemporisation > 0 && super.continueExecuting();
   }

   @Override
   public void startExecuting() {
      this.closeDoorTemporisation = (int)e;
      this.doorBlock.toggleDoor(this.theEntity.worldObj, this.doorPosition, true);
   }

   @Override
   public void resetTask() {
      if (this.closeDoor) {
         this.doorBlock.toggleDoor(this.theEntity.worldObj, this.doorPosition, false);
      }
   }

   @Override
   public void updateTask() {
      this.closeDoorTemporisation--;
      super.updateTask();
   }
}
