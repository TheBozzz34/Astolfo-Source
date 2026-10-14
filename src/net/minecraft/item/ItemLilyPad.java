package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored {
   private static final long d = 78110912262362L;

   public ItemLilyPad(Block block) {
      super(block, false);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);
      if (movingObjectPosition == null) {
         return itemStackIn;
      }

      if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         if (!worldIn.isBlockModifiable(playerIn, blockPos)) {
            return itemStackIn;
         }

         if (!playerIn.canPlayerEdit(blockPos.offset(movingObjectPosition.sideHit), movingObjectPosition.sideHit, itemStackIn)) {
            return itemStackIn;
         }

         BlockPos blockPos2 = blockPos.up();
         IBlockState iBlockState = worldIn.getBlockState(blockPos);
         if (iBlockState.getBlock().getMaterial() == Material.water && iBlockState.getValue(BlockLiquid.LEVEL) == 0 && worldIn.isAirBlock(blockPos2)) {
            worldIn.setBlockState(blockPos2, Blocks.waterlily.getDefaultState());
            if (!playerIn.capabilities.isCreativeMode) {
               itemStackIn.stackSize--;
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         }
      }

      return itemStackIn;
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      return Blocks.waterlily.getRenderColor(Blocks.waterlily.getStateFromMeta(stack.getMetadata()));
   }
}
