package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EntityAITradePlayer extends EntityAIBase {
   private EntityVillager villager;
   private static final long a = 67140599962075L;

   public EntityAITradePlayer(EntityVillager villagerIn) {
      this.villager = villagerIn;
      this.setMutexBits(5);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.villager.isEntityAlive()) {
         return false;
      }

      if (this.villager.isInWater()) {
         return false;
      }

      if (!this.villager.onGround) {
         return false;
      }

      if (this.villager.velocityChanged) {
         return false;
      }

      EntityPlayer entityPlayer = this.villager.getCustomer();
      return entityPlayer == null ? false : (this.villager.getDistanceSqToEntity(entityPlayer) > 16.0 ? false : entityPlayer.openContainer instanceof Container);
   }

   @Override
   public void startExecuting() {
      this.villager.getNavigator().clearPathEntity();
   }

   @Override
   public void resetTask() {
      this.villager.setCustomer((EntityPlayer)null);
   }
}
