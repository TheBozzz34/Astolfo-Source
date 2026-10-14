package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
   private IInventory lowerChestInventory;
   private int numRows;
   private static final long a = 8440600391199L;

   public ContainerChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player) {
      this.lowerChestInventory = chestInventory;
      this.numRows = chestInventory.getSizeInventory() / 9;
      chestInventory.openInventory(player);
      int i = (this.numRows - 4) * 18;

      for (int j = 0; j < this.numRows; j++) {
         for (int k = 0; k < 9; k++) {
            this.addSlotToContainer(new Slot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
         }
      }

      for (int l = 0; l < 3; l++) {
         for (int m = 0; m < 9; m++) {
            this.addSlotToContainer(new Slot(playerInventory, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + i));
         }
      }

      for (int n = 0; n < 9; n++) {
         this.addSlotToContainer(new Slot(playerInventory, n, 8 + n * 18, 161 + i));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.lowerChestInventory.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < this.numRows * 9) {
            if (!this.mergeItemStack(itemStack2, this.numRows * 9, this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 0, this.numRows * 9, false)) {
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
      this.lowerChestInventory.closeInventory(playerIn);
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }
}
