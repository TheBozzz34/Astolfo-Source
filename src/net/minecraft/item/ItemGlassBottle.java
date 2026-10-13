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
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var7 = this.getMovingObjectPositionFromPlayer(var2, var3, true);
      if (var7 == null) {
         return var1;
      }

      if (var7.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos var8 = var7.getBlockPos();
         if (!var2.isBlockModifiable(var3, var8)) {
            return var1;
         }

         if (!var3.canPlayerEdit(var8.offset(var7.sideHit), var7.sideHit, var1)) {
            return var1;
         }

         if (var2.getBlockState(var8).getBlock().getMaterial() == Material.water) {
            var1.stackSize--;
            var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            if (var1.stackSize <= 0) {
               return new ItemStack(Items.potionitem);
            }

            if (!var3.inventory.addItemStackToInventory(new ItemStack(Items.potionitem))) {
               var3.dropPlayerItemWithRandomChoice(new ItemStack(Items.potionitem, 1, 0), false);
            }
         }
      }

      return var1;
   }
}
