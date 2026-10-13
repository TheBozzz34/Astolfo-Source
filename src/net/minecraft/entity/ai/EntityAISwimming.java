package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAISwimming extends EntityAIBase {
   private EntityLiving theEntity;
   private static final long a = 51529700221171L;

   public EntityAISwimming(EntityLiving var1) {
      this.theEntity = var1;
      this.setMutexBits(4);
      ((PathNavigateGround)var1.getNavigator()).setCanSwim(true);
   }

   @Override
   public boolean shouldExecute() {
      return this.theEntity.isInWater() || this.theEntity.isInLava();
   }

   @Override
   public void updateTask() {
      if (this.theEntity.getRNG().nextFloat() < 0.8F) {
         this.theEntity.getJumpHelper().setJumping();
      }
   }
}
