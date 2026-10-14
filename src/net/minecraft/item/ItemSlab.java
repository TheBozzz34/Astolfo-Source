package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
   private final BlockSlab singleSlab;
   private final BlockSlab doubleSlab;

   public ItemSlab(Block block, BlockSlab singleSlab, BlockSlab doubleSlab) {
      super(block);
      this.singleSlab = singleSlab;
      this.doubleSlab = doubleSlab;
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return this.singleSlab.getUnlocalizedName(stack.getMetadata());
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (stack.stackSize == 0) {
         return false;
      }

      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return false;
      }

      Object object = this.singleSlab.getVariant(stack);
      IBlockState iBlockState = worldIn.getBlockState(pos);
      if (iBlockState.getBlock() == this.singleSlab) {
         IProperty iProperty = this.singleSlab.getVariantProperty();
         Comparable comparable = iBlockState.getValue(iProperty);
         BlockSlab.EnumBlockHalf enumBlockHalf = iBlockState.getValue(BlockSlab.HALF);
         if ((side == EnumFacing.UP && enumBlockHalf == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && enumBlockHalf == BlockSlab.EnumBlockHalf.TOP)
            && comparable == object) {
            IBlockState iBlockState2 = this.doubleSlab.getDefaultState().withProperty(iProperty, comparable);
            if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, iBlockState2)) && worldIn.setBlockState(pos, iBlockState2, 3)) {
               worldIn.playSoundEffect(
                  pos.getX() + 0.5F,
                  pos.getY() + 0.5F,
                  pos.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
            }

            return true;
         }
      }

      return this.tryPlace(stack, worldIn, pos.offset(side), object) ? true : super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
      BlockPos blockPos = pos;
      IProperty iProperty = this.singleSlab.getVariantProperty();
      Object object = this.singleSlab.getVariant(stack);
      IBlockState iBlockState = worldIn.getBlockState(pos);
      if (iBlockState.getBlock() == this.singleSlab) {
         boolean bl = iBlockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;
         if ((side == EnumFacing.UP && !bl || side == EnumFacing.DOWN && bl) && object == iBlockState.getValue(iProperty)) {
            return true;
         }
      }

      pos = pos.offset(side);
      IBlockState iBlockState2 = worldIn.getBlockState(pos);
      return iBlockState2.getBlock() == this.singleSlab && object == iBlockState2.getValue(iProperty) ? true : super.canPlaceBlockOnSide(worldIn, blockPos, side, player, stack);
   }

   private boolean tryPlace(ItemStack stack, World worldIn, BlockPos pos, Object variantInStack) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      if (iBlockState.getBlock() == this.singleSlab) {
         Comparable comparable = iBlockState.getValue((IProperty)this.singleSlab.getVariantProperty());
         if (comparable == variantInStack) {
            IBlockState iBlockState2 = this.doubleSlab.getDefaultState().withProperty((net.minecraft.block.properties.IProperty)(this.singleSlab.getVariantProperty()), comparable);
            if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, iBlockState2)) && worldIn.setBlockState(pos, iBlockState2, 3)) {
               worldIn.playSoundEffect(
                  pos.getX() + 0.5F,
                  pos.getY() + 0.5F,
                  pos.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
            }

            return true;
         }
      }

      return false;
   }
}
