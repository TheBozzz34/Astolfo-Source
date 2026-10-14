package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {
   private final IInventory hopperInventory;

   public ContainerHopper(InventoryPlayer playerInventory, IInventory hopperInventoryIn, EntityPlayer player) {
      this.hopperInventory = hopperInventoryIn;
      hopperInventoryIn.openInventory(player);
      byte b = 51;

      for (int i = 0; i < hopperInventoryIn.getSizeInventory(); i++) {
         this.addSlotToContainer(new Slot(hopperInventoryIn, i, 44 + i * 18, 20));
      }

      for (int j = 0; j < 3; j++) {
         for (int k = 0; k < 9; k++) {
            this.addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, j * 18 + b));
         }
      }

      for (int l = 0; l < 9; l++) {
         this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 58 + b));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.hopperInventory.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < this.hopperInventory.getSizeInventory()) {
            if (!this.mergeItemStack(itemStack2, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 0, this.hopperInventory.getSizeInventory(), false)) {
            return null;
         }

         if (itemStack2.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemStack;
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.hopperInventory.closeInventory(playerIn);
   }
}
