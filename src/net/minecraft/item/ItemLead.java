package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead extends Item {
   private static final long a = 24882230529357L;

   public ItemLead() {
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      Block var12 = var3.getBlockState(var4).getBlock();
      if (var12 instanceof BlockFence) {
         if (var3.isRemote) {
            return true;
         }

         attachToFence(var2, var3, var4);
         return true;
      } else {
         return false;
      }
   }

   public static boolean attachToFence(EntityPlayer var0, World var1, BlockPos var2) {
      EntityLeashKnot var6 = EntityLeashKnot.getKnotForPosition(var1, var2);
      boolean var7 = false;
      double var8 = 7.0;
      int var10 = var2.getX();
      int var11 = var2.getY();
      int var12 = var2.getZ();

      for (EntityLiving var14 : var1.getEntitiesWithinAABB(
         EntityLiving.class, new AxisAlignedBB(var10 - var8, var11 - var8, var12 - var8, var10 + var8, var11 + var8, var12 + var8)
      )) {
         if (var14.getLeashed() && var14.getLeashedToEntity() == var0) {
            if (var6 == null) {
               var6 = EntityLeashKnot.createKnot(var1, var2);
            }

            var14.setLeashedToEntity(var6, true);
            var7 = true;
         }
      }

      return var7;
   }
}
