package net.minecraft.item;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {
   public ItemEnderEye() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack) || iBlockState.getBlock() != Blocks.end_portal_frame || iBlockState.getValue(BlockEndPortalFrame.EYE)) {
         return false;
      }

      if (worldIn.isRemote) {
         return true;
      }

      worldIn.setBlockState(pos, iBlockState.withProperty(BlockEndPortalFrame.EYE, true), 2);
      worldIn.updateComparatorOutputLevel(pos, Blocks.end_portal_frame);
      stack.stackSize--;

      for (int i = 0; i < 16; i++) {
         double d = pos.getX() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double e = pos.getY() + 0.8125F;
         double d2 = pos.getZ() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double d3 = 0.0;
         double d4 = 0.0;
         double d5 = 0.0;
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, e, d2, d3, d4, d5);
      }

      EnumFacing enumFacing = iBlockState.getValue(BlockEndPortalFrame.FACING);
      int j = 0;
      int k = 0;
      boolean bl = false;
      boolean bl2 = true;
      EnumFacing enumFacing2 = enumFacing.rotateY();

      for (int l = -2; l <= 2; l++) {
         BlockPos blockPos = pos.offset(enumFacing2, l);
         IBlockState iBlockState2 = worldIn.getBlockState(blockPos);
         if (iBlockState2.getBlock() == Blocks.end_portal_frame) {
            if (!iBlockState2.getValue(BlockEndPortalFrame.EYE)) {
               bl2 = false;
               break;
            }

            k = l;
            if (!bl) {
               j = l;
               bl = true;
            }
         }
      }

      if (bl2 && k == j + 2) {
         BlockPos blockPos2 = pos.offset(enumFacing, 4);

         for (int m = j; m <= k; m++) {
            BlockPos blockPos3 = blockPos2.offset(enumFacing2, m);
            IBlockState iBlockState3 = worldIn.getBlockState(blockPos3);
            if (iBlockState3.getBlock() != Blocks.end_portal_frame || !iBlockState3.getValue(BlockEndPortalFrame.EYE)) {
               bl2 = false;
               break;
            }
         }

         for (int n = j - 1; n <= k + 1; n += 4) {
            blockPos2 = pos.offset(enumFacing2, n);

            for (int i2 = 1; i2 <= 3; i2++) {
               BlockPos blockPos4 = blockPos2.offset(enumFacing, i2);
               IBlockState iBlockState4 = worldIn.getBlockState(blockPos4);
               if (iBlockState4.getBlock() != Blocks.end_portal_frame || !iBlockState4.getValue(BlockEndPortalFrame.EYE)) {
                  bl2 = false;
                  break;
               }
            }
         }

         if (bl2) {
            for (int i3 = j; i3 <= k; i3++) {
               blockPos2 = pos.offset(enumFacing2, i3);

               for (int i4 = 1; i4 <= 3; i4++) {
                  BlockPos blockPos5 = blockPos2.offset(enumFacing, i4);
                  worldIn.setBlockState(blockPos5, Blocks.end_portal.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, false);
      if (movingObjectPosition != null
         && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
         && worldIn.getBlockState(movingObjectPosition.getBlockPos()).getBlock() == Blocks.end_portal_frame) {
         return itemStackIn;
      }

      if (!worldIn.isRemote) {
         BlockPos blockPos = worldIn.getStrongholdPos("Stronghold", new BlockPos(playerIn));
         if (blockPos != null) {
            EntityEnderEye entityEnderEye = new EntityEnderEye(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ);
            entityEnderEye.moveTowards(blockPos);
            worldIn.spawnEntityInWorld(entityEnderEye);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1002, new BlockPos(playerIn), 0);
            if (!playerIn.capabilities.isCreativeMode) {
               itemStackIn.stackSize--;
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         }
      }

      return itemStackIn;
   }
}
