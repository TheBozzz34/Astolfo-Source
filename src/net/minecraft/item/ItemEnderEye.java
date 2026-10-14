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
   private static final long a = 101523873154701L;

   public ItemEnderEye() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      long l = a ^ 92882501463191L;
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
      long m = 4635024364000137736L ^ l;
      int k = 0;
      boolean bl = false;
      boolean bl2 = true;
      EnumFacing enumFacing2 = enumFacing.rotateY();

      for (int n = -2; n <= 2; n++) {
         BlockPos blockPos = pos.offset(enumFacing2, n);
         IBlockState iBlockState2 = worldIn.getBlockState(blockPos);
         if (iBlockState2.getBlock() == Blocks.end_portal_frame) {
            if (!iBlockState2.getValue(BlockEndPortalFrame.EYE)) {
               bl2 = false;
               break;
            }

            k = n;
            if (!bl) {
               j = n;
               bl = true;
            }
         }
      }

      if (bl2 && k == j + 2) {
         BlockPos blockPos2 = pos.offset(enumFacing, 4);

         for (int i2 = j; i2 <= k; i2++) {
            BlockPos blockPos3 = blockPos2.offset(enumFacing2, i2);
            IBlockState iBlockState3 = worldIn.getBlockState(blockPos3);
            if (iBlockState3.getBlock() != Blocks.end_portal_frame || !iBlockState3.getValue(BlockEndPortalFrame.EYE)) {
               bl2 = false;
               break;
            }
         }

         for (int i3 = j - 1; i3 <= k + 1; i3 += 4) {
            blockPos2 = pos.offset(enumFacing2, i3);

            for (int i4 = 1; i4 <= 3; i4++) {
               BlockPos blockPos4 = blockPos2.offset(enumFacing, i4);
               IBlockState iBlockState4 = worldIn.getBlockState(blockPos4);
               if (iBlockState4.getBlock() != Blocks.end_portal_frame || !iBlockState4.getValue(BlockEndPortalFrame.EYE)) {
                  bl2 = false;
                  break;
               }
            }
         }

         if (bl2) {
            for (int i5 = j; i5 <= k; i5++) {
               blockPos2 = pos.offset(enumFacing2, i5);

               for (int i6 = 1; i6 <= 3; i6++) {
                  BlockPos blockPos5 = blockPos2.offset(enumFacing, i6);
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
