package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntityLockable implements IInventory {
   private static final Random RNG = new Random();
   private ItemStack[] stacks = new ItemStack[9];
   public String customName;
   private static final long a = 52587521726578L;

   @Override
   public int getSizeInventory() {
      return 9;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return this.stacks[var1];
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.stacks[var1] != null) {
         if (this.stacks[var1].stackSize <= var2) {
            ItemStack var7 = this.stacks[var1];
            this.stacks[var1] = null;
            this.markDirty();
            return var7;
         }

         ItemStack var6 = this.stacks[var1].splitStack(var2);
         if (this.stacks[var1].stackSize == 0) {
            this.stacks[var1] = null;
         }

         this.markDirty();
         return var6;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.stacks[var1] != null) {
         ItemStack var5 = this.stacks[var1];
         this.stacks[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   public int getDispenseSlot() {
      int var4 = -1;
      int var5 = 1;

      for (int var6 = 0; var6 < this.stacks.length; var6++) {
         if (this.stacks[var6] != null && RNG.nextInt(var5++) == 0) {
            var4 = var6;
         }
      }

      return var4;
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.stacks[var1] = var2;
      if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
         var2.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   public int addItemStack(ItemStack var1) {
      for (int var5 = 0; var5 < this.stacks.length; var5++) {
         if (this.stacks[var5] == null || this.stacks[var5].getItem() == null) {
            this.setInventorySlotContents(var5, var1);
            return var5;
         }
      }

      return -1;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.dispenser";
   }

   public void setCustomName(String var1) {
      this.customName = var1;
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null;
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      NBTTagList var5 = var1.getTagList("Items", 10);
      this.stacks = new ItemStack[this.getSizeInventory()];

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         int var8 = var7.getByte("Slot") & 255;
         if (var8 >= 0 && var8 < this.stacks.length) {
            this.stacks[var8] = ItemStack.loadItemStackFromNBT(var7);
         }
      }

      if (var1.hasKey("CustomName", 8)) {
         this.customName = var1.getString("CustomName");
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.stacks.length; var6++) {
         if (this.stacks[var6] != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("Slot", (byte)var6);
            this.stacks[var6].writeToNBT(var7);
            var5.appendTag(var7);
         }
      }

      var1.setTag("Items", var5);
      if (this.hasCustomName()) {
         var1.setString("CustomName", this.customName);
      }
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : var1.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
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
   public String getGuiID() {
      return "minecraft:dispenser";
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerDispenser(var1, this);
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
      for (int var4 = 0; var4 < this.stacks.length; var4++) {
         this.stacks[var4] = null;
      }
   }
}
