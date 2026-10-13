package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot {
   private final InventoryMerchant theMerchantInventory;
   private EntityPlayer thePlayer;
   private int field_75231_g;
   private final IMerchant theMerchant;
   private static final long a = 139798932726083L;

   public SlotMerchantResult(EntityPlayer var1, IMerchant var2, InventoryMerchant var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.thePlayer = var1;
      this.theMerchant = var2;
      this.theMerchantInventory = var3;
   }

   @Override
   public boolean isItemValid(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack decrStackSize(int var1) {
      if (this.getHasStack()) {
         this.field_75231_g = this.field_75231_g + Math.min(var1, this.getStack().stackSize);
      }

      return super.decrStackSize(var1);
   }

   @Override
   public void onCrafting(ItemStack var1, int var2) {
      this.field_75231_g += var2;
      this.onCrafting(var1);
   }

   @Override
   public void onCrafting(ItemStack var1) {
      var1.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75231_g);
      this.field_75231_g = 0;
   }

   @Override
   public void onPickupFromSlot(EntityPlayer var1, ItemStack var2) {
      this.onCrafting(var2);
      MerchantRecipe var6 = this.theMerchantInventory.getCurrentRecipe();
      if (var6 != null) {
         ItemStack var7 = this.theMerchantInventory.getStackInSlot(0);
         ItemStack var8 = this.theMerchantInventory.getStackInSlot(1);
         if (this.doTrade(var6, var7, var8) || this.doTrade(var6, var8, var7)) {
            this.theMerchant.useRecipe(var6);
            var1.triggerAchievement(StatList.timesTradedWithVillagerStat);
            if (var7 != null && var7.stackSize <= 0) {
               var7 = null;
            }

            if (var8 != null && var8.stackSize <= 0) {
               var8 = null;
            }

            this.theMerchantInventory.setInventorySlotContents(0, var7);
            this.theMerchantInventory.setInventorySlotContents(1, var8);
         }
      }
   }

   private boolean doTrade(MerchantRecipe var1, ItemStack var2, ItemStack var3) {
      ItemStack var7 = var1.getItemToBuy();
      ItemStack var8 = var1.getSecondItemToBuy();
      if (var2 != null && var2.getItem() == var7.getItem()) {
         if (var8 != null && var3 != null && var8.getItem() == var3.getItem()) {
            var2.stackSize = var2.stackSize - var7.stackSize;
            var3.stackSize = var3.stackSize - var8.stackSize;
            return true;
         }

         if (var8 == null && var3 == null) {
            var2.stackSize = var2.stackSize - var7.stackSize;
            return true;
         }
      }

      return false;
   }
}
