package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemBow extends Item {
   public static final String[] bowPullIconNameArray = new String[]{"pulling_0", "pulling_1", "pulling_2"};
   private static final long a = 41198642555424L;

   public ItemBow() {
      this.maxStackSize = 1;
      this.setMaxDamage(384);
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   @Override
   public void onPlayerStoppedUsing(ItemStack var1, World var2, EntityPlayer var3, int var4) {
      boolean var8 = var3.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, var1) > 0;
      if (var8 || var3.inventory.hasItem(Items.arrow)) {
         int var9 = this.getMaxItemUseDuration(var1) - var4;
         float var10 = var9 / 20.0F;
         var10 = (var10 * var10 + var10 * 2.0F) / 3.0F;
         if (var10 < 0.1) {
            return;
         }

         if (var10 > 1.0F) {
            var10 = 1.0F;
         }

         EntityArrow var11 = new EntityArrow(var2, var3, var10 * 2.0F);
         if (var10 == 1.0F) {
            var11.setIsCritical(true);
         }

         int var12 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, var1);
         if (var12 > 0) {
            var11.setDamage(var11.getDamage() + var12 * 0.5 + 0.5);
         }

         int var13 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, var1);
         if (var13 > 0) {
            var11.setKnockbackStrength(var13);
         }

         if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, var1) > 0) {
            var11.setFire(100);
         }

         var1.damageItem(1, var3);
         var2.playSoundAtEntity(var3, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + var10 * 0.5F);
         if (var8) {
            var11.canBePickedUp = 2;
         } else {
            var3.inventory.consumeInventoryItem(Items.arrow);
         }

         var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         if (!var2.isRemote) {
            var2.spawnEntityInWorld(var11);
         }
      }
   }

   @Override
   public ItemStack onItemUseFinish(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   @Override
   public int getMaxItemUseDuration(ItemStack var1) {
      return 72000;
   }

   @Override
   public EnumAction getItemUseAction(ItemStack var1) {
      return EnumAction.BOW;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      if (var3.capabilities.isCreativeMode || var3.inventory.hasItem(Items.arrow)) {
         var3.setItemInUse(var1, this.getMaxItemUseDuration(var1));
      }

      return var1;
   }

   @Override
   public int getItemEnchantability() {
      return 1;
   }
}
