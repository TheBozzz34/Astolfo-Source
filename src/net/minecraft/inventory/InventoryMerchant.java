package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {
   private final IMerchant theMerchant;
   private ItemStack[] theInventory = new ItemStack[3];
   private final EntityPlayer thePlayer;
   private MerchantRecipe currentRecipe;
   private int currentRecipeIndex;
   private static final long a = 30018990506457L;
   private static final long c = 6161021438108106816L;

   public InventoryMerchant(EntityPlayer var1, IMerchant var2) {
      this.thePlayer = var1;
      this.theMerchant = var2;
   }

   @Override
   public int getSizeInventory() {
      return this.theInventory.length;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return this.theInventory[var1];
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.theInventory[var1] != null) {
         if (var1 == 2) {
            ItemStack var8 = this.theInventory[var1];
            this.theInventory[var1] = null;
            return var8;
         }

         if (this.theInventory[var1].stackSize <= var2) {
            ItemStack var7 = this.theInventory[var1];
            this.theInventory[var1] = null;
            if (this.inventoryResetNeededOnSlotChange(var1)) {
               this.resetRecipeAndSlots();
            }

            return var7;
         } else {
            ItemStack var6 = this.theInventory[var1].splitStack(var2);
            if (this.theInventory[var1].stackSize == 0) {
               this.theInventory[var1] = null;
            }

            if (this.inventoryResetNeededOnSlotChange(var1)) {
               this.resetRecipeAndSlots();
            }

            return var6;
         }
      } else {
         return null;
      }
   }

   private boolean inventoryResetNeededOnSlotChange(int var1) {
      return var1 == 0 || var1 == 1;
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.theInventory[var1] != null) {
         ItemStack var5 = this.theInventory[var1];
         this.theInventory[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.theInventory[var1] = var2;
      if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
         var2.stackSize = this.getInventoryStackLimit();
      }

      if (this.inventoryResetNeededOnSlotChange(var1)) {
         this.resetRecipeAndSlots();
      }
   }

   @Override
   public String getName() {
      return "mob.villager";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public int getInventoryStackLimit() {
      return (int)c;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.theMerchant.getCustomer() == var1;
   }

   @Override
   public void openInventory(EntityPlayer var1) {
   }

   @Override
   public void closeInventory(EntityPlayer var1) {
   }

   @Override
   public boolean isItemValidForSlot(int var1, ItemStack var2) {
      return true;
   }

   @Override
   public void markDirty() {
      this.resetRecipeAndSlots();
   }

   public void resetRecipeAndSlots() {
      this.currentRecipe = null;
      ItemStack var4 = this.theInventory[0];
      ItemStack var5 = this.theInventory[1];
      if (var4 == null) {
         var4 = var5;
         var5 = null;
      }

      if (var4 == null) {
         this.setInventorySlotContents(2, (ItemStack)null);
      } else {
         MerchantRecipeList var6 = this.theMerchant.getRecipes(this.thePlayer);
         if (var6 != null) {
            MerchantRecipe var7 = var6.canRecipeBeUsed(var4, var5, this.currentRecipeIndex);
            if (var7 != null && !var7.isRecipeDisabled()) {
               this.currentRecipe = var7;
               this.setInventorySlotContents(2, var7.getItemToSell().copy());
            } else if (var5 != null) {
               var7 = var6.canRecipeBeUsed(var5, var4, this.currentRecipeIndex);
               if (var7 != null && !var7.isRecipeDisabled()) {
                  this.currentRecipe = var7;
                  this.setInventorySlotContents(2, var7.getItemToSell().copy());
               } else {
                  this.setInventorySlotContents(2, (ItemStack)null);
               }
            } else {
               this.setInventorySlotContents(2, (ItemStack)null);
            }
         }
      }

      this.theMerchant.verifySellingItem(this.getStackInSlot(2));
   }

   public MerchantRecipe getCurrentRecipe() {
      return this.currentRecipe;
   }

   public void setCurrentRecipeIndex(int var1) {
      this.currentRecipeIndex = var1;
      this.resetRecipeAndSlots();
   }

   @Override
   public int getField(int var1) {
      return 0;
   }

   @Override
   public void setField(int var1, int var2) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int var4 = 0; var4 < this.theInventory.length; var4++) {
         this.theInventory[var4] = null;
      }
   }
}
