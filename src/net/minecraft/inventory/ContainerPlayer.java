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
   private static final long a = 35224044280904L;

   public ContainerPlayer(InventoryPlayer var1, boolean var2, EntityPlayer var3) {
      this.isLocalWorld = var2;
      this.thePlayer = var3;
      this.addSlotToContainer(new SlotCrafting(var1.player, this.craftMatrix, this.craftResult, 0, 144, 36));

      for (int var7 = 0; var7 < 2; var7++) {
         for (int var8 = 0; var8 < 2; var8++) {
            this.addSlotToContainer(new Slot(this.craftMatrix, var8 + var7 * 2, 88 + var8 * 18, 26 + var7 * 18));
         }
      }

      for (int var9 = 0; var9 < 4; var9++) {
         final int var12 = var9;
         this.addSlotToContainer(
            new Slot(var1, var1.getSizeInventory() - 1 - var9, 8, 8 + var9 * 18) {
               private static final long a = 67202619009289L;

               @Override
               public int getSlotStackLimit() {
                  return 1;
               }

               @Override
               public boolean isItemValid(ItemStack var1) {
                  return var1 == null
                     ? false
                     : (
                        var1.getItem() instanceof ItemArmor
                           ? ((ItemArmor)var1.getItem()).armorType == var12
                           : (var1.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && var1.getItem() != Items.skull ? false : var12 == 0)
                     );
               }

               @Override
               public String getSlotTexture() {
                  return ItemArmor.EMPTY_SLOT_NAMES[var12];
               }
            }
         );
      }

      for (int var10 = 0; var10 < 3; var10++) {
         for (int var13 = 0; var13 < 9; var13++) {
            this.addSlotToContainer(new Slot(var1, var13 + (var10 + 1) * 9, 8 + var13 * 18, 84 + var10 * 18));
         }
      }

      for (int var11 = 0; var11 < 9; var11++) {
         this.addSlotToContainer(new Slot(var1, var11, 8 + var11 * 18, 142));
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   @Override
   public void onCraftMatrixChanged(IInventory var1) {
      this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
   }

   @Override
   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);

      for (int var5 = 0; var5 < 4; var5++) {
         ItemStack var6 = this.craftMatrix.removeStackFromSlot(var5);
         if (var6 != null) {
            var1.dropPlayerItemWithRandomChoice(var6, false);
         }
      }

      this.craftResult.setInventorySlotContents(0, (ItemStack)null);
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return true;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 == 0) {
            if (!this.mergeItemStack(var8, 9, 45, true)) {
               return null;
            }

            var7.onSlotChange(var8, var6);
         } else if (var2 >= 1 && var2 < 5) {
            if (!this.mergeItemStack(var8, 9, 45, false)) {
               return null;
            }
         } else if (var2 >= 5 && var2 < 9) {
            if (!this.mergeItemStack(var8, 9, 45, false)) {
               return null;
            }
         } else if (var6.getItem() instanceof ItemArmor && !this.inventorySlots.get(5 + ((ItemArmor)var6.getItem()).armorType).getHasStack()) {
            int var9 = 5 + ((ItemArmor)var6.getItem()).armorType;
            if (!this.mergeItemStack(var8, var9, var9 + 1, false)) {
               return null;
            }
         } else if (var2 >= 9 && var2 < 36) {
            if (!this.mergeItemStack(var8, 36, 45, false)) {
               return null;
            }
         } else if (var2 >= 36 && var2 < 45) {
            if (!this.mergeItemStack(var8, 9, 36, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var8, 9, 45, false)) {
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
   public boolean canMergeSlot(ItemStack var1, Slot var2) {
      return var2.inventory != this.craftResult && super.canMergeSlot(var1, var2);
   }
}
