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
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      if (var3.fishEntity != null) {
         int var7 = var3.fishEntity.handleHookRetraction();
         var1.damageItem(var7, var3);
         var3.swingItem();
      } else {
         var2.playSoundAtEntity(var3, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!var2.isRemote) {
            var2.spawnEntityInWorld(new EntityFishHook(var2, var3));
         }

         var3.swingItem();
         var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      }

      return var1;
   }

   @Override
   public boolean isItemTool(ItemStack var1) {
      return super.isItemTool(var1);
   }

   @Override
   public int getItemEnchantability() {
      return 1;
   }
}
