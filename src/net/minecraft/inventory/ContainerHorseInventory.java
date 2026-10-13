package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {
   private IInventory horseInventory;
   private EntityHorse theHorse;
   private static final long a = 99667126036234L;

   public ContainerHorseInventory(IInventory var1, IInventory var2, final EntityHorse var3, EntityPlayer var4) {
      this.horseInventory = var2;
      this.theHorse = var3;
      byte var8 = 3;
      var2.openInventory(var4);
      int var9 = (var8 - 4) * 18;
      this.addSlotToContainer(new Slot(var2, 0, 8, 18) {
         private static final long a = 123320094596717L;

         @Override
         public boolean isItemValid(ItemStack var1) {
            return super.isItemValid(var1) && var1.getItem() == Items.saddle && !this.getHasStack();
         }
      });
      this.addSlotToContainer(new Slot(var2, 1, 8, 36) {
         private static final long a = 32252764682274L;

         @Override
         public boolean isItemValid(ItemStack var1) {
            return super.isItemValid(var1) && var3.canWearArmor() && EntityHorse.isArmorItem(var1.getItem());
         }

         @Override
         public boolean canBeHovered() {
            return var3.canWearArmor();
         }
      });
      if (var3.isChested()) {
         for (int var10 = 0; var10 < var8; var10++) {
            for (int var11 = 0; var11 < 5; var11++) {
               this.addSlotToContainer(new Slot(var2, 2 + var11 + var10 * 5, 80 + var11 * 18, 18 + var10 * 18));
            }
         }
      }

      for (int var12 = 0; var12 < 3; var12++) {
         for (int var14 = 0; var14 < 9; var14++) {
            this.addSlotToContainer(new Slot(var1, var14 + var12 * 9 + 9, 8 + var14 * 18, 102 + var12 * 18 + var9));
         }
      }

      for (int var13 = 0; var13 < 9; var13++) {
         this.addSlotToContainer(new Slot(var1, var13, 8 + var13 * 18, 160 + var9));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.horseInventory.isUseableByPlayer(var1) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(var1) < 8.0F;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if (var2 < this.horseInventory.getSizeInventory()) {
            if (!this.mergeItemStack(var8, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (this.getSlot(1).isItemValid(var8) && !this.getSlot(1).getHasStack()) {
            if (!this.mergeItemStack(var8, 1, 2, false)) {
               return null;
            }
         } else if (this.getSlot(0).isItemValid(var8)) {
            if (!this.mergeItemStack(var8, 0, 1, false)) {
               return null;
            }
         } else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(var8, 2, this.horseInventory.getSizeInventory(), false)) {
            return null;
         }

         if (var8.stackSize == 0) {
            var7.putStack((ItemStack)null);
         } else {
            var7.onSlotChanged();
         }
      }

      return var6;
   }

   @Override
   public void onContainerClosed(EntityPlayer var1) {
      super.onContainerClosed(var1);
      this.horseInventory.closeInventory(var1);
   }
}
