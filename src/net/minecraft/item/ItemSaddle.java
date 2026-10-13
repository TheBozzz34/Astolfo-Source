package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item {
   private static final long a = 50088521763870L;

   public ItemSaddle() {
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   @Override
   public boolean itemInteractionForEntity(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      if (var3 instanceof EntityPig) {
         EntityPig var7 = (EntityPig)var3;
         if (!var7.getSaddled() && !var7.isChild()) {
            var7.setSaddled(true);
            var7.worldObj.playSoundAtEntity(var7, "mob.horse.leather", 0.5F, 1.0F);
            var1.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean hitEntity(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      this.itemInteractionForEntity(var1, (EntityPlayer)null, var2);
      return true;
   }
}
