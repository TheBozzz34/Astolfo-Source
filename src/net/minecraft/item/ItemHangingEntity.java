package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class<? extends EntityHanging> hangingEntityClass;

   public ItemHangingEntity(Class<? extends EntityHanging> entityClass) {
      this.hangingEntityClass = entityClass;
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      if (side == EnumFacing.UP) {
         return false;
      }

      BlockPos blockPos = pos.offset(side);
      if (!playerIn.canPlayerEdit(blockPos, side, stack)) {
         return false;
      }

      EntityHanging entityHanging = this.createEntity(worldIn, blockPos, side);
      if (entityHanging != null && entityHanging.onValidSurface()) {
         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(entityHanging);
         }

         stack.stackSize--;
      }

      return true;
   }

   private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide) {
      return this.hangingEntityClass == EntityPainting.class
         ? new EntityPainting(worldIn, pos, clickedSide)
         : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(worldIn, pos, clickedSide) : null);
   }
}
