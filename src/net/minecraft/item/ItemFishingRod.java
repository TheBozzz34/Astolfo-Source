package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {
   private static final long a = 48710590694917L;
   private static final long d = 2104916987338555456L;

   public ItemFishingRod() {
      this.setMaxDamage((int)d);
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @Override
   public boolean isFull3D() {
      return true;
   }

   @Override
   public boolean shouldRotateAroundWhenRendering() {
      return true;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (playerIn.fishEntity != null) {
         int i = playerIn.fishEntity.handleHookRetraction();
         itemStackIn.damageItem(i, playerIn);
         playerIn.swingItem();
      } else {
         worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(new EntityFishHook(worldIn, playerIn));
         }

         playerIn.swingItem();
         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      }

      return itemStackIn;
   }

   @Override
   public boolean isItemTool(ItemStack stack) {
      return super.isItemTool(stack);
   }

   @Override
   public int getItemEnchantability() {
      return 1;
   }
}
