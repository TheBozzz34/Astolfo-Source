package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class ContainerBrewingStand extends Container {
   private IInventory tileBrewingStand;
   private final Slot theSlot;
   private int brewTime;
   private static final long a = 86872862217658L;

   public ContainerBrewingStand(InventoryPlayer var1, IInventory var2) {
      this.tileBrewingStand = var2;
      this.addSlotToContainer(new ContainerBrewingStand.Potion(var1.player, var2, 0, 56, 46));
      this.addSlotToContainer(new ContainerBrewingStand.Potion(var1.player, var2, 1, 79, 53));
      this.addSlotToContainer(new ContainerBrewingStand.Potion(var1.player, var2, 2, 102, 46));
      this.theSlot = this.addSlotToContainer(new ContainerBrewingStand.Ingredient(var2, 3, 79, 17));

      for (int var6 = 0; var6 < 3; var6++) {
         for (int var7 = 0; var7 < 9; var7++) {
            this.addSlotToContainer(new Slot(var1, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
         }
      }

      for (int var8 = 0; var8 < 9; var8++) {
         this.addSlotToContainer(new Slot(var1, var8, 8 + var8 * 18, 142));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting var1) {
      super.onCraftGuiOpened(var1);
      var1.sendAllWindowProperties(this, this.tileBrewingStand);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int var4 = 0; var4 < this.crafters.size(); var4++) {
         ICrafting var5 = this.crafters.get(var4);
         if (this.brewTime != this.tileBrewingStand.getField(0)) {
            var5.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getField(0));
         }
      }

      this.brewTime = this.tileBrewingStand.getField(0);
   }

   @Override
   public void updateProgressBar(int var1, int var2) {
      this.tileBrewingStand.setField(var1, var2);
   }

   @Override
   public boolean canInteractWith(EntityPlayer var1) {
      return this.tileBrewingStand.isUseableByPlayer(var1);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer var1, int var2) {
      ItemStack var6 = null;
      Slot var7 = this.inventorySlots.get(var2);
      if (var7 != null && var7.getHasStack()) {
         ItemStack var8 = var7.getStack();
         var6 = var8.copy();
         if ((var2 < 0 || var2 > 2) && var2 != 3) {
            if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(var8)) {
               if (!this.mergeItemStack(var8, 3, 4, false)) {
                  return null;
               }
            } else if (ContainerBrewingStand.Potion.canHoldPotion(var6)) {
               if (!this.mergeItemStack(var8, 0, 3, false)) {
                  return null;
               }
            } else if (var2 >= 4 && var2 < 31) {
               if (!this.mergeItemStack(var8, 31, 40, false)) {
                  return null;
               }
            } else if (var2 >= 31 && var2 < 40) {
               if (!this.mergeItemStack(var8, 4, 31, false)) {
                  return null;
               }
            } else if (!this.mergeItemStack(var8, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.mergeItemStack(var8, 4, 40, true)) {
               return null;
            }

            var7.onSlotChange(var8, var6);
         }

         if (var8.stackSize == 0) {
            var7.putStack((ItemStack)null);
         } else {
            var7.onSlotChanged();
         }

         if (var8.stackSize == var6.stackSize) {
            return null;
         }

         var7.onPickupFromSlot(var1, var8);
      }

      return var6;
   }

   public class Ingredient extends Slot {
      private static final long a = 87202970613027L;
      private static final long c = 7571432962323906624L;

      public Ingredient(IInventory var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
      }

      @Override
      public boolean isItemValid(ItemStack var1) {
         return var1 != null ? var1.getItem().isPotionIngredient(var1) : false;
      }

      @Override
      public int getSlotStackLimit() {
         return (int)c;
      }
   }

   public static class Potion extends Slot {
      private EntityPlayer player;
      private static final long a = 22234730701749L;

      public Potion(EntityPlayer var1, IInventory var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
         this.player = var1;
      }

      @Override
      public boolean isItemValid(ItemStack var1) {
         return canHoldPotion(var1);
      }

      @Override
      public int getSlotStackLimit() {
         return 1;
      }

      @Override
      public void onPickupFromSlot(EntityPlayer var1, ItemStack var2) {
         if (var2.getItem() == Items.potionitem && var2.getMetadata() > 0) {
            this.player.triggerAchievement(AchievementList.potion);
         }

         super.onPickupFromSlot(var1, var2);
      }

      public static boolean canHoldPotion(ItemStack var0) {
         return var0 != null && (var0.getItem() == Items.potionitem || var0.getItem() == Items.glass_bottle);
      }
   }
}
