package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest extends InventoryBasic {
   private TileEntityEnderChest associatedChest;

   public InventoryEnderChest() {
      super("container.enderchest", false, 27);
   }

   public void setChestTileEntity(TileEntityEnderChest chestTileEntity) {
      this.associatedChest = chestTileEntity;
   }

   public void loadInventoryFromNBT(NBTTagList nbtTagList) {
      for (int i = 0; i < this.getSizeInventory(); i++) {
         this.setInventorySlotContents(i, (ItemStack)null);
      }

      for (int j = 0; j < nbtTagList.tagCount(); j++) {
         NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(j);
         int k = nbtTagCompound.getByte("Slot") & 255;
         if (k >= 0 && k < this.getSizeInventory()) {
            this.setInventorySlotContents(k, ItemStack.loadItemStackFromNBT(nbtTagCompound));
         }
      }
   }

   public NBTTagList saveInventoryToNBT() {
      NBTTagList nbtTagList = new NBTTagList();

      for (int i = 0; i < this.getSizeInventory(); i++) {
         ItemStack itemStack = this.getStackInSlot(i);
         if (itemStack != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setByte("Slot", (byte)i);
            itemStack.writeToNBT(nbtTagCompound);
            nbtTagList.appendTag(nbtTagCompound);
         }
      }

      return nbtTagList;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUseableByPlayer(player);
   }

   @Override
   public void openInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.openChest();
      }

      super.openInventory(player);
   }

   @Override
   public void closeInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.closeChest();
      }

      super.closeInventory(player);
      this.associatedChest = null;
   }
}
