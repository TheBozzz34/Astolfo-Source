package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockHay extends BlockRotatedPillar {
   public BlockHay() {
      super(Material.grass, MapColor.yellowColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing.Axis axis = EnumFacing.Axis.Y;
      int i = meta & 12;
      if (i == 4) {
         axis = EnumFacing.Axis.X;
      } else if (i == 8) {
         axis = EnumFacing.Axis.Z;
      }

      return this.getDefaultState().withProperty(AXIS, axis);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      byte b = 0;
      EnumFacing.Axis axis = state.getValue(AXIS);
      if (axis == EnumFacing.Axis.X) {
         b |= 4;
      } else if (axis == EnumFacing.Axis.Z) {
         b |= 8;
      }

      return b;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AXIS);
   }

   @Override
   public ItemStack createStackedBlock(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this), 1, 0);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
   }
}
