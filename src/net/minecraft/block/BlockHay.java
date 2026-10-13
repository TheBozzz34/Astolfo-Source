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
   private static final long e = 59958815666898L;

   public BlockHay() {
      super(Material.grass, MapColor.yellowColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      EnumFacing.Axis var5 = EnumFacing.Axis.Y;
      int var6 = var1 & 12;
      if (var6 == 4) {
         var5 = EnumFacing.Axis.X;
      } else if (var6 == 8) {
         var5 = EnumFacing.Axis.Z;
      }

      return this.getDefaultState().withProperty(AXIS, var5);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      byte var5 = 0;
      EnumFacing.Axis var6 = var1.getValue(AXIS);
      if (var6 == EnumFacing.Axis.X) {
         var5 |= 4;
      } else if (var6 == EnumFacing.Axis.Z) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AXIS);
   }

   @Override
   public ItemStack createStackedBlock(IBlockState var1) {
      return new ItemStack(Item.getItemFromBlock(this), 1, 0);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return super.onBlockPlaced(var1, var2, var3, var4, var5, var6, var7, var8).withProperty(AXIS, var3.getAxis());
   }
}
