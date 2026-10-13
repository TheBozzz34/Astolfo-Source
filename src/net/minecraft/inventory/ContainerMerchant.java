package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
   private IMerchant theMerchant;
   private InventoryMerchant merchantInventory;
   private final World theWorld;
   private static final long a = 93356823253243L;

   public ContainerMerchant(InventoryPlayer var1, IMerchant var2, World var3) {
      this.theMerchant = var2;
      this.theWorld = var3;
      this.merchantInventory = new InventoryMerchant(var1.player, var2);
      this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
      this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
      this.addSlotToContainer(new SlotMerchantResult(var1.player, var2, this.merchantInventory, 2, 120, 53));

      for (int var7 = 0; var7 < 3; var7++) {
         for (int var8 = 0; var8 < 9; var8++) {
            this.addSlotToContainer(new Slot(var1, var8 + var7 * 9 + 9, 8 + var8 * 18, 84 + var7 * 18));
         }
      }

      for (int var9 = 0; var9 < 9; var9++) {
         this.addSlotToContainer(new Slot(var1, var9, 8 + var9 * 18, 142));
      }
   }

   public InventoryMerchant getMerchantInventory() {
      return this.merchantInventory;
   }

   @Override
   public void onCraftGuiOpened(ICrafting var1) {
      super.onCraftGuiOpened(var1);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();
   }

   @Override
   public void onCraftMatrixChanged(IInventory var1) {
      this.merchantInventory.resetRecipeAndSlots();
      super.onCraftMatrixChanged(var1);
   }

   public void setCurrentRecipeIndex(int var1) {
      this.merchantInventory.setCurrentRecipeIndex(var1);
   }

   @Override
   public void updateProgressBar(int var1, int var2) {
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.theMerchant.getCustomer() == var1;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 == 2) {
            if (!this.mergeItemStack(var8, 3, 39, true)) {
               return null;
            }

            var7.onSlotChange(var8, var6);
         } else if (var2 != 0 && var2 != 1) {
            if (var2 >= 3 && var2 < 30) {
               if (!this.mergeItemStack(var8, 30, 39, false)) {
                  return null;
               }
            } else if (var2 >= 30 && var2 < 39 && !this.mergeItemStack(var8, 3, 30, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var8, 3, 39, false)) {
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

   @Override
   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);
      this.theMerchant.setCustomer((EntityPlayer)null);
      super.onContainerClosed(var1);
      if (!this.theWorld.isRemote) {
         ItemStack var5 = this.merchantInventory.removeStackFromSlot(0);
         if (var5 != null) {
            var1.dropPlayerItemWithRandomChoice(var5, false);
         }

         var5 = this.merchantInventory.removeStackFromSlot(1);
         if (var5 != null) {
            var1.dropPlayerItemWithRandomChoice(var5, false);
         }
      }
   }
}
