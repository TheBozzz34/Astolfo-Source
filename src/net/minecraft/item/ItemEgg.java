package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemEgg extends Item {
   private static final long a = 129809316842007L;
   private static final long d = 5746317284545134608L;

   public ItemEgg() {
      this.maxStackSize = (int)d;
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (!playerIn.capabilities.isCreativeMode) {
         itemStackIn.stackSize--;
      }

      worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         worldIn.spawnEntityInWorld(new EntityEgg(worldIn, playerIn));
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }
}
