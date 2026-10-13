package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {
   private final IInventory hopperInventory;
   private static final long a = 127444913144368L;

   public ContainerHopper(InventoryPlayer var1, IInventory var2, EntityPlayer var3) {
      this.hopperInventory = var2;
      var2.openInventory(var3);
      byte var7 = 51;

      for (int var8 = 0; var8 < var2.getSizeInventory(); var8++) {
         this.addSlotToContainer(new Slot(var2, var8, 44 + var8 * 18, 20));
      }

      for (int var10 = 0; var10 < 3; var10++) {
         for (int var9 = 0; var9 < 9; var9++) {
            this.addSlotToContainer(new Slot(var1, var9 + var10 * 9 + 9, 8 + var9 * 18, var10 * 18 + var7));
         }
      }

      for (int var11 = 0; var11 < 9; var11++) {
         this.addSlotToContainer(new Slot(var1, var11, 8 + var11 * 18, 58 + var7));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.hopperInventory.isUseableByPlayer(var1);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 < this.hopperInventory.getSizeInventory()) {
            if (!this.mergeItemStack(var8, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(var8, 0, this.hopperInventory.getSizeInventory(), false)) {
            return null;
         }

         if (var8.stackSize == 0) {
            var7.putStack((ItemStack)null);
         } else {
            var7.onSlotChanged();
         }
      }

      return var6;
   }

   @Override
   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);
      this.hopperInventory.closeInventory(var1);
   }
}
