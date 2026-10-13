package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnaceOutput extends Slot {
   private EntityPlayer thePlayer;
   private int field_75228_b;
   private static final long a = 128512174167017L;

   public SlotFurnaceOutput(EntityPlayer var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.thePlayer = var1;
   }

   @Override
   public boolean isItemValid(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack decrStackSize(int var1) {
      if (this.getHasStack()) {
         this.field_75228_b = this.field_75228_b + Math.min(var1, this.getStack().stackSize);
      }

      return super.decrStackSize(var1);
   }

   @Override
   public void onPickupFromSlot(EntityPlayer var1, ItemStack var2) {
      this.onCrafting(var2);
      super.onPickupFromSlot(var1, var2);
   }

   @Override
   public void onCrafting(ItemStack var1, int var2) {
      this.field_75228_b += var2;
      this.onCrafting(var1);
   }

   @Override
   public void onCrafting(ItemStack var1) {
      var1.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75228_b);
      if (!this.thePlayer.worldObj.isRemote) {
         int var5 = this.field_75228_b;
         float var6 = FurnaceRecipes.instance().getSmeltingExperience(var1);
         if (var6 == 0.0F) {
            var5 = 0;
         } else if (var6 < 1.0F) {
            int var7 = MathHelper.floor_float(var5 * var6);
            if (var7 < MathHelper.ceiling_float_int(var5 * var6) && Math.random() < var5 * var6 - var7) {
               var7++;
            }

            var5 = var7;
         }

         while (var5 > 0) {
            int var8 = EntityXPOrb.getXPSplit(var5);
            var5 -= var8;
            this.thePlayer
               .worldObj
               .spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5, this.thePlayer.posZ + 0.5, var8));
         }
      }

      this.field_75228_b = 0;
      if (var1.getItem() == Items.iron_ingot) {
         this.thePlayer.triggerAchievement(AchievementList.acquireIron);
      }

      if (var1.getItem() == Items.cooked_fish) {
         this.thePlayer.triggerAchievement(AchievementList.cookFish);
      }
   }
}
