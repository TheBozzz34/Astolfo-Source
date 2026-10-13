package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest extends InventoryBasic {
   private TileEntityEnderChest associatedChest;
   private static final long a = 92070153852673L;

   public InventoryEnderChest() {
      super("container.enderchest", false, 27);
   }

   public void setChestTileEntity(TileEntityEnderChest var1) {
      this.associatedChest = var1;
   }

   public void loadInventoryFromNBT(NBTTagList var1) {
      for (int var5 = 0; var5 < this.getSizeInventory(); var5++) {
         this.setInventorySlotContents(var5, (ItemStack)null);
      }

      for (int var8 = 0; var8 < var1.tagCount(); var8++) {
         NBTTagCompound var6 = var1.getCompoundTagAt(var8);
         int var7 = var6.getByte("Slot") & 255;
         if (var7 >= 0 && var7 < this.getSizeInventory()) {
            this.setInventorySlotContents(var7, ItemStack.loadItemStackFromNBT(var6));
         }
      }
   }

   public NBTTagList saveInventoryToNBT() {
      NBTTagList var4 = new NBTTagList();

      for (int var5 = 0; var5 < this.getSizeInventory(); var5++) {
         ItemStack var6 = this.getStackInSlot(var5);
         if (var6 != null) {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("Slot", (byte)var5);
            var6.writeToNBT(var7);
            var4.appendTag(var7);
         }
      }

      return var4;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer var1) {
      return this.associatedChest != null && !this.associatedChest.canBeUsed(var1) ? false : super.isUseableByPlayer(var1);
   }

   @Override
   public void openInventory(EntityPlayer var1) {
      if (this.associatedChest != null) {
         this.associatedChest.A();
      }

      super.openInventory(var1);
   }

   @Override
   public void closeInventory(EntityPlayer var1) {
      if (this.associatedChest != null) {
         this.associatedChest.C();
      }

      super.closeInventory(var1);
      this.associatedChest = null;
   }
}
