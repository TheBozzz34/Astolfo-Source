package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReed extends Block {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
   private static final long e = 130121991611931L;

   public BlockReed() {
      super(Material.plants);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      float var1 = 0.375F;
      this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 1.0F, 0.5F + var1);
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if ((var1.getBlockState(var2.down()).getBlock() == Blocks.reeds || this.checkForDrop(var1, var2, var3)) && var1.isAirBlock(var2.up())) {
         int var8 = 1;

         while (var1.getBlockState(var2.down(var8)).getBlock() == this) {
            var8++;
         }

         if (var8 < 3) {
            int var9 = var3.getValue(AGE);
            if (var9 == 15) {
               var1.setBlockState(var2.up(), this.getDefaultState());
               var1.setBlockState(var2, var3.withProperty(AGE, 0), 4);
            } else {
               var1.setBlockState(var2, var3.withProperty(AGE, var9 + 1), 4);
            }
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      Block var6 = var1.getBlockState(var2.down()).getBlock();
      if (var6 == this) {
         return true;
      }

      if (var6 != Blocks.grass && var6 != Blocks.dirt && var6 != Blocks.sand) {
         return false;
      }

      for (Object var8 : EnumFacing.Plane.HORIZONTAL) {
         if (var1.getBlockState(var2.offset((EnumFacing)var8).down()).getBlock().getMaterial() == Material.water) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.checkForDrop(var1, var2, var3);
   }

   public final boolean checkForDrop(World var1, BlockPos var2, IBlockState var3) {
      if (this.canBlockStay(var1, var2)) {
         return true;
      }

      this.dropBlockAsItem(var1, var2, var3, 0);
      var1.setBlockToAir(var2);
      return false;
   }

   public boolean canBlockStay(World var1, BlockPos var2) {
      return this.canPlaceBlockAt(var1, var2);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.reeds;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.reeds;
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return var1.getBiomeGenForCoords(var2).getGrassColorAtPos(var2);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(AGE, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE);
   }
}
