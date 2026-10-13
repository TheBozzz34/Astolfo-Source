package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {
   private static final long a = 216027092150L;

   public ItemCarrotOnAStick() {
      this.setCreativeTab(CreativeTabs.tabTransport);
      this.setMaxStackSize(1);
      this.setMaxDamage(25);
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
      if (var3.E() && var3.ridingEntity instanceof EntityPig) {
         EntityPig var7 = (EntityPig)var3.ridingEntity;
         if (var7.getAIControlledByPlayer().isControlledByPlayer() && var1.getMaxDamage() - var1.getMetadata() >= 7) {
            var7.getAIControlledByPlayer().boostSpeed();
            var1.damageItem(7, var3);
            if (var1.stackSize == 0) {
               ItemStack var8 = new ItemStack(Items.fishing_rod);
               var8.setTagCompound(var1.getTagCompound());
               return var8;
            }
         }
      }

      var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return var1;
   }
}
