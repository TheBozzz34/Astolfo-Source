package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {
   private IInventory horseInventory;
   private EntityHorse theHorse;

   public ContainerHorseInventory(IInventory playerInventory, IInventory horseInventoryIn, final EntityHorse horse, EntityPlayer player) {
      this.horseInventory = horseInventoryIn;
      this.theHorse = horse;
      byte b = 3;
      horseInventoryIn.openInventory(player);
      int i = (b - 4) * 18;
      this.addSlotToContainer(new Slot(horseInventoryIn, 0, 8, 18) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && stack.getItem() == Items.saddle && !this.getHasStack();
         }
      });
      this.addSlotToContainer(new Slot(horseInventoryIn, 1, 8, 36) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && horse.canWearArmor() && EntityHorse.isArmorItem(stack.getItem());
         }

         @Override
         public boolean canBeHovered() {
            return horse.canWearArmor();
         }
      });
      if (horse.isChested()) {
         for (int j = 0; j < b; j++) {
            for (int k = 0; k < 5; k++) {
               this.addSlotToContainer(new Slot(horseInventoryIn, 2 + k + j * 5, 80 + k * 18, 18 + j * 18));
            }
         }
      }

      for (int l = 0; l < 3; l++) {
         for (int m = 0; m < 9; m++) {
            this.addSlotToContainer(new Slot(playerInventory, m + l * 9 + 9, 8 + m * 18, 102 + l * 18 + i));
         }
      }

      for (int n = 0; n < 9; n++) {
         this.addSlotToContainer(new Slot(playerInventory, n, 8 + n * 18, 160 + i));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.horseInventory.isUseableByPlayer(playerIn) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(playerIn) < 8.0F;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index < this.horseInventory.getSizeInventory()) {
            if (!this.mergeItemStack(itemStack2, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (this.getSlot(1).isItemValid(itemStack2) && !this.getSlot(1).getHasStack()) {
            if (!this.mergeItemStack(itemStack2, 1, 2, false)) {
               return null;
            }
         } else if (this.getSlot(0).isItemValid(itemStack2)) {
            if (!this.mergeItemStack(itemStack2, 0, 1, false)) {
               return null;
            }
         } else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemStack2, 2, this.horseInventory.getSizeInventory(), false)) {
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
      this.horseInventory.closeInventory(playerIn);
   }
}
