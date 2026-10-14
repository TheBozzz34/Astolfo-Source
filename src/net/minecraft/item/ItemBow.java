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

   public ItemBow() {
      this.maxStackSize = 1;
      this.setMaxDamage(384);
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   @Override
   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
      boolean bl = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
      if (bl || playerIn.inventory.hasItem(Items.arrow)) {
         int i = this.getMaxItemUseDuration(stack) - timeLeft;
         float f = i / 20.0F;
         f = (f * f + f * 2.0F) / 3.0F;
         if (f < 0.1) {
            return;
         }

         if (f > 1.0F) {
            f = 1.0F;
         }

         EntityArrow entityArrow = new EntityArrow(worldIn, playerIn, f * 2.0F);
         if (f == 1.0F) {
            entityArrow.setIsCritical(true);
         }

         int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
         if (j > 0) {
            entityArrow.setDamage(entityArrow.getDamage() + j * 0.5 + 0.5);
         }

         int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
         if (k > 0) {
            entityArrow.setKnockbackStrength(k);
         }

         if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
            entityArrow.setFire(100);
         }

         stack.damageItem(1, playerIn);
         worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
         if (bl) {
            entityArrow.canBePickedUp = 2;
         } else {
            playerIn.inventory.consumeInventoryItem(Items.arrow);
         }

         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(entityArrow);
         }
      }
   }

   @Override
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      return stack;
   }

   @Override
   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   @Override
   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(Items.arrow)) {
         playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
      }

      return itemStackIn;
   }

   @Override
   public int getItemEnchantability() {
      return 1;
   }
}
