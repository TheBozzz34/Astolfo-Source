package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILookAtTradePlayer extends EntityAIWatchClosest {
   private final EntityVillager theMerchant;
   private static final long a = 46845952393505L;

   public EntityAILookAtTradePlayer(EntityVillager var1) {
      super(var1, EntityPlayer.class, 8.0F);
      this.theMerchant = var1;
   }

   @Override
   public boolean shouldExecute() {
      if (this.theMerchant.isTrading()) {
         this.closestEntity = this.theMerchant.getCustomer();
         return true;
      } else {
         return false;
      }
   }
}
