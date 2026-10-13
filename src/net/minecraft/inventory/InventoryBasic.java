package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory {
   private String inventoryTitle;
   private int slotsCount;
   private ItemStack[] inventoryContents;
   private List<IInvBasic> changeListeners;
   private boolean hasCustomName;
   private static final long b = 54028134748460L;
   private static final long f = -2792295836996861888L;

   public InventoryBasic(String var1, boolean var2, int var3) {
      this.inventoryTitle = var1;
      this.hasCustomName = var2;
      this.slotsCount = var3;
      this.inventoryContents = new ItemStack[var3];
   }

   public InventoryBasic(IChatComponent var1, int var2) {
      this(var1.getUnformattedText(), true, var2);
   }

   public void addInventoryChangeListener(IInvBasic var1) {
      if (this.changeListeners == null) {
         this.changeListeners = Lists.newArrayList();
      }

      this.changeListeners.add(var1);
   }

   public void removeInventoryChangeListener(IInvBasic var1) {
      this.changeListeners.remove(var1);
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return var1 >= 0 && var1 < this.inventoryContents.length ? this.inventoryContents[var1] : null;
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.inventoryContents[var1] != null) {
         if (this.inventoryContents[var1].stackSize <= var2) {
            ItemStack var7 = this.inventoryContents[var1];
            this.inventoryContents[var1] = null;
            this.markDirty();
            return var7;
         }

         ItemStack var6 = this.inventoryContents[var1].splitStack(var2);
         if (this.inventoryContents[var1].stackSize == 0) {
            this.inventoryContents[var1] = null;
         }

         this.markDirty();
         return var6;
      } else {
         return null;
      }
   }

   public ItemStack func_174894_a(ItemStack var1) {
      ItemStack var5 = var1.copy();

      for (int var6 = 0; var6 < this.slotsCount; var6++) {
         ItemStack var7 = this.getStackInSlot(var6);
         if (var7 == null) {
            this.setInventorySlotContents(var6, var5);
            this.markDirty();
            return null;
         }

         if (ItemStack.areItemsEqual(var7, var5)) {
            int var8 = Math.min(this.getInventoryStackLimit(), var7.getMaxStackSize());
            int var9 = Math.min(var5.stackSize, var8 - var7.stackSize);
            if (var9 > 0) {
               var7.stackSize += var9;
               var5.stackSize -= var9;
               if (var5.stackSize <= 0) {
                  this.markDirty();
                  return null;
               }
            }
         }
      }

      if (var5.stackSize != var1.stackSize) {
         this.markDirty();
      }

      return var5;
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.inventoryContents[var1] != null) {
         ItemStack var5 = this.inventoryContents[var1];
         this.inventoryContents[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.inventoryContents[var1] = var2;
      if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
         var2.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   @Override
   public int getSizeInventory() {
      return this.slotsCount;
   }

   @Override
   public String getName() {
      return this.inventoryTitle;
   }

   @Override
   public boolean hasCustomName() {
      return this.hasCustomName;
   }

   public void setCustomName(String var1) {
      this.hasCustomName = true;
      this.inventoryTitle = var1;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public int getInventoryStackLimit() {
      return (int)f;
   }

   @Override
   public void markDirty() {
      if (this.changeListeners != null) {
         for (int var4 = 0; var4 < this.changeListeners.size(); var4++) {
            this.changeListeners.get(var4).onInventoryChanged(this);
         }
      }
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
      for (int var4 = 0; var4 < this.inventoryContents.length; var4++) {
         this.inventoryContents[var4] = null;
      }
   }
}
