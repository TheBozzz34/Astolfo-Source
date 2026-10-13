package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemBucketMilk extends Item {
   private static final long a = 75705823635470L;
   private static final long b = 7373298750795546656L;

   public ItemBucketMilk() {
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemUseFinish(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var3.capabilities.isCreativeMode) {
         var1.stackSize--;
      }

      if (!var2.isRemote) {
         var3.clearActivePotions();
      }

      var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return var1.stackSize <= 0 ? new ItemStack(Items.bucket) : var1;
   }

   @Override
   public int getMaxItemUseDuration(ItemStack var1) {
      return (int)b;
   }

   @Override
   public EnumAction getItemUseAction(ItemStack var1) {
      return EnumAction.DRINK;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      var3.setItemInUse(var1, this.getMaxItemUseDuration(var1));
      return var1;
   }
}
