package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container {
   private IInventory tileBeacon;
   private final ContainerBeacon.BeaconSlot beaconSlot;
   private static final long a = 102205230103921L;

   public ContainerBeacon(IInventory playerInventory, IInventory tileBeaconIn) {
      this.tileBeacon = tileBeaconIn;
      this.addSlotToContainer(this.beaconSlot = new ContainerBeacon.BeaconSlot(tileBeaconIn, 0, 136, 110));
      byte b = 36;
      short s = 137;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, b + j * 18, s + i * 18));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.addSlotToContainer(new Slot(playerInventory, k, b + k * 18, 58 + s));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendAllWindowProperties(this, this.tileBeacon);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      this.tileBeacon.setField(id, data);
   }

   public IInventory func_180611_e() {
      return this.tileBeacon;
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (playerIn != null && !playerIn.worldObj.isRemote) {
         ItemStack itemStack = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());
         if (itemStack != null) {
            playerIn.dropPlayerItemWithRandomChoice(itemStack, false);
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileBeacon.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemStack2, 1, 37, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(itemStack2) && itemStack2.stackSize == 1) {
            if (!this.mergeItemStack(itemStack2, 0, 1, false)) {
               return null;
            }
         } else if (index >= 1 && index < 28) {
            if (!this.mergeItemStack(itemStack2, 28, 37, false)) {
               return null;
            }
         } else if (index >= 28 && index < 37) {
            if (!this.mergeItemStack(itemStack2, 1, 28, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 1, 37, false)) {
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

   public class BeaconSlot extends Slot {
      private static final long a = 10415451948L;

      public BeaconSlot(IInventory iInventory, int i, int j, int k) {
         super(iInventory, i, j, k);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return stack == null
            ? false
            : stack.getItem() == Items.emerald
               || stack.getItem() == Items.diamond
               || stack.getItem() == Items.gold_ingot
               || stack.getItem() == Items.iron_ingot;
      }

      @Override
      public int getSlotStackLimit() {
         return 1;
      }
   }
}
