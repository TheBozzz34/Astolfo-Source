package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAIBeg extends EntityAIBase {
   private EntityWolf theWolf;
   private EntityPlayer thePlayer;
   private World worldObject;
   private float minPlayerDistance;
   private int timeoutCounter;
   private static final long a = 104579596509084L;

   public EntityAIBeg(EntityWolf wolf, float minDistance) {
      this.theWolf = wolf;
      this.worldObject = wolf.worldObj;
      this.minPlayerDistance = minDistance;
      this.setMutexBits(2);
   }

   @Override
   public boolean shouldExecute() {
      this.thePlayer = this.worldObject.getClosestPlayerToEntity(this.theWolf, this.minPlayerDistance);
      return this.thePlayer == null ? false : this.hasPlayerGotBoneInHand(this.thePlayer);
   }

   @Override
   public boolean continueExecuting() {
      return !this.thePlayer.isEntityAlive()
         ? false
         : (
            this.theWolf.getDistanceSqToEntity(this.thePlayer) > this.minPlayerDistance * this.minPlayerDistance
               ? false
               : this.timeoutCounter > 0 && this.hasPlayerGotBoneInHand(this.thePlayer)
         );
   }

   @Override
   public void startExecuting() {
      this.theWolf.setBegging(true);
      this.timeoutCounter = 40 + this.theWolf.getRNG().nextInt(40);
   }

   @Override
   public void resetTask() {
      this.theWolf.setBegging(false);
      this.thePlayer = null;
   }

   @Override
   public void updateTask() {
      this.theWolf
         .getLookHelper()
         .setLookPosition(
            this.thePlayer.posX, this.thePlayer.posY + this.thePlayer.getEyeHeight(), this.thePlayer.posZ, 10.0F, this.theWolf.getVerticalFaceSpeed()
         );
      this.timeoutCounter--;
   }

   private boolean hasPlayerGotBoneInHand(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      return itemStack == null ? false : (!this.theWolf.isTamed() && itemStack.getItem() == Items.bone ? true : this.theWolf.isBreedingItem(itemStack));
   }
}
