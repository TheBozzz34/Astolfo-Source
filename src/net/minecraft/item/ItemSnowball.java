package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemSnowball extends Item {
   private static final long a = 69575560901943L;
   private static final long d = -7899574751570427888L;

   public ItemSnowball() {
      this.maxStackSize = (int)d;
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (!playerIn.capabilities.isCreativeMode) {
         itemStackIn.stackSize--;
      }

      worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         worldIn.spawnEntityInWorld(new EntitySnowball(worldIn, playerIn));
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }
}
