package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
   private ItemStack[] minecartContainerItems = new ItemStack[36];
   private boolean dropContentsWhenDead = true;
   private static final long cb = 55121102546858L;

   public EntityMinecartContainer(World var1) {
      super(var1);
   }

   public EntityMinecartContainer(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void killMinecart(DamageSource var1) {
      super.killMinecart(var1);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         InventoryHelper.dropInventoryItems(this.worldObj, this, this);
      }
   }

   @Override
   public ItemStack getStackInSlot(int var1) {
      return this.minecartContainerItems[var1];
   }

   @Override
   public ItemStack decrStackSize(int var1, int var2) {
      if (this.minecartContainerItems[var1] != null) {
         if (this.minecartContainerItems[var1].stackSize <= var2) {
            ItemStack var7 = this.minecartContainerItems[var1];
            this.minecartContainerItems[var1] = null;
            return var7;
         }

         ItemStack var6 = this.minecartContainerItems[var1].splitStack(var2);
         if (this.minecartContainerItems[var1].stackSize == 0) {
            this.minecartContainerItems[var1] = null;
         }

         return var6;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int var1) {
      if (this.minecartContainerItems[var1] != null) {
         ItemStack var5 = this.minecartContainerItems[var1];
         this.minecartContainerItems[var1] = null;
         return var5;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int var1, ItemStack var2) {
      this.minecartContainerItems[var1] = var2;
      if (var2 != null && var2.stackSize > this.getInventoryStackLimit()) {
         var2.stackSize = this.getInventoryStackLimit();
      }
   }

   @Override
   public void markDirty() {
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.isDead ? false : var1.getDistanceSqToEntity(this) <= 64.0;
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
   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : "container.minecart";
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public void travelToDimension(int var1) {
      this.dropContentsWhenDead = false;
      super.travelToDimension(var1);
   }

   @Override
   public void setDead() {
      if (this.dropContentsWhenDead) {
         InventoryHelper.dropInventoryItems(this.worldObj, this, this);
      }

      super.setDead();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      NBTTagList var5 = new NBTTagList();

      for (int var6 = 0; var6 < this.minecartContainerItems.length; var6++) {
         if (this.minecartContainerItems[var6] != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("Slot", (byte)var6);
            this.minecartContainerItems[var6].writeToNBT(var7);
            var5.appendTag(var7);
         }
      }

      var1.setTag("Items", var5);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      NBTTagList var5 = var1.getTagList("Items", 10);
      this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

      for (int var6 = 0; var6 < var5.tagCount(); var6++) {
         NBTTagCompound var7 = var5.getCompoundTagAt(var6);
         int var8 = var7.getByte("Slot") & 255;
         if (var8 >= 0 && var8 < this.minecartContainerItems.length) {
            this.minecartContainerItems[var8] = ItemStack.loadItemStackFromNBT(var7);
         }
      }
   }

   @Override
   public boolean interactFirst(EntityPlayer var1) {
      if (!this.worldObj.isRemote) {
         var1.displayGUIChest(this);
      }

      return true;
   }

   @Override
   public void applyDrag() {
      int var3 = 15 - Container.calcRedstoneFromInventory(this);
      float var4 = 0.98F + var3 * 0.001F;
      this.motionX *= var4;
      this.motionY *= 0.0;
      this.motionZ *= var4;
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
      return false;
   }

   @Override
   public void setLockCode(LockCode var1) {
   }

   @Override
   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   @Override
   public void clear() {
      for (int var4 = 0; var4 < this.minecartContainerItems.length; var4++) {
         this.minecartContainerItems[var4] = null;
      }
   }
}
