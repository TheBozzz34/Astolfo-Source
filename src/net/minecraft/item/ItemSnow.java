package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlock {
   public ItemSnow(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (stack.stackSize == 0) {
         return false;
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      IBlockState iBlockState = worldIn.getBlockState(pos);
      Block block2 = iBlockState.getBlock();
      BlockPos blockPos = pos;
      if ((side != EnumFacing.UP || block2 != this.block) && !block2.isReplaceable(worldIn, pos)) {
         blockPos = pos.offset(side);
         iBlockState = worldIn.getBlockState(blockPos);
         block2 = iBlockState.getBlock();
      }

      if (block2 == this.block) {
         int i = iBlockState.getValue(BlockSnow.LAYERS);
         if (i <= 7) {
            IBlockState iBlockState2 = iBlockState.withProperty(BlockSnow.LAYERS, i + 1);
            AxisAlignedBB axisAlignedBB = this.block.getCollisionBoundingBox(worldIn, blockPos, iBlockState2);
            if (axisAlignedBB != null && worldIn.checkNoEntityCollision(axisAlignedBB) && worldIn.setBlockState(blockPos, iBlockState2, 2)) {
               worldIn.playSoundEffect(
                  blockPos.getX() + 0.5F,
                  blockPos.getY() + 0.5F,
                  blockPos.getZ() + 0.5F,
                  this.block.stepSound.getPlaceSound(),
                  (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.block.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
               return true;
            }
         }
      }

      return super.onItemUse(stack, playerIn, worldIn, blockPos, side, hitX, hitY, hitZ);
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }
}
