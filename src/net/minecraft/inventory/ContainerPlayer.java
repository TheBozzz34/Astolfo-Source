package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer extends Container {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
   public IInventory craftResult = new InventoryCraftResult();
   public boolean isLocalWorld;
   private final EntityPlayer thePlayer;

   public ContainerPlayer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
      this.isLocalWorld = localWorld;
      this.thePlayer = player;
      this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18, 26 + i * 18));
         }
      }

      for (int k = 0; k < 4; k++) {
         final int l = k;
         this.addSlotToContainer(
            new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - k, 8, 8 + k * 18) {
               @Override
               public int getSlotStackLimit() {
                  return 1;
               }

               @Override
               public boolean isItemValid(ItemStack stack) {
                  return stack == null
                     ? false
                     : (
                        stack.getItem() instanceof ItemArmor
                           ? ((ItemArmor)stack.getItem()).armorType == l
                           : (stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull ? false : l == 0)
                     );
               }

               @Override
               public String getSlotTexture() {
                  return ItemArmor.EMPTY_SLOT_NAMES[l];
               }
            }
         );
      }

      for (int m = 0; m < 3; m++) {
         for (int n = 0; n < 9; n++) {
            this.addSlotToContainer(new Slot(playerInventory, n + (m + 1) * 9, 8 + n * 18, 84 + m * 18));
         }
      }

      for (int i2 = 0; i2 < 9; i2++) {
         this.addSlotToContainer(new Slot(playerInventory, i2, 8 + i2 * 18, 142));
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);

      for (int i = 0; i < 4; i++) {
         ItemStack itemStack = this.craftMatrix.removeStackFromSlot(i);
         if (itemStack != null) {
            playerIn.dropPlayerItemWithRandomChoice(itemStack, false);
         }
      }

      this.craftResult.setInventorySlotContents(0, (ItemStack)null);
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemStack2, 9, 45, true)) {
               return null;
            }

            slot.onSlotChange(itemStack2, itemStack);
         } else if (index >= 1 && index < 5) {
            if (!this.mergeItemStack(itemStack2, 9, 45, false)) {
               return null;
            }
         } else if (index >= 5 && index < 9) {
            if (!this.mergeItemStack(itemStack2, 9, 45, false)) {
               return null;
            }
         } else if (itemStack.getItem() instanceof ItemArmor && !this.inventorySlots.get(5 + ((ItemArmor)itemStack.getItem()).armorType).getHasStack()) {
            int i = 5 + ((ItemArmor)itemStack.getItem()).armorType;
            if (!this.mergeItemStack(itemStack2, i, i + 1, false)) {
               return null;
            }
         } else if (index >= 9 && index < 36) {
            if (!this.mergeItemStack(itemStack2, 36, 45, false)) {
               return null;
            }
         } else if (index >= 36 && index < 45) {
            if (!this.mergeItemStack(itemStack2, 9, 36, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemStack2, 9, 45, false)) {
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

   @Override
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
   }
}
