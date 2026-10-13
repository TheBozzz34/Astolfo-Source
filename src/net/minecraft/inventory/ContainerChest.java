package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
   private IInventory lowerChestInventory;
   private int numRows;
   private static final long a = 8440600391199L;

   public ContainerChest(IInventory var1, IInventory var2, EntityPlayer var3) {
      this.lowerChestInventory = var2;
      this.numRows = var2.getSizeInventory() / 9;
      var2.openInventory(var3);
      int var7 = (this.numRows - 4) * 18;

      for (int var8 = 0; var8 < this.numRows; var8++) {
         for (int var9 = 0; var9 < 9; var9++) {
            this.addSlotToContainer(new Slot(var2, var9 + var8 * 9, 8 + var9 * 18, 18 + var8 * 18));
         }
      }

      for (int var10 = 0; var10 < 3; var10++) {
         for (int var12 = 0; var12 < 9; var12++) {
            this.addSlotToContainer(new Slot(var1, var12 + var10 * 9 + 9, 8 + var12 * 18, 103 + var10 * 18 + var7));
         }
      }

      for (int var11 = 0; var11 < 9; var11++) {
         this.addSlotToContainer(new Slot(var1, var11, 8 + var11 * 18, 161 + var7));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.lowerChestInventory.isUseableByPlayer(var1);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 < this.numRows * 9) {
            if (!this.mergeItemStack(var8, this.numRows * 9, this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(var8, 0, this.numRows * 9, false)) {
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
      this.lowerChestInventory.closeInventory(var1);
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }
}
