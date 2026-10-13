package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class InventoryLargeChest implements ILockableContainer {
   private String name;
   private ILockableContainer upperChest;
   private ILockableContainer lowerChest;
   private static final long a = 97849797470365L;

   public InventoryLargeChest(String var1, ILockableContainer var2, ILockableContainer var3) {
      this.name = var1;
      if (var2 == null) {
         var2 = var3;
      }

      if (var3 == null) {
         var3 = var2;
      }

      this.upperChest = var2;
      this.lowerChest = var3;
      if (var2.isLocked()) {
         var3.setLockCode(var2.getLockCode());
      } else if (var3.isLocked()) {
         var2.setLockCode(var3.getLockCode());
      }
   }

   @Override
   public int getSizeInventory() {
      return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
   }

   public boolean isPartOfLargeChest(IInventory var1) {
      return this.upperChest == var1 || this.lowerChest == var1;
   }

   @Override
   public String getName() {
      return this.upperChest.hasCustomName() ? this.upperChest.getName() : (this.lowerChest.hasCustomName() ? this.lowerChest.getName() : this.name);
   }

   @Override
   public boolean hasCustomName() {
      return this.upperChest.hasCustomName() || this.lowerChest.hasCustomName();
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return var1 >= this.upperChest.getSizeInventory()
         ? this.lowerChest.getStackInSlot(var1 - this.upperChest.getSizeInventory())
         : this.upperChest.getStackInSlot(var1);
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      return var1 >= this.upperChest.getSizeInventory()
         ? this.lowerChest.decrStackSize(var1 - this.upperChest.getSizeInventory(), var2)
         : this.upperChest.decrStackSize(var1, var2);
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      return var1 >= this.upperChest.getSizeInventory()
         ? this.lowerChest.removeStackFromSlot(var1 - this.upperChest.getSizeInventory())
         : this.upperChest.removeStackFromSlot(var1);
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      if (var1 >= this.upperChest.getSizeInventory()) {
         this.lowerChest.setInventorySlotContents(var1 - this.upperChest.getSizeInventory(), var2);
      } else {
         this.upperChest.setInventorySlotContents(var1, var2);
      }
   }

   @Override
   public int getInventoryStackLimit() {
      return this.upperChest.getInventoryStackLimit();
   }

   @Override
   public void markDirty() {
      this.upperChest.markDirty();
      this.lowerChest.markDirty();
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.upperChest.isUseableByPlayer(var1) && this.lowerChest.isUseableByPlayer(var1);
   }

   @Override
   public void openInventory(EntityPlayer var1) {
      this.upperChest.openInventory(var1);
      this.lowerChest.openInventory(var1);
   }

   @Override
   public void closeInventory(EntityPlayer var1) {
      this.upperChest.closeInventory(var1);
      this.lowerChest.closeInventory(var1);
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
   public boolean isLocked() {
      return this.upperChest.isLocked() || this.lowerChest.isLocked();
   }

   @Override
   public void setLockCode(LockCode var1) {
      this.upperChest.setLockCode(var1);
      this.lowerChest.setLockCode(var1);
   }

   @Override
   public LockCode getLockCode() {
      return this.upperChest.getLockCode();
   }

   @Override
   public String getGuiID() {
      return this.upperChest.getGuiID();
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerChest(var1, this, var2);
   }

   @Override
   public void clear() {
      this.upperChest.clear();
      this.lowerChest.clear();
   }
}
