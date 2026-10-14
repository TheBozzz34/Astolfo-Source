package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot {
   private final InventoryMerchant theMerchantInventory;
   private EntityPlayer thePlayer;
   private int field_75231_g;
   private final IMerchant theMerchant;
   private static final long a = 139798932726083L;

   public SlotMerchantResult(EntityPlayer player, IMerchant merchant, InventoryMerchant merchantInventory, int slotIndex, int xPosition, int yPosition) {
      super(merchantInventory, slotIndex, xPosition, yPosition);
      this.thePlayer = player;
      this.theMerchant = merchant;
      this.theMerchantInventory = merchantInventory;
   }

   @Override
   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   @Override
   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.field_75231_g = this.field_75231_g + Math.min(amount, this.getStack().stackSize);
      }

      return super.decrStackSize(amount);
   }

   @Override
   public void onCrafting(ItemStack stack, int amount) {
      this.field_75231_g += amount;
      this.onCrafting(stack);
   }

   @Override
   public void onCrafting(ItemStack stack) {
      stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75231_g);
      this.field_75231_g = 0;
   }

   @Override
   public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      this.onCrafting(stack);
      MerchantRecipe merchantRecipe = this.theMerchantInventory.getCurrentRecipe();
      if (merchantRecipe != null) {
         ItemStack itemStack = this.theMerchantInventory.getStackInSlot(0);
         ItemStack itemStack2 = this.theMerchantInventory.getStackInSlot(1);
         if (this.doTrade(merchantRecipe, itemStack, itemStack2) || this.doTrade(merchantRecipe, itemStack2, itemStack)) {
            this.theMerchant.useRecipe(merchantRecipe);
            playerIn.triggerAchievement(StatList.timesTradedWithVillagerStat);
            if (itemStack != null && itemStack.stackSize <= 0) {
               itemStack = null;
            }

            if (itemStack2 != null && itemStack2.stackSize <= 0) {
               itemStack2 = null;
            }

            this.theMerchantInventory.setInventorySlotContents(0, itemStack);
            this.theMerchantInventory.setInventorySlotContents(1, itemStack2);
         }
      }
   }

   private boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem) {
      ItemStack itemStack = trade.getItemToBuy();
      ItemStack itemStack2 = trade.getSecondItemToBuy();
      if (firstItem != null && firstItem.getItem() == itemStack.getItem()) {
         if (itemStack2 != null && secondItem != null && itemStack2.getItem() == secondItem.getItem()) {
            firstItem.stackSize = firstItem.stackSize - itemStack.stackSize;
            secondItem.stackSize = secondItem.stackSize - itemStack2.stackSize;
            return true;
         }

         if (itemStack2 == null && secondItem == null) {
            firstItem.stackSize = firstItem.stackSize - itemStack.stackSize;
            return true;
         }
      }

      return false;
   }
}
