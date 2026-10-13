package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.AchievementList;

public class SlotCrafting extends Slot {
   private final InventoryCrafting craftMatrix;
   private final EntityPlayer thePlayer;
   private int amountCrafted;
   private static final long c = 90705289458458L;

   public SlotCrafting(EntityPlayer var1, InventoryCrafting var2, IInventory var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.thePlayer = var1;
      this.craftMatrix = var2;
   }

   @Override
   public boolean isItemValid(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack decrStackSize(int var1) {
      if (this.getHasStack()) {
         this.amountCrafted = this.amountCrafted + Math.min(var1, this.getStack().stackSize);
      }

      return super.decrStackSize(var1);
   }

   @Override
   public void onCrafting(ItemStack var1, int var2) {
      this.amountCrafted += var2;
      this.onCrafting(var1);
   }

   @Override
   public void onCrafting(ItemStack var1) {
      if (this.amountCrafted > 0) {
         var1.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
      }

      this.amountCrafted = 0;
      if (var1.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
         this.thePlayer.triggerAchievement(AchievementList.buildWorkBench);
      }

      if (var1.getItem() instanceof ItemPickaxe) {
         this.thePlayer.triggerAchievement(AchievementList.buildPickaxe);
      }

      if (var1.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
         this.thePlayer.triggerAchievement(AchievementList.buildFurnace);
      }

      if (var1.getItem() instanceof ItemHoe) {
         this.thePlayer.triggerAchievement(AchievementList.buildHoe);
      }

      if (var1.getItem() == Items.bread) {
         this.thePlayer.triggerAchievement(AchievementList.makeBread);
      }

      if (var1.getItem() == Items.cake) {
         this.thePlayer.triggerAchievement(AchievementList.bakeCake);
      }

      if (var1.getItem() instanceof ItemPickaxe && ((ItemPickaxe)var1.getItem()).getToolMaterial() != Item.ToolMaterial.WOOD) {
         this.thePlayer.triggerAchievement(AchievementList.buildBetterPickaxe);
      }

      if (var1.getItem() instanceof ItemSword) {
         this.thePlayer.triggerAchievement(AchievementList.buildSword);
      }

      if (var1.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
         this.thePlayer.triggerAchievement(AchievementList.enchantments);
      }

      if (var1.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
         this.thePlayer.triggerAchievement(AchievementList.bookcase);
      }

      if (var1.getItem() == Items.golden_apple && var1.getMetadata() == 1) {
         this.thePlayer.triggerAchievement(AchievementList.overpowered);
      }
   }

   @Override
   public void onPickupFromSlot(EntityPlayer var1, ItemStack var2) {
      this.onCrafting(var2);
      ItemStack[] var6 = CraftingManager.getInstance().func_180303_b(this.craftMatrix, var1.worldObj);

      for (int var7 = 0; var7 < var6.length; var7++) {
         ItemStack var8 = this.craftMatrix.getStackInSlot(var7);
         ItemStack var9 = var6[var7];
         if (var8 != null) {
            this.craftMatrix.decrStackSize(var7, 1);
         }

         if (var9 != null) {
            if (this.craftMatrix.getStackInSlot(var7) == null) {
               this.craftMatrix.setInventorySlotContents(var7, var9);
            } else if (!this.thePlayer.inventory.addItemStackToInventory(var9)) {
               this.thePlayer.dropPlayerItemWithRandomChoice(var9, false);
            }
         }
      }
   }
}
