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
   public ItemLead() {
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      Block block = worldIn.getBlockState(pos).getBlock();
      if (block instanceof BlockFence) {
         if (worldIn.isRemote) {
            return true;
         }

         attachToFence(playerIn, worldIn, pos);
         return true;
      } else {
         return false;
      }
   }

   public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence) {
      EntityLeashKnot entityLeashKnot = EntityLeashKnot.getKnotForPosition(worldIn, fence);
      boolean bl = false;
      double d = 7.0;
      int i = fence.getX();
      int j = fence.getY();
      int k = fence.getZ();

      for (EntityLiving entityLiving : worldIn.getEntitiesWithinAABB(
         EntityLiving.class, new AxisAlignedBB(i - d, j - d, k - d, i + d, j + d, k + d)
      )) {
         if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == player) {
            if (entityLeashKnot == null) {
               entityLeashKnot = EntityLeashKnot.createKnot(worldIn, fence);
            }

            entityLiving.setLeashedToEntity(entityLeashKnot, true);
            bl = true;
         }
      }

      return bl;
   }
}
