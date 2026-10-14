package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {
   private static final long a = 87270199721378L;

   public ItemBed() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      if (side != EnumFacing.UP) {
         return false;
      }

      IBlockState iBlockState = worldIn.getBlockState(pos);
      Block block = iBlockState.getBlock();
      boolean bl = block.isReplaceable(worldIn, pos);
      if (!bl) {
         pos = pos.up();
      }

      int i = MathHelper.floor_double(playerIn.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
      EnumFacing enumFacing = EnumFacing.getHorizontal(i);
      BlockPos blockPos = pos.offset(enumFacing);
      if (playerIn.canPlayerEdit(pos, side, stack) && playerIn.canPlayerEdit(blockPos, side, stack)) {
         boolean bl2 = worldIn.getBlockState(blockPos).getBlock().isReplaceable(worldIn, blockPos);
         boolean bl3 = bl || worldIn.isAirBlock(pos);
         boolean bl4 = bl2 || worldIn.isAirBlock(blockPos);
         if (bl3 && bl4 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos.down())) {
            IBlockState iBlockState2 = Blocks.bed
               .getDefaultState()
               .withProperty(BlockBed.OCCUPIED, false)
               .withProperty(BlockBed.FACING, enumFacing)
               .withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
            if (worldIn.setBlockState(pos, iBlockState2, 3)) {
               IBlockState iBlockState3 = iBlockState2.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
               worldIn.setBlockState(blockPos, iBlockState3, 3);
            }

            stack.stackSize--;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
