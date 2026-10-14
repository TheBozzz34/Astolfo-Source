package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {
   private static final long a = 135655707212666L;

   public ItemGlassBottle() {
      this.setCreativeTab(CreativeTabs.tabBrewing);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);
      if (movingObjectPosition == null) {
         return itemStackIn;
      }

      if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         if (!worldIn.isBlockModifiable(playerIn, blockPos)) {
            return itemStackIn;
         }

         if (!playerIn.canPlayerEdit(blockPos.offset(movingObjectPosition.sideHit), movingObjectPosition.sideHit, itemStackIn)) {
            return itemStackIn;
         }

         if (worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.water) {
            itemStackIn.stackSize--;
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            if (itemStackIn.stackSize <= 0) {
               return new ItemStack(Items.potionitem);
            }

            if (!playerIn.inventory.addItemStackToInventory(new ItemStack(Items.potionitem))) {
               playerIn.dropPlayerItemWithRandomChoice(new ItemStack(Items.potionitem, 1, 0), false);
            }
         }
      }

      return itemStackIn;
   }
}
