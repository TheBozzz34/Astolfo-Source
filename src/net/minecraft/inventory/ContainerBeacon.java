package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container {
   private IInventory tileBeacon;
   private final ContainerBeacon.BeaconSlot beaconSlot;
   private static final long a = 102205230103921L;

   public ContainerBeacon(IInventory var1, IInventory var2) {
      this.tileBeacon = var2;
      this.addSlotToContainer(this.beaconSlot = new ContainerBeacon.BeaconSlot(var2, 0, 136, 110));
      byte var6 = 36;
      short var7 = 137;

      for (int var8 = 0; var8 < 3; var8++) {
         for (int var9 = 0; var9 < 9; var9++) {
            this.addSlotToContainer(new Slot(var1, var9 + var8 * 9 + 9, var6 + var9 * 18, var7 + var8 * 18));
         }
      }

      for (int var10 = 0; var10 < 9; var10++) {
         this.addSlotToContainer(new Slot(var1, var10, var6 + var10 * 18, 58 + var7));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting var1) {
      super.onCraftGuiOpened(var1);
      var1.sendAllWindowProperties(this, this.tileBeacon);
   }

   @Override
   public void updateProgressBar(int var1, int var2) {
      this.tileBeacon.setField(var1, var2);
   }

   public IInventory func_180611_e() {
      return this.tileBeacon;
   }

   @Override
   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);
      if (var1 != null && !var1.worldObj.isRemote) {
         ItemStack var5 = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());
         if (var5 != null) {
            var1.dropPlayerItemWithRandomChoice(var5, false);
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.tileBeacon.isUseableByPlayer(var1);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 == 0) {
            if (!this.mergeItemStack(var8, 1, 37, true)) {
               return null;
            }

            var7.onSlotChange(var8, var6);
         } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(var8) && var8.stackSize == 1) {
            if (!this.mergeItemStack(var8, 0, 1, false)) {
               return null;
            }
         } else if (var2 >= 1 && var2 < 28) {
            if (!this.mergeItemStack(var8, 28, 37, false)) {
               return null;
            }
         } else if (var2 >= 28 && var2 < 37) {
            if (!this.mergeItemStack(var8, 1, 28, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var8, 1, 37, false)) {
            return null;
         }

         if (var8.stackSize == 0) {
            var7.putStack((ItemStack)null);
         } else {
            var7.onSlotChanged();
         }

         if (var8.stackSize == var6.stackSize) {
            return null;
         }

         var7.onPickupFromSlot(var1, var8);
      }

      return var6;
   }

   public class BeaconSlot extends Slot {
      private static final long a = 10415451948L;

      public BeaconSlot(IInventory var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
      }

      @Override
      public boolean isItemValid(ItemStack var1) {
         return var1 == null
            ? false
            : var1.getItem() == Items.emerald || var1.getItem() == Items.diamond || var1.getItem() == Items.gold_ingot || var1.getItem() == Items.iron_ingot;
      }

      @Override
      public int getSlotStackLimit() {
         return 1;
      }
   }
}
