package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCrafting implements IInventory {
   private final ItemStack[] stackList;
   private final int inventoryWidth;
   private final int inventoryHeight;
   private final Container eventHandler;
   private static final long a = 125232650460522L;
   private static final long c = 8138514146002993216L;

   public InventoryCrafting(Container var1, int var2, int var3) {
      int var4 = var2 * var3;
      this.stackList = new ItemStack[var4];
      this.eventHandler = var1;
      this.inventoryWidth = var2;
      this.inventoryHeight = var3;
   }

   @Override
   public int getSizeInventory() {
      return this.stackList.length;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return var1 >= this.getSizeInventory() ? null : this.stackList[var1];
   }

   public ItemStack getStackInRowAndColumn(int var1, int var2) {
      return var1 >= 0 && var1 < this.inventoryWidth && var2 >= 0 && var2 <= this.inventoryHeight
         ? this.getStackInSlot(var1 + var2 * this.inventoryWidth)
         : null;
   }

   @Override
   public String getName() {
      return "container.crafting";
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
   public ItemStack removeStackFromSlot(int var1) {
      if (this.stackList[var1] != null) {
         ItemStack var5 = this.stackList[var1];
         this.stackList[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.stackList[var1] != null) {
         if (this.stackList[var1].stackSize <= var2) {
            ItemStack var7 = this.stackList[var1];
            this.stackList[var1] = null;
            this.eventHandler.onCraftMatrixChanged(this);
            return var7;
         }

         ItemStack var6 = this.stackList[var1].splitStack(var2);
         if (this.stackList[var1].stackSize == 0) {
            this.stackList[var1] = null;
         }

         this.eventHandler.onCraftMatrixChanged(this);
         return var6;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.stackList[var1] = var2;
      this.eventHandler.onCraftMatrixChanged(this);
   }

   @Override
   public int getInventoryStackLimit() {
      return (int)c;
   }

   @Override
   public void markDirty() {
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return true;
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
      for (int var4 = 0; var4 < this.stackList.length; var4++) {
         this.stackList[var4] = null;
      }
   }

   public int getHeight() {
      return this.inventoryHeight;
   }

   public int getWidth() {
      return this.inventoryWidth;
   }
}
