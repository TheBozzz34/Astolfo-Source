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
   private static final long a = 6242534706069L;

   public InventoryPlayer(EntityPlayer var1) {
      this.player = var1;
   }

   public ItemStack getCurrentItem() {
      return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
   }

   public static int getHotbarSize() {
      return 9;
   }

   private int getInventorySlotContainItem(Item var1) {
      for (int var5 = 0; var5 < this.mainInventory.length; var5++) {
         if (this.mainInventory[var5] != null && this.mainInventory[var5].getItem() == var1) {
            return var5;
         }
      }

      return -1;
   }

   private int getInventorySlotContainItemAndDamage(Item var1, int var2) {
      for (int var6 = 0; var6 < this.mainInventory.length; var6++) {
         if (this.mainInventory[var6] != null && this.mainInventory[var6].getItem() == var1 && this.mainInventory[var6].getMetadata() == var2) {
            return var6;
         }
      }

      return -1;
   }

   private int h(ItemStack var1) {
      for (int var5 = 0; var5 < this.mainInventory.length; var5++) {
         if (this.mainInventory[var5] != null
            && this.mainInventory[var5].getItem() == var1.getItem()
            && this.mainInventory[var5].isStackable()
            && this.mainInventory[var5].stackSize < this.mainInventory[var5].getMaxStackSize()
            && this.mainInventory[var5].stackSize < this.getInventoryStackLimit()
            && (!this.mainInventory[var5].getHasSubtypes() || this.mainInventory[var5].getMetadata() == var1.getMetadata())
            && ItemStack.areItemStackTagsEqual(this.mainInventory[var5], var1)) {
            return var5;
         }
      }

      return -1;
   }

   public int getFirstEmptyStack() {
      for (int var4 = 0; var4 < this.mainInventory.length; var4++) {
         if (this.mainInventory[var4] == null) {
            return var4;
         }
      }

      return -1;
   }

   public void setCurrentItem(Item var1, int var2, boolean var3, boolean var4) {
      ItemStack var8 = this.getCurrentItem();
      int var9 = var3 ? this.getInventorySlotContainItemAndDamage(var1, var2) : this.getInventorySlotContainItem(var1);
      if (var9 >= 0 && var9 < 9) {
         this.currentItem = var9;
      } else if (var4 && var1 != null) {
         int var10 = this.getFirstEmptyStack();
         if (var10 >= 0 && var10 < 9) {
            this.currentItem = var10;
         }

         if (var8 == null || !var8.isItemEnchantable() || this.getInventorySlotContainItemAndDamage(var8.getItem(), var8.getItemDamage()) != this.currentItem) {
            int var11 = this.getInventorySlotContainItemAndDamage(var1, var2);
            int var12;
            if (var11 >= 0) {
               var12 = this.mainInventory[var11].stackSize;
               this.mainInventory[var11] = this.mainInventory[this.currentItem];
            } else {
               var12 = 1;
            }

            this.mainInventory[this.currentItem] = new ItemStack(var1, var12, var2);
         }
      }
   }

   public void changeCurrentItem(int var1) {
      if (var1 > 0) {
         var1 = 1;
      }

      if (var1 < 0) {
         var1 = -1;
      }

      this.currentItem -= var1;

      while (this.currentItem < 0) {
         this.currentItem += 9;
      }

      while (this.currentItem >= 9) {
         this.currentItem -= 9;
      }
   }

   public int clearMatchingItems(Item var1, int var2, int var3, NBTTagCompound var4) {
      int var8 = 0;

      for (int var9 = 0; var9 < this.mainInventory.length; var9++) {
         ItemStack var10 = this.mainInventory[var9];
         if (var10 != null
            && (var1 == null || var10.getItem() == var1)
            && (var2 <= -1 || var10.getMetadata() == var2)
            && (var4 == null || NBTUtil.func_181123_a(var4, var10.getTagCompound(), true))) {
            int var11 = var3 <= 0 ? var10.stackSize : Math.min(var3 - var8, var10.stackSize);
            var8 += var11;
            if (var3 != 0) {
               this.mainInventory[var9].stackSize -= var11;
               if (this.mainInventory[var9].stackSize == 0) {
                  this.mainInventory[var9] = null;
               }

               if (var3 > 0 && var8 >= var3) {
                  return var8;
               }
            }
         }
      }

      for (int var12 = 0; var12 < this.armorInventory.length; var12++) {
         ItemStack var14 = this.armorInventory[var12];
         if (var14 != null
            && (var1 == null || var14.getItem() == var1)
            && (var2 <= -1 || var14.getMetadata() == var2)
            && (var4 == null || NBTUtil.func_181123_a(var4, var14.getTagCompound(), false))) {
            int var15 = var3 <= 0 ? var14.stackSize : Math.min(var3 - var8, var14.stackSize);
            var8 += var15;
            if (var3 != 0) {
               this.armorInventory[var12].stackSize -= var15;
               if (this.armorInventory[var12].stackSize == 0) {
                  this.armorInventory[var12] = null;
               }

               if (var3 > 0 && var8 >= var3) {
                  return var8;
               }
            }
         }
      }

      if (this.itemStack != null) {
         if (var1 != null && this.itemStack.getItem() != var1) {
            return var8;
         }

         if (var2 > -1 && this.itemStack.getMetadata() != var2) {
            return var8;
         }

         if (var4 != null && !NBTUtil.func_181123_a(var4, this.itemStack.getTagCompound(), false)) {
            return var8;
         }

         int var13 = var3 <= 0 ? this.itemStack.stackSize : Math.min(var3 - var8, this.itemStack.stackSize);
         var8 += var13;
         if (var3 != 0) {
            this.itemStack.stackSize -= var13;
            if (this.itemStack.stackSize == 0) {
               this.itemStack = null;
            }

            if (var3 > 0 && var8 >= var3) {
               return var8;
            }
         }
      }

      return var8;
   }

   private int o(ItemStack var1) {
      Item var5 = var1.getItem();
      int var6 = var1.stackSize;
      int var7 = this.h(var1);
      if (var7 < 0) {
         var7 = this.getFirstEmptyStack();
      }

      if (var7 < 0) {
         return var6;
      }

      if (this.mainInventory[var7] == null) {
         this.mainInventory[var7] = new ItemStack(var5, 0, var1.getMetadata());
         if (var1.hasTagCompound()) {
            this.mainInventory[var7].setTagCompound((NBTTagCompound)var1.getTagCompound().copy());
         }
      }

      int var8 = var6;
      if (var6 > this.mainInventory[var7].getMaxStackSize() - this.mainInventory[var7].stackSize) {
         var8 = this.mainInventory[var7].getMaxStackSize() - this.mainInventory[var7].stackSize;
      }

      if (var8 > this.getInventoryStackLimit() - this.mainInventory[var7].stackSize) {
         var8 = this.getInventoryStackLimit() - this.mainInventory[var7].stackSize;
      }

      if (var8 == 0) {
         return var6;
      }

      var6 -= var8;
      this.mainInventory[var7].stackSize += var8;
      this.mainInventory[var7].animationsToGo = 5;
      return var6;
   }

   public void Y() {
      for (int var4 = 0; var4 < this.mainInventory.length; var4++) {
         if (this.mainInventory[var4] != null) {
            this.mainInventory[var4].updateAnimation(this.player.worldObj, this.player, var4, this.currentItem == var4);
         }
      }
   }

   public boolean consumeInventoryItem(Item var1) {
      int var5 = this.getInventorySlotContainItem(var1);
      if (var5 < 0) {
         return false;
      }

      if (--this.mainInventory[var5].stackSize <= 0) {
         this.mainInventory[var5] = null;
      }

      return true;
   }

   public boolean hasItem(Item var1) {
      int var5 = this.getInventorySlotContainItem(var1);
      return var5 >= 0;
   }

   public boolean addItemStackToInventory(final ItemStack var1) {
      if (var1 != null && var1.stackSize != 0 && var1.getItem() != null) {
         try {
            if (var1.isItemDamaged()) {
               int var9 = this.getFirstEmptyStack();
               if (var9 >= 0) {
                  this.mainInventory[var9] = ItemStack.copyItemStack(var1);
                  this.mainInventory[var9].animationsToGo = 5;
                  var1.stackSize = 0;
                  return true;
               } else if (this.player.capabilities.isCreativeMode) {
                  var1.stackSize = 0;
                  return true;
               } else {
                  return false;
               }
            } else {
               int var5;
               do {
                  var5 = var1.stackSize;
                  var1.stackSize = this.o(var1);
               } while (var1.stackSize > 0 && var1.stackSize < var5);

               if (var1.stackSize == var5 && this.player.capabilities.isCreativeMode) {
                  var1.stackSize = 0;
                  return true;
               } else {
                  return var1.stackSize < var5;
               }
            }
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.makeCrashReport(var8, "Adding item to inventory");
            CrashReportCategory var7 = var6.makeCategory("Item being added");
            var7.addCrashSection("Item ID", Item.getIdFromItem(var1.getItem()));
            var7.addCrashSection("Item data", var1.getMetadata());
            var7.addCrashSectionCallable("Item name", new Callable<String>() {
               public String call() throws Exception {
                  return var1.getDisplayName();
               }
            });
            throw new ReportedException(var6);
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      ItemStack[] var6 = this.mainInventory;
      if (var1 >= this.mainInventory.length) {
         var6 = this.armorInventory;
         var1 -= this.mainInventory.length;
      }

      if (var6[var1] != null) {
         if (var6[var1].stackSize <= var2) {
            ItemStack var8 = var6[var1];
            var6[var1] = null;
            return var8;
         }

         ItemStack var7 = var6[var1].splitStack(var2);
         if (var6[var1].stackSize == 0) {
            var6[var1] = null;
         }

         return var7;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      ItemStack[] var5 = this.mainInventory;
      if (var1 >= this.mainInventory.length) {
         var5 = this.armorInventory;
         var1 -= this.mainInventory.length;
      }

      if (var5[var1] != null) {
         ItemStack var6 = var5[var1];
         var5[var1] = null;
         return var6;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      ItemStack[] var6 = this.mainInventory;
      if (var1 >= var6.length) {
         var1 -= var6.length;
         var6 = this.armorInventory;
      }

      var6[var1] = var2;
   }

   public float getStrVsBlock(Block var1) {
      float var4 = 1.0F;
      if (this.mainInventory[this.currentItem] != null) {
         var4 *= this.mainInventory[this.currentItem].getStrVsBlock(var1);
      }

      return var4;
   }

   public NBTTagList writeToNBT(NBTTagList var1) {
      for (int var5 = 0; var5 < this.mainInventory.length; var5++) {
         if (this.mainInventory[var5] != null) {
            NBTTagCompound var6 = new NBTTagCompound();
            var6.setByte("Slot", (byte)var5);
            this.mainInventory[var5].writeToNBT(var6);
            var1.appendTag(var6);
         }
      }

      for (int var7 = 0; var7 < this.armorInventory.length; var7++) {
         if (this.armorInventory[var7] != null) {
            NBTTagCompound var8 = new NBTTagCompound();
            var8.setByte("Slot", (byte)(var7 + 100));
            this.armorInventory[var7].writeToNBT(var8);
            var1.appendTag(var8);
         }
      }

      return var1;
   }

   public void readFromNBT(NBTTagList var1) {
      this.mainInventory = new ItemStack[36];
      this.armorInventory = new ItemStack[4];

      for (int var5 = 0; var5 < var1.tagCount(); var5++) {
         NBTTagCompound var6 = var1.getCompoundTagAt(var5);
         int var7 = var6.getByte("Slot") & 255;
         ItemStack var8 = ItemStack.loadItemStackFromNBT(var6);
         if (var8 != null) {
            if (var7 >= 0 && var7 < this.mainInventory.length) {
               this.mainInventory[var7] = var8;
            }

            if (var7 >= 100 && var7 < this.armorInventory.length + 100) {
               this.armorInventory[var7 - 100] = var8;
            }
         }
      }
   }

   @Override
   public int getSizeInventory() {
      return this.mainInventory.length + 4;
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      ItemStack[] var5 = this.mainInventory;
      if (var1 >= var5.length) {
         var1 -= var5.length;
         var5 = this.armorInventory;
      }

      return var5[var1];
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

   public boolean canHeldItemHarvest(Block var1) {
      if (var1.getMaterial().isToolNotRequired()) {
         return true;
      }

      ItemStack var5 = this.getStackInSlot(this.currentItem);
      return var5 != null ? var5.canHarvestBlock(var1) : false;
   }

   public ItemStack armorItemInSlot(int var1) {
      return this.armorInventory[var1];
   }

   public int getTotalArmorValue() {
      int var4 = 0;

      for (int var5 = 0; var5 < this.armorInventory.length; var5++) {
         if (this.armorInventory[var5] != null && this.armorInventory[var5].getItem() instanceof ItemArmor) {
            int var6 = ((ItemArmor)this.armorInventory[var5].getItem()).damageReduceAmount;
            var4 += var6;
         }
      }

      return var4;
   }

   public void damageArmor(float var1) {
      var1 /= 4.0F;
      if (var1 < 1.0F) {
         var1 = 1.0F;
      }

      for (int var5 = 0; var5 < this.armorInventory.length; var5++) {
         if (this.armorInventory[var5] != null && this.armorInventory[var5].getItem() instanceof ItemArmor) {
            this.armorInventory[var5].damageItem((int)var1, this.player);
            if (this.armorInventory[var5].stackSize == 0) {
               this.armorInventory[var5] = null;
            }
         }
      }
   }

   public void dropAllItems() {
      for (int var4 = 0; var4 < this.mainInventory.length; var4++) {
         if (this.mainInventory[var4] != null) {
            this.player.dropItem(this.mainInventory[var4], true, false);
            this.mainInventory[var4] = null;
         }
      }

      for (int var5 = 0; var5 < this.armorInventory.length; var5++) {
         if (this.armorInventory[var5] != null) {
            this.player.dropItem(this.armorInventory[var5], true, false);
            this.armorInventory[var5] = null;
         }
      }
   }

   @Override
   public void markDirty() {
      this.inventoryChanged = true;
   }

   public void setItemStack(ItemStack var1) {
      this.itemStack = var1;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.player.isDead ? false : var1.getDistanceSqToEntity(this.player) <= 64.0;
   }

   public boolean hasItemStack(ItemStack var1) {
      for (int var5 = 0; var5 < this.armorInventory.length; var5++) {
         if (this.armorInventory[var5] != null && this.armorInventory[var5].isItemEqual(var1)) {
            return true;
         }
      }

      for (int var6 = 0; var6 < this.mainInventory.length; var6++) {
         if (this.mainInventory[var6] != null && this.mainInventory[var6].isItemEqual(var1)) {
            return true;
         }
      }

      return false;
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

   public void copyInventory(InventoryPlayer var1) {
      for (int var5 = 0; var5 < this.mainInventory.length; var5++) {
         this.mainInventory[var5] = ItemStack.copyItemStack(var1.mainInventory[var5]);
      }

      for (int var6 = 0; var6 < this.armorInventory.length; var6++) {
         this.armorInventory[var6] = ItemStack.copyItemStack(var1.armorInventory[var6]);
      }

      this.currentItem = var1.currentItem;
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
      for (int var4 = 0; var4 < this.mainInventory.length; var4++) {
         this.mainInventory[var4] = null;
      }

      for (int var5 = 0; var5 < this.armorInventory.length; var5++) {
         this.armorInventory[var5] = null;
      }
   }

   public InventoryPlayer clone() {
      try {
         return (InventoryPlayer)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }
}
