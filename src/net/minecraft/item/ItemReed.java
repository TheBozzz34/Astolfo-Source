package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemReed extends Item {
   private Block block;

   public ItemReed(Block block) {
      this.block = block;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      Block block2 = iBlockState.getBlock();
      if (block2 == Blocks.snow_layer && iBlockState.getValue(BlockSnow.LAYERS) < 1) {
         side = EnumFacing.UP;
      } else if (!block2.isReplaceable(worldIn, pos)) {
         pos = pos.offset(side);
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (stack.stackSize == 0) {
         return false;
      }

      if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack)) {
         IBlockState iBlockState2 = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
         if (worldIn.setBlockState(pos, iBlockState2, 3)) {
            iBlockState2 = worldIn.getBlockState(pos);
            if (iBlockState2.getBlock() == this.block) {
               ItemBlock.setTileEntityNBT(worldIn, playerIn, pos, stack);
               iBlockState2.getBlock().onBlockPlacedBy(worldIn, pos, iBlockState2, playerIn, stack);
            }

            worldIn.playSoundEffect(
               pos.getX() + 0.5F,
               pos.getY() + 0.5F,
               pos.getZ() + 0.5F,
               this.block.stepSound.getPlaceSound(),
               (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
               this.block.stepSound.getFrequency() * 0.8F
            );
            stack.stackSize--;
            return true;
         }
      }

      return false;
   }
}
