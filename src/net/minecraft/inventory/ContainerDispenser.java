package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerDispenser extends Container {
   private IInventory dispenserInventory;
   private static final long a = 58627766223273L;

   public ContainerDispenser(IInventory var1, IInventory var2) {
      this.dispenserInventory = var2;

      for (int var6 = 0; var6 < 3; var6++) {
         for (int var7 = 0; var7 < 3; var7++) {
            this.addSlotToContainer(new Slot(var2, var7 + var6 * 3, 62 + var7 * 18, 17 + var6 * 18));
         }
      }

      for (int var8 = 0; var8 < 3; var8++) {
         for (int var10 = 0; var10 < 9; var10++) {
            this.addSlotToContainer(new Slot(var1, var10 + var8 * 9 + 9, 8 + var10 * 18, 84 + var8 * 18));
         }
      }

      for (int var9 = 0; var9 < 9; var9++) {
         this.addSlotToContainer(new Slot(var1, var9, 8 + var9 * 18, 142));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.dispenserInventory.isUseableByPlayer(var1);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 < 9) {
            if (!this.mergeItemStack(var8, 9, 45, true)) {
               return null;
            }
         } else if (!this.mergeItemStack(var8, 0, 9, false)) {
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
}
