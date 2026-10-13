package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockWeb extends Block {
   public BlockWeb() {
      super(Material.web);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      var4.setInWeb();
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.string;
   }

   @Override
   public boolean canSilkHarvest() {
      return true;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
