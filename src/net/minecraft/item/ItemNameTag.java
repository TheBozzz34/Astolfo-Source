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
   public boolean itemInteractionForEntity(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      if (!var1.hasDisplayName()) {
         return false;
      } else if (var3 instanceof EntityLiving) {
         EntityLiving var7 = (EntityLiving)var3;
         var7.setCustomNameTag(var1.getDisplayName());
         var7.enablePersistence();
         var1.stackSize--;
         return true;
      } else {
         return super.itemInteractionForEntity(var1, var2, var3);
      }
   }
}
