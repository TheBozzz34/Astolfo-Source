package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerDispenser extends Container {
   private IInventory dispenserInventory;

   public ContainerDispenser(IInventory playerInventory, IInventory dispenserInventoryIn) {
      this.dispenserInventory = dispenserInventoryIn;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            this.addSlotToContainer(new Slot(dispenserInventoryIn, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for (int k = 0; k < 3; k++) {
         for (int l = 0; l < 9; l++) {
            this.addSlotToContainer(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
         }
      }

      for (int m = 0; m < 9; m++) {
         this.addSlotToContainer(new Slot(playerInventory, m, 8 + m * 18, 142));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.dispenserInventory.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < 9) {
            if (!this.mergeItemStack(itemStack2, 9, 45, true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 0, 9, false)) {
            return null;
         }

         if (itemStack2.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if (itemStack2.stackSize == itemStack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(playerIn, itemStack2);
      }

      return itemStack;
   }
}
