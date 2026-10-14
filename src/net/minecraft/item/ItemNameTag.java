package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item {
   private static final long a = 28123043722635L;

   public ItemNameTag() {
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @Override
   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target) {
      if (!stack.hasDisplayName()) {
         return false;
      } else if (target instanceof EntityLiving) {
         EntityLiving entityLiving = (EntityLiving)target;
         entityLiving.setCustomNameTag(stack.getDisplayName());
         entityLiving.enablePersistence();
         stack.stackSize--;
         return true;
      } else {
         return super.itemInteractionForEntity(stack, playerIn, target);
      }
   }
}
