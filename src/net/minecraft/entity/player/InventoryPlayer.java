package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory, Cloneable {
   public ItemStack[] mainInventory = new ItemStack[36];
   public ItemStack[] armorInventory = new ItemStack[4];
   public int currentItem;
   public EntityPlayer player;
   private ItemStack itemStack;
   public boolean inventoryChanged;

   public InventoryPlayer(EntityPlayer playerIn) {
      this.player = playerIn;
   }

   public ItemStack getCurrentItem() {
      return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
   }

   public static int getHotbarSize() {
      return 9;
   }

   private int getInventorySlotContainItem(Item itemIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn) {
            return i;
         }
      }

      return -1;
   }

   private int getInventorySlotContainItemAndDamage(Item itemIn, int metadataIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn && this.mainInventory[i].getMetadata() == metadataIn) {
            return i;
         }
      }

      return -1;
   }

   private int storeItemStack(ItemStack itemStackIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null
            && this.mainInventory[i].getItem() == itemStackIn.getItem()
            && this.mainInventory[i].isStackable()
            && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize()
            && this.mainInventory[i].stackSize < this.getInventoryStackLimit()
            && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getMetadata() == itemStackIn.getMetadata())
            && ItemStack.areItemStackTagsEqual(this.mainInventory[i], itemStackIn)) {
            return i;
         }
      }

      return -1;
   }

   public int getFirstEmptyStack() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] == null) {
            return i;
         }
      }

      return -1;
   }

   public void setCurrentItem(Item itemIn, int metadataIn, boolean isMetaSpecific, boolean bl) {
      ItemStack itemStack = this.getCurrentItem();
      int i = isMetaSpecific ? this.getInventorySlotContainItemAndDamage(itemIn, metadataIn) : this.getInventorySlotContainItem(itemIn);
      if (i >= 0 && i < 9) {
         this.currentItem = i;
      } else if (bl && itemIn != null) {
         int j = this.getFirstEmptyStack();
         if (j >= 0 && j < 9) {
            this.currentItem = j;
         }

         if (itemStack == null || !itemStack.isItemEnchantable() || this.getInventorySlotContainItemAndDamage(itemStack.getItem(), itemStack.getItemDamage()) != this.currentItem) {
            int k = this.getInventorySlotContainItemAndDamage(itemIn, metadataIn);
            int l;
            if (k >= 0) {
               l = this.mainInventory[k].stackSize;
               this.mainInventory[k] = this.mainInventory[this.currentItem];
            } else {
               l = 1;
            }

            this.mainInventory[this.currentItem] = new ItemStack(itemIn, l, metadataIn);
         }
      }
   }

   public void changeCurrentItem(int direction) {
      if (direction > 0) {
         direction = 1;
      }

      if (direction < 0) {
         direction = -1;
      }

      this.currentItem -= direction;

      while (this.currentItem < 0) {
         this.currentItem += 9;
      }

      while (this.currentItem >= 9) {
         this.currentItem -= 9;
      }
   }

   public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT) {
      int i = 0;

      for (int j = 0; j < this.mainInventory.length; j++) {
         ItemStack itemStack2 = this.mainInventory[j];
         if (itemStack2 != null
            && (itemIn == null || itemStack2.getItem() == itemIn)
            && (metadataIn <= -1 || itemStack2.getMetadata() == metadataIn)
            && (itemNBT == null || NBTUtil.func_181123_a(itemNBT, itemStack2.getTagCompound(), true))) {
            int k = removeCount <= 0 ? itemStack2.stackSize : Math.min(removeCount - i, itemStack2.stackSize);
            i += k;
            if (removeCount != 0) {
               this.mainInventory[j].stackSize -= k;
               if (this.mainInventory[j].stackSize == 0) {
                  this.mainInventory[j] = null;
               }

               if (removeCount > 0 && i >= removeCount) {
                  return i;
               }
            }
         }
      }

      for (int l = 0; l < this.armorInventory.length; l++) {
         ItemStack itemStack3 = this.armorInventory[l];
         if (itemStack3 != null
            && (itemIn == null || itemStack3.getItem() == itemIn)
            && (metadataIn <= -1 || itemStack3.getMetadata() == metadataIn)
            && (itemNBT == null || NBTUtil.func_181123_a(itemNBT, itemStack3.getTagCompound(), false))) {
            int m = removeCount <= 0 ? itemStack3.stackSize : Math.min(removeCount - i, itemStack3.stackSize);
            i += m;
            if (removeCount != 0) {
               this.armorInventory[l].stackSize -= m;
               if (this.armorInventory[l].stackSize == 0) {
                  this.armorInventory[l] = null;
               }

               if (removeCount > 0 && i >= removeCount) {
                  return i;
               }
            }
         }
      }

      if (this.itemStack != null) {
         if (itemIn != null && this.itemStack.getItem() != itemIn) {
            return i;
         }

         if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
            return i;
         }

         if (itemNBT != null && !NBTUtil.func_181123_a(itemNBT, this.itemStack.getTagCompound(), false)) {
            return i;
         }

         int n = removeCount <= 0 ? this.itemStack.stackSize : Math.min(removeCount - i, this.itemStack.stackSize);
         i += n;
         if (removeCount != 0) {
            this.itemStack.stackSize -= n;
            if (this.itemStack.stackSize == 0) {
               this.itemStack = null;
            }

            if (removeCount > 0 && i >= removeCount) {
               return i;
            }
         }
      }

      return i;
   }

   private int storePartialItemStack(ItemStack itemStackIn) {
      Item item = itemStackIn.getItem();
      int i = itemStackIn.stackSize;
      int j = this.storeItemStack(itemStackIn);
      if (j < 0) {
         j = this.getFirstEmptyStack();
      }

      if (j < 0) {
         return i;
      }

      if (this.mainInventory[j] == null) {
         this.mainInventory[j] = new ItemStack(item, 0, itemStackIn.getMetadata());
         if (itemStackIn.hasTagCompound()) {
            this.mainInventory[j].setTagCompound((NBTTagCompound)itemStackIn.getTagCompound().copy());
         }
      }

      int k = i;
      if (i > this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize) {
         k = this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize;
      }

      if (k > this.getInventoryStackLimit() - this.mainInventory[j].stackSize) {
         k = this.getInventoryStackLimit() - this.mainInventory[j].stackSize;
      }

      if (k == 0) {
         return i;
      }

      i -= k;
      this.mainInventory[j].stackSize += k;
      this.mainInventory[j].animationsToGo = 5;
      return i;
   }

   public void decrementAnimations() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            this.mainInventory[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem == i);
         }
      }
   }

   public boolean consumeInventoryItem(Item itemIn) {
      int i = this.getInventorySlotContainItem(itemIn);
      if (i < 0) {
         return false;
      }

      if (--this.mainInventory[i].stackSize <= 0) {
         this.mainInventory[i] = null;
      }

      return true;
   }

   public boolean hasItem(Item itemIn) {
      int i = this.getInventorySlotContainItem(itemIn);
      return i >= 0;
   }

   public boolean addItemStackToInventory(final ItemStack itemStackIn) {
      if (itemStackIn != null && itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
         try {
            if (itemStackIn.isItemDamaged()) {
               int i = this.getFirstEmptyStack();
               if (i >= 0) {
                  this.mainInventory[i] = ItemStack.copyItemStack(itemStackIn);
                  this.mainInventory[i].animationsToGo = 5;
                  itemStackIn.stackSize = 0;
                  return true;
               } else if (this.player.capabilities.isCreativeMode) {
                  itemStackIn.stackSize = 0;
                  return true;
               } else {
                  return false;
               }
            } else {
               int j;
               do {
                  j = itemStackIn.stackSize;
                  itemStackIn.stackSize = this.storePartialItemStack(itemStackIn);
               } while (itemStackIn.stackSize > 0 && itemStackIn.stackSize < j);

               if (itemStackIn.stackSize == j && this.player.capabilities.isCreativeMode) {
                  itemStackIn.stackSize = 0;
                  return true;
               } else {
                  return itemStackIn.stackSize < j;
               }
            }
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Item being added");
            crashReportCategory.addCrashSection("Item ID", Item.getIdFromItem(itemStackIn.getItem()));
            crashReportCategory.addCrashSection("Item data", itemStackIn.getMetadata());
            crashReportCategory.addCrashSectionCallable("Item name", new Callable<String>() {
               public String call() throws Exception {
                  return itemStackIn.getDisplayName();
               }
            });
            throw new ReportedException(crashReport);
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      ItemStack[] itemStacks = this.mainInventory;
      if (index >= this.mainInventory.length) {
         itemStacks = this.armorInventory;
         index -= this.mainInventory.length;
      }

      if (itemStacks[index] != null) {
         if (itemStacks[index].stackSize <= count) {
            ItemStack itemStack = itemStacks[index];
            itemStacks[index] = null;
            return itemStack;
         }

         ItemStack itemStack2 = itemStacks[index].splitStack(count);
         if (itemStacks[index].stackSize == 0) {
            itemStacks[index] = null;
         }

         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      ItemStack[] itemStacks = this.mainInventory;
      if (index >= this.mainInventory.length) {
         itemStacks = this.armorInventory;
         index -= this.mainInventory.length;
      }

      if (itemStacks[index] != null) {
         ItemStack itemStack = itemStacks[index];
         itemStacks[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      ItemStack[] itemStacks = this.mainInventory;
      if (index >= itemStacks.length) {
         index -= itemStacks.length;
         itemStacks = this.armorInventory;
      }

      itemStacks[index] = stack;
   }

   public float getStrVsBlock(Block blockIn) {
      float f = 1.0F;
      if (this.mainInventory[this.currentItem] != null) {
         f *= this.mainInventory[this.currentItem].getStrVsBlock(blockIn);
      }

      return f;
   }

   public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)i);
            this.mainInventory[i].writeToNBT(nbtTagCompound);
            nbtTagListIn.appendTag(nbtTagCompound);
         }
      }

      for (int j = 0; j < this.armorInventory.length; j++) {
         if (this.armorInventory[j] != null) {
            NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
            nbtTagCompound2.setByte("Slot", (byte)(j + 100));
            this.armorInventory[j].writeToNBT(nbtTagCompound2);
            nbtTagListIn.appendTag(nbtTagCompound2);
         }
      }

      return nbtTagListIn;
   }

   public void readFromNBT(NBTTagList nbtTagListIn) {
      this.mainInventory = new ItemStack[36];
      this.armorInventory = new ItemStack[4];

      for (int i = 0; i < nbtTagListIn.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = nbtTagListIn.getCompoundTagAt(i);
         int j = nbtTagCompound.getByte("Slot") & 255;
         ItemStack itemStack = ItemStack.loadItemStackFromNBT(nbtTagCompound);
         if (itemStack != null) {
            if (j >= 0 && j < this.mainInventory.length) {
               this.mainInventory[j] = itemStack;
            }

            if (j >= 100 && j < this.armorInventory.length + 100) {
               this.armorInventory[j - 100] = itemStack;
            }
         }
      }
   }

   @Override
   public int getSizeInventory() {
      return this.mainInventory.length + 4;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      ItemStack[] itemStacks = this.mainInventory;
      if (index >= itemStacks.length) {
         index -= itemStacks.length;
         itemStacks = this.armorInventory;
      }

      return itemStacks[index];
   }

   @Override
   public String getName() {
      return "container.inventory";
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
      return 64;
   }

   public boolean canHeldItemHarvest(Block blockIn) {
      if (blockIn.getMaterial().isToolNotRequired()) {
         return true;
      }

      ItemStack itemStack = this.getStackInSlot(this.currentItem);
      return itemStack != null ? itemStack.canHarvestBlock(blockIn) : false;
   }

   public ItemStack armorItemInSlot(int slotIn) {
      return this.armorInventory[slotIn];
   }

   public int getTotalArmorValue() {
      int i = 0;

      for (int j = 0; j < this.armorInventory.length; j++) {
         if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof ItemArmor) {
            int k = ((ItemArmor)this.armorInventory[j].getItem()).damageReduceAmount;
            i += k;
         }
      }

      return i;
   }

   public void damageArmor(float damage) {
      damage /= 4.0F;
      if (damage < 1.0F) {
         damage = 1.0F;
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
            this.armorInventory[i].damageItem((int)damage, this.player);
            if (this.armorInventory[i].stackSize == 0) {
               this.armorInventory[i] = null;
            }
         }
      }
   }

   public void dropAllItems() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            this.player.dropItem(this.mainInventory[i], true, false);
            this.mainInventory[i] = null;
         }
      }

      for (int j = 0; j < this.armorInventory.length; j++) {
         if (this.armorInventory[j] != null) {
            this.player.dropItem(this.armorInventory[j], true, false);
            this.armorInventory[j] = null;
         }
      }
   }

   @Override
   public void markDirty() {
      this.inventoryChanged = true;
   }

   public void setItemStack(ItemStack itemStackIn) {
      this.itemStack = itemStackIn;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0;
   }

   public boolean hasItemStack(ItemStack itemStackIn) {
      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null && this.armorInventory[i].isItemEqual(itemStackIn)) {
            return true;
         }
      }

      for (int j = 0; j < this.mainInventory.length; j++) {
         if (this.mainInventory[j] != null && this.mainInventory[j].isItemEqual(itemStackIn)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public void copyInventory(InventoryPlayer playerInventory) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         this.mainInventory[i] = ItemStack.copyItemStack(playerInventory.mainInventory[i]);
      }

      for (int j = 0; j < this.armorInventory.length; j++) {
         this.armorInventory[j] = ItemStack.copyItemStack(playerInventory.armorInventory[j]);
      }

      this.currentItem = playerInventory.currentItem;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         this.mainInventory[i] = null;
      }

      for (int j = 0; j < this.armorInventory.length; j++) {
         this.armorInventory[j] = null;
      }
   }

   public InventoryPlayer clone() {
      try {
         return (InventoryPlayer)super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
         throw new AssertionError();
      }
   }
}
