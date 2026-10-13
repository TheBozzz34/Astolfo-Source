package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCraftResult implements IInventory {
   private ItemStack[] stackResult = new ItemStack[1];
   private static final long a = 31712658054187L;
   private static final long c = 5698696319254659136L;

   @Override
   public int getSizeInventory() {
      return 1;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return this.stackResult[0];
   }

   @Override
   public String getName() {
      return "Result";
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
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.stackResult[0] != null) {
         ItemStack var6 = this.stackResult[0];
         this.stackResult[0] = null;
         return var6;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.stackResult[0] != null) {
         ItemStack var5 = this.stackResult[0];
         this.stackResult[0] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.stackResult[0] = var2;
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
      for (int var4 = 0; var4 < this.stackResult.length; var4++) {
         this.stackResult[var4] = null;
      }
   }
}
